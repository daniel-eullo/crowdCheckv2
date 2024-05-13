package com.example.customchu;

public class recModel {

    String sender_name, sender_uid;

    recModel(){

    }

    public recModel(String sender_name, String sender_uid) {
        this.sender_name = sender_name;
        this.sender_uid = sender_uid;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getSender_uid() {
        return sender_uid;
    }

    public void setSender_uid(String sender_uid) {
        this.sender_uid = sender_uid;
    }
}
