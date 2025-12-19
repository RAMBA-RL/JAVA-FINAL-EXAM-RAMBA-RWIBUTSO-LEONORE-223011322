package com.entertainment.automation.models;

public class TicketType {
    private int ticketTypeID;
    private int eventID;
    private String typeName;
    private double price;
    private String description;

    public TicketType() {}

    public TicketType(int eventID, String typeName, double price, String description) {
        this.eventID = eventID;
        this.typeName = typeName;
        this.price = price;
        this.description = description;
    }
    public int getTicketTypeID() { return ticketTypeID; }
    public void setTicketTypeID(int ticketTypeID) { this.ticketTypeID = ticketTypeID; }

    public int getEventID() { return eventID; }
    public void setEventID(int eventID) { this.eventID = eventID; }

    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}