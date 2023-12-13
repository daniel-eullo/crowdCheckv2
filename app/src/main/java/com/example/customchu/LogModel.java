package com.example.customchu;

public class LogModel {

    LogModel(){}
    String account_id, date_and_time;
    Boolean  in,  out;

    public LogModel(String account_id, String date_and_time, Boolean in, Boolean out) {
        this.account_id = account_id;
        this.date_and_time = date_and_time;
        this.in = in;
        this.out = out;
    }

    public String getAccount_id() {
        return account_id;
    }
    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }
    public String getDate_and_time() {
        return date_and_time;
    }
    public void setDate_and_time(String date_and_time) {
        this.date_and_time = date_and_time;
    }

    public Boolean getIn() {
        return in;
    }
    public void setIn(Boolean in) {
        this.in = in;
    }

    public Boolean getOut() {return out;}
    public void setOut(Boolean out) {
        this.out = out;
    }
}
