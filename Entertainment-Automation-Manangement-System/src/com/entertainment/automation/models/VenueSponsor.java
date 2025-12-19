package com.entertainment.automation.models;

import java.sql.Date;

public class VenueSponsor {
    private int venueID;
    private int sponsorID;
    private Date sponsorshipStart;
    private Date sponsorshipEnd;
    
    public VenueSponsor() {}
    
    public VenueSponsor(int venueID, int sponsorID, Date sponsorshipStart, Date sponsorshipEnd) {
        this.venueID = venueID;
        this.sponsorID = sponsorID;
        this.sponsorshipStart = sponsorshipStart;
        this.sponsorshipEnd = sponsorshipEnd;
    }
   
    public int getVenueID() { 
        return venueID; 
    }
    
    public void setVenueID(int venueID) { 
        this.venueID = venueID; 
    }
    
    public int getSponsorID() { 
        return sponsorID; 
    }
    
    public void setSponsorID(int sponsorID) { 
        this.sponsorID = sponsorID; 
    }
    
    public Date getSponsorshipStart() { 
        return sponsorshipStart; 
    }
    
    public void setSponsorshipStart(Date sponsorshipStart) { 
        this.sponsorshipStart = sponsorshipStart; 
    }
    
    public Date getSponsorshipEnd() { 
        return sponsorshipEnd; 
    }
    
    public void setSponsorshipEnd(Date sponsorshipEnd) { 
        this.sponsorshipEnd = sponsorshipEnd; 
    }
    
    @Override
    public String toString() {
        return "VenueSponsor{" +
                "venueID=" + venueID +
                ", sponsorID=" + sponsorID +
                ", sponsorshipStart=" + sponsorshipStart +
                ", sponsorshipEnd=" + sponsorshipEnd +
                '}';
    }
}