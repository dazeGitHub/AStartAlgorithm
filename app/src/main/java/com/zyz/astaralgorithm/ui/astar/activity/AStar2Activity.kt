package com.zyz.astaralgorithm.ui.astar.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.zyz.astaralgorithm.R
import com.zyz.astaralgorithm.bean.NodeBean
import com.zyz.astaralgorithm.bean.ReachState
import com.zyz.astaralgorithm.ui.astar.adapter.AStarAdapter
import com.zyz.astaralgorithm.utils.AStarUtils
import com.zyz.astaralgorithm.utils.PathUtils
import com.zyz.astaralgorithm.utils.decoration.GridItemDecoration
import kotlinx.android.synthetic.main.activity_a_star2.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ticker

/**
 * 实时更新画面（实时寻路），并通过上下左右控制 目标点
 */
class AStar2Activity : AppCompatActivity() {
    private val TAG = AStar2Activity::class.java.name

    private var mStartPos = 0
    private var mEndPos = 0
    private val mRepeatTime = 100                            //重复次数 100 次最多了
    private lateinit var mNodeAdapter: AStarAdapter
    private var mNextStepReachState = ReachState.NOT_FIND    // 0 未到达 1 已到达 2 此路不通
    private var mAStarUtils: AStarUtils = AStarUtils()
    private val mGameLooperJob = Job()                       //用来取消协程

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a_star2)

        initUI(initData())
    }

    private fun initData(): MutableList<NodeBean> {
        val triple: Triple<Int, Int, MutableList<NodeBean>> = PathUtils.initPath()
        mStartPos = triple.first
        mEndPos = triple.second
        println(String.format("startPos index: %d, endPos index: %d", mStartPos, mEndPos))

        mAStarUtils.initNodeList(triple.third)
        return triple.third
    }

    private fun initUI(list: MutableList<NodeBean>) {
        if (mAStarUtils.isDiagonal) {
            btn_walk_type.text = "Diagonal"                 //[daɪˈæɡənl] 对角线的
        } else {
            btn_walk_type.text = "Straight"                 //[streɪt] 直的
        }
        initRecy(list)
        initListener()
    }

    private fun initRecy(list: MutableList<NodeBean>) {
        mNodeAdapter = AStarAdapter(list)
        rv_path_node.setAdapter(mNodeAdapter)
        rv_path_node.setLayoutManager(GridLayoutManager(this, 10))
        rv_path_node.addItemDecoration(
            GridItemDecoration.Builder(this)
                .setHead(false)
                .setHor(true)
                .setDrawFirstTopLine(false)
                .color(resources?.getColor(R.color.code_black)!!)
                .size(resources?.getDimension(R.dimen.dp_1)!!.toInt()).build()
        )

        mNodeAdapter.setOnItemClickListener { adapter, view, position ->
            val tempBean = (adapter.getItem(position) as NodeBean)
            if (tempBean.reachSate == ReachState.NOT_ALLOW_GO) {
                tempBean.reachSate = ReachState.NOT_FIND
                adapter.notifyItemChanged(position)
            } else if (tempBean.reachSate == ReachState.NOT_FIND) {
                tempBean.reachSate = ReachState.NOT_ALLOW_GO
                adapter.notifyItemChanged(position)
            }
        }
    }

    private fun initListener() {
        //点击设置直走还是斜走
        btn_walk_type.setOnClickListener {
            settingMode()
        }

        btn_next.setOnClickListener {
            goNext()
        }

        btn_next.setOnLongClickListener {
            goNextTotalPath()
            true
        }

        btn_reset.setOnClickListener {
            reset()
        }

        btn_start_auto_next.setOnClickListener{
            initGameLooper()
        }

        btn_close_auto_next.setOnClickListener{
            closeGameLooper()
        }

        btn_up.setOnClickListener{
            val tempEndPos =
            mNodeAdapter.data[mEndPos]

        }

        btn_down.setOnClickListener{

        }

        btn_left.setOnClickListener{

        }

        btn_right.setOnClickListener{

        }
    }

    private fun initGameLooper() { //开启游戏循环，实时更新画面
        val tickerChannel = ticker(delayMillis = 1_000, initialDelayMillis = 0)     //每 1s 执行一次
        val uiScope = CoroutineScope(Dispatchers.Main + mGameLooperJob)     //初始化 CoroutineScope 指定协程的运行所在线程传入 Job 方便后面取消协程
        val tempJob:Job = uiScope.launch {                                          //启动一个协程
            repeat(mRepeatTime) {
                val receiveValue = tickerChannel.receive()
                Log.d(TAG, "initGameLooper repeat receiveValue =$receiveValue , CurrentThread =" + Thread.currentThread().name)
                goNext()
            }
        }
    }

    private fun closeGameLooper(){                           //关闭自动寻路
        mGameLooperJob.cancel()                              //关闭协程
        Log.d(TAG,"关闭了自动寻路")
    }

    private fun settingMode(){
        if (mAStarUtils.isDiagonal) {
            mAStarUtils.isDiagonal = false
            btn_walk_type.text = "Straight"
        } else {
            mAStarUtils.isDiagonal = true
            btn_walk_type.text = "Diagonal"
        }
    }

    private fun goNext(){
        if (mNextStepReachState == ReachState.FIND_BUT_NOT_GO) {
            ToastUtils.showShort("Has Reach End, Auto closeGameLooper !")
            mNodeAdapter.refreshPath(mAStarUtils.getNodeList()!!)
            closeGameLooper()
            return
        } else if (mNextStepReachState == ReachState.FIND_AND_GO) {
            ToastUtils.showShort("No Road, Auto closeGameLooper !")
            closeGameLooper()
            return
        }
        mNextStepReachState =
            mAStarUtils.nextStep(mNodeAdapter.data[mStartPos], mNodeAdapter.data[mEndPos])
        mNodeAdapter.refreshPath(mAStarUtils.getNodeList()!!)
    }

    private fun goNextTotalPath(){
        val pathList =
            mAStarUtils.findPath(mNodeAdapter.data[mStartPos], mNodeAdapter.data[mEndPos])
        mNodeAdapter.refreshPath(mAStarUtils.getNodeList()!!)
    }

    private fun reset(){
        mNextStepReachState = ReachState.NOT_FIND
        mAStarUtils.reset()
        mNodeAdapter.refreshPath(initData())
    }

    override fun onDestroy() {
        super.onDestroy()
        mAStarUtils.reset()
    }
}