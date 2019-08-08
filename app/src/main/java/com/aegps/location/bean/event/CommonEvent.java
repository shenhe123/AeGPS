package com.aegps.location.bean.event;

/**
 * Created by ShenHe on 2019/8/8.
 */

public class CommonEvent {
    private int code;
    private String msg;

    public CommonEvent(int code) {
        this.code = code;
    }

    public CommonEvent(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
