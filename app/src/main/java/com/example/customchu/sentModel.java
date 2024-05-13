package com.example.customchu;

public class sentModel {
    //must be the same name to firebase key
    String receiver_name, receiver_uid;

    sentModel(){

    }

    public sentModel(String receiver_name, String receiver_uid) {
        this.receiver_name = receiver_name;
        this.receiver_uid = receiver_uid;
    }

    public String getReceiver_uid() {
        return receiver_uid;
    }

    public void setReceiver_uid(String receiver_uid) {
        this.receiver_uid = receiver_uid;
    }

    public String getReceiver_name() {
        return receiver_name;
    }

    public void setReceiver_name(String receiver_name) {
        this.receiver_name = receiver_name;
    }
}
