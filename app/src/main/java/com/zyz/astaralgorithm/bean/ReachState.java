package com.zyz.astaralgorithm.bean;

/**
 * <pre>
 *     author : ZYZ
 *     e-mail : zyz163mail@163.com
 *     time   : 2020/09/11
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public enum ReachState{

    NOT_ALLOW_GO(-1),           // -1 不能走
    NOT_FIND(0),                // 0  未发现
    FIND_BUT_NOT_GO(1),         // 1  已发现未走
    FIND_AND_GO(2),             // 2  已发现已走
    DESTINATION(3);             // 3  目的地

    public final int code;

    ReachState(int code) {
        this.code = code;
    }
}