package com.entertainment.automation.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Venue {
    private int venueID;
    private String name;
    private String address;
    private int capacity;
    private String manager;
    private String contact;
    private LocalDateTime createdAt;
    private List<Sponsor> sponsors;
    
    public Venue() {
        this.sponsors = new ArrayList<>();
    }
    
    public Venue(int venueID, String name, String address, int capacity, String manager, String contact) {
        this.venueID = venueID;
        this.name = name;
        this.address = address;
        this.capacity = capacity;
        this.manager = manager;
        this.contact = contact;
        this.sponsors = new ArrayList<>();
    }
    
    // Getters and Setters
    public int getVenueID() { 
        return venueID; 
    }
    
    public void setVenueID(int venueID) { 
        this.venueID = venueID; 
    }
    
    public String getName() { 
        return name; 
    }
    
    public void setName(String name) { 
        this.name = name; 
    }
    
    public String getAddress() { 
        return address; 
    }
    
    public void setAddress(String address) { 
        this.address = address; 
    }
    
    public int getCapacity() { 
        return capacity; 
    }
    
    public void setCapacity(int capacity) { 
        this.capacity = capacity; 
    }
    
    public String getManager() { 
        return manager; 
    }
    
    public void setManager(String manager) { 
        this.manager = manager; 
    }
    
    public String getContact() { 
        return contact; 
    }
    
    public void setContact(String contact) { 
        this.contact = contact; 
    }
    
    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }
    
    public List<Sponsor> getSponsors() { 
        return sponsors; 
    }
    
    public void setSponsors(List<Sponsor> sponsors) { 
        this.sponsors = sponsors; 
    }
    
    public void addSponsor(Sponsor sponsor) {
        if (this.sponsors == null) {
            this.sponsors = new ArrayList<>();
        }
        if (!this.sponsors.contains(sponsor)) {
            this.sponsors.add(sponsor);
        }
    }
    
    public void removeSponsor(Sponsor sponsor) {
        if (this.sponsors != null) {
            this.sponsors.remove(sponsor);
        }
    }
    
    public int getSponsorCount() {
        return (this.sponsors != null) ? this.sponsors.size() : 0;
    }
    
    @Override
    public String toString() {
        return "Venue{" +
                "venueID=" + venueID +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", capacity=" + capacity +
                ", manager='" + manager + '\'' +
                ", contact='" + contact + '\'' +
                ", sponsorCount=" + getSponsorCount() +
                '}';
    }
}