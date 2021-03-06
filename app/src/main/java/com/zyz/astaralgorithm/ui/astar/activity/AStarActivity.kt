package com.zyz.astaralgorithm.ui.astar.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.zyz.astaralgorithm.R
import com.zyz.astaralgorithm.bean.NodeBean
import com.zyz.astaralgorithm.bean.ReachState
import com.zyz.astaralgorithm.ui.astar.adapter.AStarAdapter
import com.zyz.astaralgorithm.utils.AStarUtils
import com.zyz.astaralgorithm.utils.PathUtils
import com.zyz.astaralgorithm.utils.decoration.GridItemDecoration
import kotlinx.android.synthetic.main.activity_a_star.*

/**
 * 参考: https://gitee.com/qiaogaojian/AndroidTest
 */
open class AStarActivity : AppCompatActivity() {

    private var mStartPos = 0
    private var mEndPos = 0
    private lateinit var mNodeAdapter: AStarAdapter
    private var mNextStepReachState = ReachState.NOT_FIND    // 0 未到达 1 已到达 2 此路不通
    private var mAStarUtils: AStarUtils =
        AStarUtils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a_star)

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
            btn_walk_type.text = "Diagonal"     //[daɪˈæɡənl] 对角线的
        } else {
            btn_walk_type.text = "Straight"     //[streɪt] 直的
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
            if (mAStarUtils.isDiagonal) {
                mAStarUtils.isDiagonal = false
                btn_walk_type.text = "Straight"
            } else {
                mAStarUtils.isDiagonal = true
                btn_walk_type.text = "Diagonal"
            }
        }

        btn_next.setOnClickListener {
            if (mNextStepReachState == ReachState.FIND_BUT_NOT_GO) {
                ToastUtils.showShort("Has Reach End")
                mNodeAdapter.refreshPath(mAStarUtils.getNodeList()!!)
            } else if (mNextStepReachState == ReachState.FIND_AND_GO) {
                ToastUtils.showShort("No Road")
            }
            mNextStepReachState =
                mAStarUtils.nextStep(mNodeAdapter.data[mStartPos], mNodeAdapter.data[mEndPos])
            mNodeAdapter.refreshPath(mAStarUtils.getNodeList()!!)
        }

        btn_next.setOnLongClickListener {
            val pathList =
                mAStarUtils.findPath(mNodeAdapter.data[mStartPos], mNodeAdapter.data[mEndPos])
            mNodeAdapter.refreshPath(mAStarUtils.getNodeList()!!)
            true
        }

        btn_reset.setOnClickListener {
            mNextStepReachState = ReachState.NOT_FIND
            mAStarUtils.reset()
            mNodeAdapter.refreshPath(initData())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mAStarUtils.reset()
    }
}