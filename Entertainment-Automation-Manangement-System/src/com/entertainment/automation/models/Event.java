package com.entertainment.automation.models;

import java.time.LocalDateTime;

public class Event {
    private int eventID;
    private String referenceID;
    private String title;
    private String description;
    private LocalDateTime eventDate;
    private int venueID;
    private String status;
    private double ticketPrice;
    private int availableTickets;
    private int createdBy;
    private String remarks;
    private LocalDateTime createdAt;
    private String venueName;
    
    public Event() {}
  
    public int getEventID() { return eventID; }
    public void setEventID(int eventID) { this.eventID = eventID; }
    
    public String getReferenceID() { return referenceID; }
    public void setReferenceID(String referenceID) { this.referenceID = referenceID; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime localDateTime) { this.eventDate = localDateTime; }
    
    public int getVenueID() { return venueID; }
    public void setVenueID(int venueID) { this.venueID = venueID; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public double getTicketPrice() { return ticketPrice; }
    public void setTicketPrice(double ticketPrice) { this.ticketPrice = ticketPrice; }
    
    public int getAvailableTickets() { return availableTickets; }
    public void setAvailableTickets(int availableTickets) { this.availableTickets = availableTickets; }
    
    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }
    
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }
} 