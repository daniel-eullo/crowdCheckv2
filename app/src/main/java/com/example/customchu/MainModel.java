package com.example.customchu;

public class MainModel {
    //must be the same name to firebase key
    String username, account_id, userFeedback;
    Long rating;

    MainModel(){

    }

    public MainModel(String username, String account_id, String userFeedback, int rating) {
        this.username = username;
        this.account_id = account_id;
        this.userFeedback = userFeedback;
        this.rating = (long) rating;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public String getUserFeedback() {
        return userFeedback;
    }

    public void setUserFeedback(String userFeedback) {
        this.userFeedback = userFeedback;
    }

    public int getRating() {
        return Math.toIntExact(rating);
    }

    public void setRating(int rating) {
        this.rating = (long) rating;
    }
}
