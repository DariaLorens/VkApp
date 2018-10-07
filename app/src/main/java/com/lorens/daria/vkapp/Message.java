package com.lorens.daria.vkapp;

import java.io.Serializable;

/**
 * Created by ilyas on 07-Oct-18.
 */

public class Message implements Serializable {

    private String msg;
    private int user_id;
    private String user_name;
    private long at;
    private boolean out;

    public Message() {
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public long getAt() {
        return at;
    }

    public boolean isOut() {
        return out;
    }

    public void setOut(boolean out) {
        this.out = out;
    }

    public void setAt(long at) {
        this.at = at;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
