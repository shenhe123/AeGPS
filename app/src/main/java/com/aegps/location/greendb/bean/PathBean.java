package com.aegps.location.greendb.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class PathBean {

    private String userid;
    private String name;
    private String tracktime;
    @Generated(hash = 1409714889)
    public PathBean(String userid, String name, String tracktime) {
        this.userid = userid;
        this.name = name;
        this.tracktime = tracktime;
    }
    @Generated(hash = 277194800)
    public PathBean() {
    }
    public String getUserid() {
        return this.userid;
    }
    public void setUserid(String userid) {
        this.userid = userid;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getTracktime() {
        return this.tracktime;
    }
    public void setTracktime(String tracktime) {
        this.tracktime = tracktime;
    }
}
