package com.entertainment.automation.models;
import java.time.LocalDateTime;

public class Ticket {
    private int ticketID;
    private int eventID;
    private int userID;
    private int ticketTypeID; 
    private String ticketNumber;
    private String seatNumber;
    private String ticketType;  
    private double price;       
    private String status;
    private LocalDateTime purchaseDate;
    private String eventTitle;
    private String userName;
    
    public Ticket() {}
    
    public int getTicketID() { return ticketID; }
    public void setTicketID(int ticketID) { this.ticketID = ticketID; }
    
    public int getEventID() { return eventID; }
    public void setEventID(int eventID) { this.eventID = eventID; }
    
    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }
    
    public int getTicketTypeID() { return ticketTypeID; }
    public void setTicketTypeID(int ticketTypeID) { this.ticketTypeID = ticketTypeID; }
    
    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
    
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    
    public String getTicketType() { return ticketType; }
    public void setTicketType(String ticketType) { this.ticketType = ticketType; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }
    
    public String getEventTitle() { return eventTitle; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}