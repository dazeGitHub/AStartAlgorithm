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

    NOT_ALLOW_GO(-1),
    NOT_FIND(0),
    FIND_BUT_NOT_GO(1),
    FIND_AND_GO(2),
    DESTINATION(3);

    public final int code;

    ReachState(int code) {
        this.code = code;
    }
}