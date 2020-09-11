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
    private List<NodeBean> openList = new ArrayList<>();
    private List<NodeBean> closeList = new ArrayList<>();
    private List<NodeBean> pathList = new ArrayList<>();
    private List<NodeBean> nodeList;
    private NodeBean curNode;
    private boolean isDiagonal;                         // 是否斜角行进

    public void initNodeList(List<NodeBean> oriList) {  //初始化 NodeList
        nodeList = oriList;
    }

    private int getPosDistance(Vector2 pos1, Vector2 pos2) {
        int distance = 0;
        if (pos1.getX() == pos2.getX() || pos1.getY() == pos2.getY()) {
            distance += Math.abs(pos1.getX() - pos2.getX()) * 10;
            distance += Math.abs(pos1.getY() - pos2.getY()) * 10;
        } else {
            int dx = Math.abs(pos1.getX() - pos2.getX());
            int dy = Math.abs(pos1.getY() - pos2.getY());
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
        if (curNode == null) {
            curNode = start;
        }
        closeList.add(curNode);

        nodeList.get(curNode.getIndex()).setReachSate(ReachState.FIND_AND_GO);
        for (int i = 0; i < openList.size(); i++) {
            if (openList.get(i).getIndex() == curNode.getIndex()) {
                openList.remove(i);
                break;
            }
        }

        Vector2 startPos = start.getPos();
        Vector2 endPos = end.getPos();

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

        if (isDiagonal) {
            Vector2 curTopLeftPos = new Vector2(curNode.getPos().getX() - 1, curNode.getPos().getY() - 1);
            Vector2 curTopRightPos = new Vector2(curNode.getPos().getX() + 1, curNode.getPos().getY() - 1);
            Vector2 curDownLeftPos = new Vector2(curNode.getPos().getX() - 1, curNode.getPos().getY() + 1);
            Vector2 curDownRightPos = new Vector2(curNode.getPos().getX() + 1, curNode.getPos().getY() + 1);

            Vector2 topLeftDown = new Vector2(curTopLeftPos.getX(), curTopLeftPos.getY() + 1);
            Vector2 topLeftRight = new Vector2(curTopLeftPos.getX() + 1, curTopLeftPos.getY());
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

        addNeighborToOpenList(curLeftPos, startPos, endPos);
        addNeighborToOpenList(curRightPos, startPos, endPos);
        addNeighborToOpenList(curTopPos, startPos, endPos);
        addNeighborToOpenList(curDownPos, startPos, endPos);


        if (openList.size() > 0) {
            NodeBean minFNode = openList.get(0);
            for (int i = 0; i < openList.size(); i++) {
                if (openList.get(i).getF() <= minFNode.getF()) {
                    minFNode = openList.get(i);
                }
            }
            curNode = minFNode;
        }

        for (int i = 0; i < closeList.size(); i++) {
            if (closeList.get(i).getIndex() == end.getIndex()) {
                curNode = end;
                do {
                    pathList.add(curNode);
                    curNode = curNode.getParent();
                } while (curNode != null);

                for (int j = 0; j < pathList.size(); j++) {
                    nodeList.get(pathList.get(j).getIndex()).setPath(true);
                }

                return ReachState.FIND_BUT_NOT_GO;
            }
        }
        if (openList.size() == 0) {
            return ReachState.FIND_AND_GO;
        }
        return ReachState.NOT_FIND;
    }

    private void addNeighborToOpenList(Vector2 curPos, Vector2 startPos, Vector2 endPos) {
        if (curPos.isValid(10) && nodeList.get(Tools.pos2index(curPos, 10)).findNode()) {
            System.out.println(String.format("AstarUtils curLeftPos valid! pos:%s-%s index:%s", curPos.getX(), curPos.getY(), Tools.pos2index(curPos, 10)));
            nodeList.get(Tools.pos2index(curPos, 10)).setG(getPosDistance(startPos, curPos));
            nodeList.get(Tools.pos2index(curPos, 10)).setH(getPosDistance(curPos, endPos));
            nodeList.get(Tools.pos2index(curPos, 10)).calF();
            nodeList.get(Tools.pos2index(curPos, 10)).setParent(curNode);

            openList.add(nodeList.get(Tools.pos2index(curPos, 10)));
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
