package com.example.customchu;

public class sentModel {
    //must be the same name to firebase key
    String receiver_name;

    sentModel(){

    }
    public sentModel(String receiver_name) {
        this.receiver_name = receiver_name;
    }

    public String getReceiver_name() {
        return receiver_name;
    }

    public void setReceiver_name(String receiver_name) {
        this.receiver_name = receiver_name;
    }
}
