package com.zyz.astaralgorithm.ui.astar.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.zyz.astaralgorithm.R
import com.zyz.astaralgorithm.bean.NodeBean
import com.zyz.astaralgorithm.bean.ReachState
import com.zyz.astaralgorithm.ui.astar.adapter.AStarAdapter
import com.zyz.astaralgorithm.utils.AstarUtils
import com.zyz.astaralgorithm.utils.Tools
import com.zyz.astaralgorithm.utils.decoration.GridItemDecoration
import kotlinx.android.synthetic.main.activity_a_start.*
import java.util.*

class AStarActivity : AppCompatActivity() {

    private var nodeList: MutableList<NodeBean> = ArrayList()
    private lateinit var nodeAdapter: AStarAdapter
    private var start = 0
    private var end = 0
    private var reachState = ReachState.NOT_FIND    // 0 未到达 1 已到达 2 此路不通
            
    private var astarUtils: AstarUtils = AstarUtils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a_start)

        nodeAdapter = AStarAdapter(initPath())
        rv_path_node.setAdapter(nodeAdapter)
        rv_path_node.setLayoutManager(GridLayoutManager(this, 10))
        rv_path_node.addItemDecoration(
            GridItemDecoration.Builder(this)
                .setHead(false)
                .setHor(true)
                .setDrawFirstTopLine(false)
                .color(resources?.getColor(R.color.code_black)!!)
                .size(resources?.getDimension(R.dimen.dp_1)!!.toInt()).build()
        )

        nodeAdapter.setOnItemClickListener { adapter, view, position ->
            val tempBean = (adapter.getItem(position) as NodeBean)
            if (tempBean.reachSate == ReachState.NOT_ALLOW_GO) {
                tempBean.reachSate = ReachState.NOT_FIND
                adapter.notifyItemChanged(position)
            } else if (tempBean.reachSate ==  ReachState.NOT_FIND) {
                tempBean.reachSate = ReachState.NOT_ALLOW_GO
                adapter.notifyItemChanged(position)
            }
        }

        if (astarUtils.isDiagonal) {
            btn_walk_type.text = "Diagonal"     //[daɪˈæɡənl] 对角线的
        } else {
            btn_walk_type.text = "Straight"     //[streɪt] 直的
        }

        //点击设置直走还是斜走
        btn_walk_type.setOnClickListener{
            if (astarUtils.isDiagonal()) {
                astarUtils.setIsDiagonal(false)
                btn_walk_type.text = "Straight"
            } else {
                astarUtils.setIsDiagonal(true)
                btn_walk_type.text = "Diagonal"
            }
        }

        btn_next.setOnClickListener{
            if (reachState == ReachState.FIND_BUT_NOT_GO) {
                ToastUtils.showShort("Has Reach End")
                nodeAdapter.refreshPath(astarUtils.getNodeList())
            } else if (reachState == ReachState.FIND_AND_GO) {
                ToastUtils.showShort("No Road")
            }
            reachState = astarUtils.nextStep(nodeList, nodeList!![start], nodeList[end])
            nodeAdapter.refreshPath(astarUtils.getNodeList())
        }

        btn_next.setOnLongClickListener{
            val pathList = astarUtils.findPath(nodeList, nodeList!![start], nodeList[end])
            nodeAdapter.refreshPath(astarUtils.getNodeList())
             true
        }

        btn_reset.setOnClickListener{
            reachState = ReachState.NOT_FIND
            astarUtils.reset()
            nodeAdapter.refreshPath(initPath())
        }
    }


    private fun initPath(): MutableList<NodeBean> {
        for (i in 0..99) {
            val node = NodeBean()
            node.pos = Tools.index2pos(i, 10)
            node.index = i
            node.reachSate = ReachState.NOT_FIND
            nodeList.add(node)
        }

        // 障碍
        val random = Random()
        for (i in 0..29) {
            nodeList.get(random.nextInt(100)).reachSate = ReachState.NOT_ALLOW_GO
        }

        // 起点和终点
        start = random.nextInt(100)
        end = random.nextInt(100)
        nodeList.get(start).reachSate = ReachState.FIND_AND_GO
        nodeList.get(start).isStart = true
        nodeList.get(end).reachSate =  ReachState.DESTINATION
        nodeList.get(end).isEnd = true
        println(String.format("startPos index:%d", start))
        return nodeList
    }

    override fun onDestroy() {
        super.onDestroy()
        astarUtils.reset()
    }
}