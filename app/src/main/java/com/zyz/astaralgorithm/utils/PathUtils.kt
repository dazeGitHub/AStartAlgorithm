package com.zyz.astaralgorithm.utils

import com.zyz.astaralgorithm.bean.NodeBean
import com.zyz.astaralgorithm.bean.ReachState
import java.util.*

/**
 * <pre>
 *     author : ZYZ
 *     e-mail : zyz163mail@163.com
 *     time   : 2020/09/11
 *     desc   :
 *     version: 1.0
 * </pre>
 */

object PathUtils {

     fun initPath(): Triple<Int, Int, MutableList<NodeBean>> { // MutableList<NodeBean>
        val tempNodeList: MutableList<NodeBean> = ArrayList()

        for (i in 0..99) {
            val node = NodeBean()
            node.pos = Tools.index2pos(i, 10)
            node.index = i
            node.reachSate = ReachState.NOT_FIND
            tempNodeList.add(node)
        }

        // 障碍
        val random = Random()
        for (i in 0..29) {
            tempNodeList.get(random.nextInt(100)).reachSate = ReachState.NOT_ALLOW_GO
        }

        // 起点和终点
        val start: Int = random.nextInt(100)
        val end: Int = random.nextInt(100)
        tempNodeList.get(start).reachSate = ReachState.FIND_AND_GO
        tempNodeList.get(start).isStart = true
        tempNodeList.get(end).reachSate = ReachState.DESTINATION
        tempNodeList.get(end).isEnd = true

        return Triple(start, end, tempNodeList)
    }

}