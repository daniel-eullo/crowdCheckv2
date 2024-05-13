package com.example.customchu;

public class MainModel {
    //must be the same name to firebase key
    String username, account_id, userFeedback, date;
    Long rating;

    private String ticketNumber;

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public MainModel(String username, String account_id, String userFeedback, String date, Long rating) {
        this.username = username;
        this.account_id = account_id;
        this.userFeedback = userFeedback;
        this.date = date;
        this.rating = rating;
    }

    MainModel(){

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getRating() {
        return rating;
    }

    public void setRating(Long rating) {
        this.rating = rating;
    }
}
