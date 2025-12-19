package com.entertainment.automation.models;

import java.time.LocalDateTime;

public class Review {
    private int reviewID;
    private int eventID;
    private int userID;
    private int rating;
    private String comment;
    private LocalDateTime reviewDate;
    private String eventTitle;  
    private String userName;     
    public Review() {}

    public int getReviewID() { return reviewID; }
    public void setReviewID(int reviewID) { this.reviewID = reviewID; }

    public int getEventID() { return eventID; }
    public void setEventID(int eventID) { this.eventID = eventID; }

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDateTime reviewDate) { this.reviewDate = reviewDate; }

    public String getEventTitle() { return eventTitle; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}