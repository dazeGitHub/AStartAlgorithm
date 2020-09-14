package com.zyz.astaralgorithm.utils

import com.blankj.utilcode.util.ToastUtils
import com.zyz.astaralgorithm.bean.NodeBean
import com.zyz.astaralgorithm.bean.ReachState
import com.zyz.astaralgorithm.bean.Vector2
import java.util.*

/**
 * Created by Michael
 * Date:  2020/8/25
 * Func:
 */
class AStarUtils {
    
    private val mOpenList: MutableList<NodeBean> = ArrayList()      //探查到的可以走的结点列表
    private val mCloseList: MutableList<NodeBean> = ArrayList()     //已经走过的结点列表
    private val mPathList: MutableList<NodeBean> = ArrayList()
    private var mNodeList: MutableList<NodeBean>? = null
    private var mCurNode: NodeBean? = null
    private var mLength = 10
    var isDiagonal:Boolean = false                                  // 是否斜角行进 = false

    fun initNodeList(oriList: MutableList<NodeBean>) {              //初始化 NodeList
        mNodeList = oriList
    }

    /**
     * 返回两个二维向量之间的距离
     * @param pos1
     * @param pos2
     * @return
     */
    private fun getPosDistance(pos1: Vector2, pos2: Vector2): Int {
        var distance = 0
        if (pos1.x == pos2.x || pos1.y == pos2.y) {
            distance += Math.abs(pos1.x - pos2.x) * 10
            distance += Math.abs(pos1.y - pos2.y) * 10
        } else {
            val dx = Math.abs(pos1.x - pos2.x)
            val dy = Math.abs(pos1.y - pos2.y)
            //多出来的距离 * 10，相等的距离 * 14
            if (dx > dy) {
                distance += (dx - dy) * 10
                distance += dy * 14
            } else {
                distance += (dy - dx) * 10
                distance += dx * 14
            }
        }
        return distance
    }

    /**
     * 一直执行 nextStep() 直到找到终点
     * @param start
     * @param end
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun findPath(start: NodeBean, end: NodeBean): List<NodeBean?>? {
        if (mNodeList == null) {
            throw Exception("AstarUtils not init NodeList !!")
        }
        while (true) {
            val state = nextStep(start, end)
            if (state == ReachState.FIND_BUT_NOT_GO) {
                ToastUtils.showShort("Has Reach End")
                break
            } else if (state == ReachState.FIND_AND_GO) {
                ToastUtils.showShort("No Road")
                break
            }
        }
        return mPathList
    }

    /**
     * @param startNodeBean 起点
     * @param endNodeBean   终点
     * @return 0 未到达 1 已到达 2 此路不通
     */
    @Throws(Exception::class)
    fun nextStep(startNodeBean: NodeBean, endNodeBean: NodeBean): ReachState {
        if (mNodeList == null) {
            throw Exception("AstarUtils not init NodeList !!")
        }
        //如果刚开始没有当前结点，则设置起点为当前结点
        if (mCurNode == null) {
            mCurNode = startNodeBean
        }
        mCloseList.add(mCurNode!!)
        mNodeList!![mCurNode!!.index].reachSate = ReachState.FIND_AND_GO  //设置当前结点为 "已发现已走"

        //从 openList 中移除当前结点
        for (i in mOpenList.indices) {
            if (mOpenList[i]?.index == mCurNode!!.index) {
                mOpenList.removeAt(i)
                break
            }
        }
        val startPos = startNodeBean.pos
        val endPos = endNodeBean.pos

        //获取左右上下四个结点
        val curLeftPos : Vector2 = getLeftPosNodeVector(mCurNode!!)
        val curRightPos : Vector2 = getRightPosNode(mCurNode!!)
        val curTopPos : Vector2 = getTopPosNodeVector(mCurNode!!)
        val curDownPos : Vector2 = getDownPosNodeVector(mCurNode!!)

        println(
            String.format(
                "AstarUtils startPos:%s-%s leftPos:%s-%s rightPos:%s-%s topPos:%s-%s downPos:%s-%s endPos:%s-%s "
                , startPos.x, startPos.y, curLeftPos.x, curLeftPos.y , curRightPos.x, curRightPos.y, curTopPos.x, curTopPos.y , curDownPos.x, curDownPos.y, endPos.x, endPos.y
            )
        )

        if (isDiagonal) { //如果 斜角前进的开关 为开，则获取四个角的结点并加到 openList 中
            processDiagonal2(startPos,endPos)
        }

        getNodeBeanByPosVector(curLeftPos)?.let{addNeighborToOpenList2(it,curLeftPos, startPos, endPos) }
        getNodeBeanByPosVector(curRightPos)?.let{ addNeighborToOpenList2(it,curRightPos, startPos, endPos)}
        getNodeBeanByPosVector(curTopPos)?.let{ addNeighborToOpenList2(it,curTopPos, startPos, endPos)}
        getNodeBeanByPosVector(curDownPos)?.let{ addNeighborToOpenList2(it,curDownPos, startPos, endPos) }

        //获取 openList 中 F 值最小的结点来做为当前结点
        setupCurNodeFromOpenList()

        travelCloseListToGetPath(endNodeBean)?.let{
            return it
        }

        return if (mOpenList.size == 0) {
            ReachState.FIND_AND_GO                  //已发现已走 (没有路，addNeighborToOpenList() 没往 openList 加进去结点）
        } else
            ReachState.NOT_FIND                     //未发现

    }

    //遍历 closeList 得到路径 path
    private fun travelCloseListToGetPath(end: NodeBean):ReachState?{

        for (i in mCloseList.indices) {

            //如果有一个结点是终结点，则设置当前结点为终结点
            if (mCloseList[i]!!.index == end.index) {
                mCurNode = end

                //从当前结点(终结点)一直往前倒，添加到 pathList 中
                do {
                    mPathList.add(mCurNode!!)
                    mCurNode = mCurNode!!.parent
                } while (mCurNode != null)

                //遍历 pathList，设置 nodeList 中包含的值的 isPath 为 true
                for (j in mPathList.indices) {
                    mNodeList!![mPathList[j]!!.index].isPath = true
                }
                return ReachState.FIND_BUT_NOT_GO   //已发现未走
            }
        }
        return null
    }

    //获取 openList 中 F 值最小的结点来做为当前结点
    private fun setupCurNodeFromOpenList(){
        if (mOpenList.size > 0) {
            var minFNode = mOpenList[0]
            for (i in mOpenList.indices) {
                if (mOpenList[i].f <= minFNode.f) {
                    minFNode = mOpenList[i]
                }
            }
            mCurNode = minFNode
        }
    }

    private fun processDiagonal2(startPos:Vector2,endPos:Vector2){
        val curTopLeftPos = getTopLeftPosNode(mCurNode!!)
        val curTopRightPos = getTopRightPosNode(mCurNode!!)
        val curDownLeftPos = getDownLeftPosNode(mCurNode!!)
        val curDownRightPos = getDownRightPosNode(mCurNode!!)

        val topLeftDown = Vector2(curTopLeftPos.x, curTopLeftPos.y + 1)
        val topLeftRight = Vector2(curTopLeftPos.x + 1, curTopLeftPos.y)

        if(getNodeBeanByPosVector(topLeftDown) != null || getNodeBeanByPosVector(topLeftRight) != null){
            addNeighborToOpenList2(getNodeBeanByPosVector(curTopLeftPos),curTopLeftPos, startPos, endPos)
        }

        val topRightDown = Vector2(curTopRightPos.x, curTopRightPos.y + 1)
        val topRightLeft = Vector2(curTopRightPos.x - 1, curTopRightPos.y)

        if(getNodeBeanByPosVector(topRightDown) != null || getNodeBeanByPosVector(topRightLeft) != null){
            addNeighborToOpenList2(getNodeBeanByPosVector(curTopRightPos),curTopRightPos, startPos, endPos)
        }

        val downLeftTop = Vector2(curDownLeftPos.x, curDownLeftPos.y - 1)
        val downLeftRight = Vector2(curDownLeftPos.x + 1, curDownLeftPos.y)

        if(getNodeBeanByPosVector(downLeftTop) != null || getNodeBeanByPosVector(downLeftRight) != null){
            addNeighborToOpenList2(getNodeBeanByPosVector(curDownLeftPos),curDownLeftPos, startPos, endPos)
        }

        val downRightTop = Vector2(curDownRightPos.x, curDownRightPos.y - 1)
        val downRightLeft = Vector2(curDownRightPos.x - 1, curDownRightPos.y)

        if(getNodeBeanByPosVector(downRightTop) != null || getNodeBeanByPosVector(downRightLeft) != null){
            addNeighborToOpenList2(getNodeBeanByPosVector(curDownRightPos),curDownRightPos, startPos, endPos)
        }
    }

    //计算 curPos 的结点的 G 值、H 值、F 值，设置其 Parent 是当前结点 curNode，并将其添加到 openList 中
    private fun addNeighborToOpenList2(curNodeBean: NodeBean?, curPos: Vector2, startPos: Vector2, endPos: Vector2) {
        if(curNodeBean?.findNode() == true){
            //这里 G 值和 H 值使用同一种方法 getPosDistance() 来计算
            curNodeBean.g = getPosDistance(startPos, curPos)
            curNodeBean.h = getPosDistance(curPos, endPos)
            curNodeBean.calF()
            curNodeBean.parent = mCurNode
            curNodeBean.let { mOpenList.add(it) }
        }
    }

    fun checkVectorValidate(curPosIndex:Int,vector:Vector2):Boolean{
        return vector.isValid(mLength) && mNodeList!![curPosIndex].checkNode()
    }

    fun checkVectorValidate(vector:Vector2):Boolean{
        return checkVectorValidate(Tools.pos2index(vector, mLength),vector)
    }

    fun getNodeBeanByPosVector(vector:Vector2): NodeBean?{
        val curPosIndex = Tools.pos2index(vector, mLength)
        if(checkVectorValidate(curPosIndex,vector)){
            return mNodeList!![Tools.pos2index(vector, mLength)]
        }
        return null
    }

    fun getCurPosNodeVector(nodeBean:NodeBean): Vector2{
        return Vector2(nodeBean.pos.x, nodeBean.pos.y)
    }

    //获取指定结点上下左右的结点
    fun getTopPosNodeVector(nodeBean:NodeBean): Vector2{
        return Vector2(nodeBean.pos.x, nodeBean.pos.y - 1)
    }

    fun getDownPosNodeVector(nodeBean:NodeBean): Vector2{
        return Vector2(nodeBean.pos.x, nodeBean.pos.y + 1)
    }

    fun getLeftPosNodeVector(nodeBean:NodeBean):Vector2{
        return Vector2(nodeBean.pos.x - 1,nodeBean.pos.y)
    }

    fun getRightPosNode(nodeBean:NodeBean):Vector2{
        return Vector2(nodeBean.pos.x + 1, nodeBean.pos.y)
//        return getNodeBeanByPosVector(curTopPos)
    }

    //获取指定结点对角线的结点
    fun getTopLeftPosNode(nodeBean:NodeBean):Vector2{
        return Vector2(nodeBean.pos.x - 1, nodeBean.pos.y - 1)
    }

    fun getTopRightPosNode(nodeBean:NodeBean):Vector2{
        return Vector2(nodeBean.pos.x + 1, nodeBean.pos.y - 1)
    }

    fun getDownLeftPosNode(nodeBean:NodeBean):Vector2{
        return Vector2(nodeBean.pos.x - 1, nodeBean.pos.y + 1)
    }

    fun getDownRightPosNode(nodeBean:NodeBean):Vector2{
        return Vector2(nodeBean.pos.x + 1, nodeBean.pos.y + 1)
    }

    fun getNodeList(): MutableList<NodeBean>? {
        return mNodeList
    }

    fun reset() {
        mCurNode = null
        if (mNodeList != null) mNodeList!!.clear()
        mOpenList?.clear()
        mCloseList?.clear()
        mPathList?.clear()
    }
}