package com.zyz.astaralgorithm.bean;

/**
 * Created by Michael
 * Date:  2020/8/25
 * Func:
 */
public class NodeBean {
    private int f;
    private int g;
    private int h;
    private Vector2 pos;
    private int index;
    private ReachState reachSate;  // -1 不能走 0 未发现 1 已发现未走 2 已发现已走 3 目的地
    private NodeBean parent;
    private boolean isPath;
    private boolean isEnd;  // 是否终点
    private boolean isStart; // 是否起点
    private TileType tileType;

    public int getF() {
        return g + h;
    }

    public boolean isFValidate(){
        return getF() != 0;
    }

    public void setF(int f) {
        this.f = f;
    }

    public void calF() {
        this.f = g + h;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public Vector2 getPos() {
        return pos;
    }

    public void setPos(Vector2 pos) {
        this.pos = pos;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ReachState getReachSate() {
        return reachSate;
    }

    public void setReachSate(ReachState reachSate) {
        this.reachSate = reachSate;
    }

    public NodeBean getParent() {
        return parent;
    }

    public void setParent(NodeBean parent) {
        this.parent = parent;
    }

    public boolean isPath() {
        return isPath;
    }

    public void setPath(boolean path) {
        isPath = path;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public TileType getTileType() {
        return tileType;
    }

    public void setTileType(TileType tileType) {
        this.tileType = tileType;
        switch (tileType){
            case Floor:
            case OpenDoor:
            case Stairs:
                reachSate = ReachState.NOT_FIND;
                break;
            case Wall:
            case LowWall:
            case Table:
            case ClosedDoor:
            case Tree:
                reachSate = ReachState.NOT_ALLOW_GO;
                break;
            case Grass:
                reachSate = ReachState.FIND_BUT_NOT_GO;
                break;
            case Maze:

                break;
        }
    }

    /**
     *  如果当前 nodeBean 的 reachSate 是 未发现 或 是目的地，则设置 reachSate 为 已发现未走，并返回 true，否则返回 false
     */
    public boolean findNode() {
        if (this.reachSate == ReachState.NOT_FIND || this.reachSate == ReachState.DESTINATION) {
            this.reachSate = ReachState.FIND_BUT_NOT_GO;
            return true;
        }
        return false;
    }

    public boolean checkNode() {
        if (this.reachSate == ReachState.NOT_ALLOW_GO) {
            return false;
        }
        if (!this.pos.isValid(10)) {
            return false;
        }
        return true;
    }
}
