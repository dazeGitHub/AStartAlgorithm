package com.zyz.astaralgorithm.utils;

import com.blankj.utilcode.util.ToastUtils;
import com.zyz.astaralgorithm.bean.NodeBean;
import com.zyz.astaralgorithm.bean.ReachState;
import com.zyz.astaralgorithm.bean.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael
 * Date:  2020/8/25
 * Func:
 */
public class AstarUtils {

    private List<NodeBean> openList = new ArrayList<>();  //探查到的可以走的结点列表
    private List<NodeBean> closeList = new ArrayList<>(); //已经走过的结点列表
    private List<NodeBean> pathList = new ArrayList<>();
    private List<NodeBean> nodeList;

    private NodeBean curNode;
    private boolean isDiagonal;                         // 是否斜角行进

    public void initNodeList(List<NodeBean> oriList) {  //初始化 NodeList
        nodeList = oriList;
    }

    /**
     * 返回两个二维向量之间的距离
     * @param pos1
     * @param pos2
     * @return
     */
    private int getPosDistance(Vector2 pos1, Vector2 pos2) {
        int distance = 0;
        if (pos1.getX() == pos2.getX() || pos1.getY() == pos2.getY()) {
            distance += Math.abs(pos1.getX() - pos2.getX()) * 10;
            distance += Math.abs(pos1.getY() - pos2.getY()) * 10;
        } else {
            int dx = Math.abs(pos1.getX() - pos2.getX());
            int dy = Math.abs(pos1.getY() - pos2.getY());
            //多出来的距离 * 10，相等的距离 * 14
            if (dx > dy) {
                distance += (dx - dy) * 10;
                distance += dy * 14;
            } else {
                distance += (dy - dx) * 10;
                distance += dx * 14;
            }
        }
        return distance;
    }

    public List<NodeBean> findPath(NodeBean start, NodeBean end) throws Exception {
        if (nodeList == null) {
            throw new Exception("AstarUtils not init NodeList !!");
        }

        while (true) {
            ReachState state = nextStep(start, end);
            if (state == ReachState.FIND_BUT_NOT_GO) {
                ToastUtils.showShort("Has Reach End");
                break;
            } else if (state == ReachState.FIND_AND_GO) {
                ToastUtils.showShort("No Road");
                break;
            }
        }
        return pathList;
    }

    /**
     * @param start 起点
     * @param end   终点
     * @return 0 未到达 1 已到达 2 此路不通
     */

    public ReachState nextStep(NodeBean start, NodeBean end) throws Exception {
        if (nodeList == null) {
            throw new Exception("AstarUtils not init NodeList !!");
        }
        //如果刚开始没有当前结点，则设置起点为当前结点
        if (curNode == null) {
            curNode = start;
        }
        closeList.add(curNode);

        nodeList.get(curNode.getIndex()).setReachSate(ReachState.FIND_AND_GO); //设置当前结点为 "已发现已走"

        //从 openList 中移除当前结点
        for (int i = 0; i < openList.size(); i++) {
            if (openList.get(i).getIndex() == curNode.getIndex()) {
                openList.remove(i);
                break;
            }
        }

        Vector2 startPos = start.getPos();
        Vector2 endPos = end.getPos();

        //获取左右上下四个结点
        Vector2 curLeftPos = new Vector2(curNode.getPos().getX() - 1, curNode.getPos().getY());
        Vector2 curRightPos = new Vector2(curNode.getPos().getX() + 1, curNode.getPos().getY());
        Vector2 curTopPos = new Vector2(curNode.getPos().getX(), curNode.getPos().getY() - 1);
        Vector2 curDownPos = new Vector2(curNode.getPos().getX(), curNode.getPos().getY() + 1);

        System.out.println(String.format("AstarUtils startPos:%s-%s leftPos:%s-%s rightPos:%s-%s topPos:%s-%s downPos:%s-%s endPos:%s-%s "
                , startPos.getX(), startPos.getY()
                , curLeftPos.getX(), curLeftPos.getY()
                , curRightPos.getX(), curRightPos.getY()
                , curTopPos.getX(), curTopPos.getY()
                , curDownPos.getX(), curDownPos.getY()
                , endPos.getX(), endPos.getY()));

        if (isDiagonal) { //如果 斜角前进的开关 为开，则获取四个角的结点并加到 openList 中
            Vector2 curTopLeftPos = new Vector2(curNode.getPos().getX() - 1, curNode.getPos().getY() - 1);
            Vector2 curTopRightPos = new Vector2(curNode.getPos().getX() + 1, curNode.getPos().getY() - 1);
            Vector2 curDownLeftPos = new Vector2(curNode.getPos().getX() - 1, curNode.getPos().getY() + 1);
            Vector2 curDownRightPos = new Vector2(curNode.getPos().getX() + 1, curNode.getPos().getY() + 1);

            Vector2 topLeftDown = new Vector2(curTopLeftPos.getX(), curTopLeftPos.getY() + 1);
            Vector2 topLeftRight = new Vector2(curTopLeftPos.getX() + 1, curTopLeftPos.getY());

            //如果 左上角的结点 的右边或者下边是可达的，说明 该左上角的结点 也可达，则把它加入到 openList 中，其他同理
            if (topLeftDown.isValid(10) && nodeList.get(Tools.pos2index(topLeftDown, 10)).checkNode() ||
                    topLeftRight.isValid(10) && nodeList.get(Tools.pos2index(topLeftRight, 10)).checkNode()) {
                addNeighborToOpenList(curTopLeftPos, startPos, endPos);
            }

            Vector2 topRightDown = new Vector2(curTopRightPos.getX(), curTopRightPos.getY() + 1);
            Vector2 topRightLeft = new Vector2(curTopRightPos.getX() - 1, curTopRightPos.getY());
            if (topRightDown.isValid(10) && nodeList.get(Tools.pos2index(topRightDown, 10)).checkNode() ||
                    topRightLeft.isValid(10) && nodeList.get(Tools.pos2index(topRightLeft, 10)).checkNode()) {
                addNeighborToOpenList(curTopRightPos, startPos, endPos);
            }

            Vector2 downLeftTop = new Vector2(curDownLeftPos.getX(), curDownLeftPos.getY() - 1);
            Vector2 downLeftRight = new Vector2(curDownLeftPos.getX() + 1, curDownLeftPos.getY());
            if (downLeftTop.isValid(10) && nodeList.get(Tools.pos2index(downLeftTop, 10)).checkNode() ||
                    downLeftRight.isValid(10) && nodeList.get(Tools.pos2index(downLeftRight, 10)).checkNode()) {
                addNeighborToOpenList(curDownLeftPos, startPos, endPos);
            }

            Vector2 downRightTop = new Vector2(curDownRightPos.getX(), curDownRightPos.getY() - 1);
            Vector2 downRightLeft = new Vector2(curDownRightPos.getX() - 1, curDownRightPos.getY());
            if (downRightTop.isValid(10) && nodeList.get(Tools.pos2index(downRightTop, 10)).checkNode() ||
                    downRightLeft.isValid(10) && nodeList.get(Tools.pos2index(downRightLeft, 10)).checkNode()) {
                addNeighborToOpenList(curDownRightPos, startPos, endPos);
            }
        }

        //把左右上下四个结点放到 openList 中
        addNeighborToOpenList(curLeftPos, startPos, endPos);
        addNeighborToOpenList(curRightPos, startPos, endPos);
        addNeighborToOpenList(curTopPos, startPos, endPos);
        addNeighborToOpenList(curDownPos, startPos, endPos);

        //每次都是获取 openList 中 F 值最小的结点来做为当前结点
        if (openList.size() > 0) {
            NodeBean minFNode = openList.get(0);
            for (int i = 0; i < openList.size(); i++) {
                if (openList.get(i).getF() <= minFNode.getF()) {
                    minFNode = openList.get(i);
                }
            }
            curNode = minFNode;
        }

        //遍历 closeList
        for (int i = 0; i < closeList.size(); i++) {

            //如果有一个结点是终结点，则设置当前结点为终结点
            if (closeList.get(i).getIndex() == end.getIndex()) {
                curNode = end;

                //从当前结点(终结点)一直往前倒，添加到 pathList 中
                do {
                    pathList.add(curNode);
                    curNode = curNode.getParent();
                } while (curNode != null);

                //遍历 pathList，设置 nodeList 中包含的值的 isPath 为 true
                for (int j = 0; j < pathList.size(); j++) {
                    nodeList.get(pathList.get(j).getIndex()).setPath(true);
                }

                return ReachState.FIND_BUT_NOT_GO;  //已发现未走
            }
        }
        if (openList.size() == 0) {
            return ReachState.FIND_AND_GO;          //已发现已走 (没有路，addNeighborToOpenList() 没往 openList 加进去结点）
        }
        return ReachState.NOT_FIND;                 //未发现
    }

    //计算 curPos 的结点的 G 值、H 值、F 值，设置其 Parent 是当前结点 curNode，并将其添加到 openList 中
    private void addNeighborToOpenList(Vector2 curPos, Vector2 startPos, Vector2 endPos) {
        int curPosIndex = Tools.pos2index(curPos, 10);
        if (curPos.isValid(10) && nodeList.get(curPosIndex).findNode()) {
            System.out.println(String.format("AstarUtils curLeftPos valid! pos:%s-%s index:%s", curPos.getX(), curPos.getY(), curPosIndex));
            //这里 G 值和 H 值使用同一种方法 getPosDistance() 来计算
            nodeList.get(curPosIndex).setG(getPosDistance(startPos, curPos));
            nodeList.get(curPosIndex).setH(getPosDistance(curPos, endPos));
            nodeList.get(curPosIndex).calF();
            nodeList.get(curPosIndex).setParent(curNode);

            openList.add(nodeList.get(curPosIndex));
        }
    }

    public List<NodeBean> getNodeList() {
        return nodeList;
    }

    public boolean isDiagonal() {
        return isDiagonal;
    }

    public void setIsDiagonal(boolean diagonal) {
        isDiagonal = diagonal;
    }

    public void reset() {
        if (nodeList != null)
            nodeList.clear();
        if (openList != null)
            openList.clear();
        if (closeList != null)
            closeList.clear();
        if (pathList != null)
            pathList.clear();
    }

}
