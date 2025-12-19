package com.entertainment.automation.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Sponsor {
    private int sponsorID;
    private String companyName;
    private String contactPerson;
    private String email;
    private String phone;
    private String sponsorshipLevel;
    private double amount;
    private LocalDateTime createdAt;
    
    private List<Venue> venues;
    
    public Sponsor() {
        this.venues = new ArrayList<>();
    }
    
    public Sponsor(int sponsorID, String companyName, String contactPerson, 
                   String email, String phone, String sponsorshipLevel, double amount) {
        this.sponsorID = sponsorID;
        this.companyName = companyName;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
        this.sponsorshipLevel = sponsorshipLevel;
        this.amount = amount;
        this.venues = new ArrayList<>();
    }
    
    // Getters and Setters
    public int getSponsorID() { 
        return sponsorID; 
    }
    
    public void setSponsorID(int sponsorID) { 
        this.sponsorID = sponsorID; 
    }
    
    public String getCompanyName() { 
        return companyName; 
    }
    
    public void setCompanyName(String companyName) { 
        this.companyName = companyName; 
    }
    
    public String getContactPerson() { 
        return contactPerson; 
    }
    
    public void setContactPerson(String contactPerson) { 
        this.contactPerson = contactPerson; 
    }
    
    public String getEmail() { 
        return email; 
    }
    
    public void setEmail(String email) { 
        this.email = email; 
    }
    
    public String getPhone() { 
        return phone; 
    }
    
    public void setPhone(String phone) { 
        this.phone = phone; 
    }
    
    public String getSponsorshipLevel() { 
        return sponsorshipLevel; 
    }
    
    public void setSponsorshipLevel(String sponsorshipLevel) { 
        this.sponsorshipLevel = sponsorshipLevel; 
    }
    
    public double getAmount() { 
        return amount; 
    }
    
    public void setAmount(double amount) { 
        this.amount = amount; 
    }
    
    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }
    
    public List<Venue> getVenues() { 
        return venues; 
    }
    
    public void setVenues(List<Venue> venues) { 
        this.venues = venues; 
    }
    
    public void addVenue(Venue venue) {
        if (this.venues == null) {
            this.venues = new ArrayList<>();
        }
        if (!this.venues.contains(venue)) {
            this.venues.add(venue);
        }
    }
    
    public void removeVenue(Venue venue) {
        if (this.venues != null) {
            this.venues.remove(venue);
        }
    }
    
    public int getVenueCount() {
        return (this.venues != null) ? this.venues.size() : 0;
    }
    
    @Override
    public String toString() {
        return "Sponsor{" +
                "sponsorID=" + sponsorID +
                ", companyName='" + companyName + '\'' +
                ", contactPerson='" + contactPerson + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", sponsorshipLevel='" + sponsorshipLevel + '\'' +
                ", amount=" + amount +
                ", venueCount=" + getVenueCount() +
                '}';
    }
}