package com.zyz.astaralgorithm.bean;

/**
 * Created by Michael
 * Date:  2020/8/25
 * Func: Vector2 : 二维向量
 */
public class Vector2 {
    private int x;
    private int y;

    public Vector2() {

    }

    public Vector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isValid(int length) {
        return x >= 1 && y >= 1 && x <= length && y <= length;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
