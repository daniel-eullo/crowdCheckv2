package com.example.customchu;

public class LogModel {

    LogModel(){}
    String date, time, uid;
    Integer status;

    public LogModel(String date, String time, String uid, Integer status) {
        this.date = date;
        this.time = time;
        this.uid = uid;
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
