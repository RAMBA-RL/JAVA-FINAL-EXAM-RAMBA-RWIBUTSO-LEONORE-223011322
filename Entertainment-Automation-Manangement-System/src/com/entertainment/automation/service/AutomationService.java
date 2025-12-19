package com.entertainment.automation.service;

import com.entertainment.automation.dao.EventDAO;
import com.entertainment.automation.dao.TicketDAO;
import com.entertainment.automation.models.Event;
import com.entertainment.automation.models.Ticket;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AutomationService {
    private Connection connection;
    private EventDAO eventDAO;
    private TicketDAO ticketDAO;
    
    public AutomationService(Connection connection) {
        this.connection = connection;
        this.eventDAO = new EventDAO();
        this.ticketDAO = new TicketDAO();
    }
    
    public void autoUpdateEventStatus() {
        List<Event> events = eventDAO.getAllEvents();
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();
        
        for (Event event : events) {
            String currentStatus = event.getStatus();
            
            if (event.getAvailableTickets() == 0 && !"Sold Out".equals(currentStatus)) {
                event.setStatus("Sold Out");
                eventDAO.updateEvent(event);
                System.out.println("Auto-updated event '" + event.getTitle() + "' to SOLD OUT");
                continue; 
            }
            if (event.getEventDate().isBefore(today.atStartOfDay()) &&
                    !"Completed".equals(currentStatus) &&
                    !"Sold Out".equals(currentStatus)) {
                event.setStatus("Completed");
                eventDAO.updateEvent(event);
                System.out.println("Auto-updated event '" + event.getTitle() + "' to COMPLETED");
                continue;
            }
            if (event.getEventDate().toLocalDate().equals(today) &&
                    !"Ongoing".equals(currentStatus) &&
                    !"Sold Out".equals(currentStatus) &&
                    !"Completed".equals(currentStatus)) {
                event.setStatus("Ongoing");
                eventDAO.updateEvent(event);
                System.out.println("Auto-updated event '" + event.getTitle() + "' to ONGOING");
                continue;
            }
            
            if (event.getEventDate().isAfter(now) &&
                    !"Scheduled".equals(currentStatus) &&
                    !"Sold Out".equals(currentStatus)) {
                event.setStatus("Scheduled");
                eventDAO.updateEvent(event);
                System.out.println("Auto-updated event '" + event.getTitle() + "' to SCHEDULED");
            }
        }
    }
    
    public void autoUpdateEventStatusWithLogging() {
        List<Event> events = eventDAO.getAllEvents();
        LocalDateTime now = LocalDateTime.now();
        int updatedCount = 0;
        
        System.out.println("=== AUTO-UPDATE EVENT STATUS ===");
        System.out.println("Checking " + events.size() + " events at " + now);
        
        for (Event event : events) {
            String oldStatus = event.getStatus();
            String newStatus = determineEventStatus(event, now);
            
            if (!oldStatus.equals(newStatus)) {
                event.setStatus(newStatus);
                eventDAO.updateEvent(event);
                updatedCount++;
                System.out.println("[UPDATED] " + event.getTitle() + ": " + oldStatus + " to " + newStatus);
            }
        }
        
        System.out.println("Updated " + updatedCount + " events");
        System.out.println("================================\n");
    }
    private String determineEventStatus(Event event, LocalDateTime now) {
        LocalDate today = now.toLocalDate();
        LocalDate eventDate = event.getEventDate().toLocalDate();
      
        if (event.getAvailableTickets() == 0) {
            return "Sold Out";
        }
        
        if (eventDate.isBefore(today)) {
            return "Completed";
        }
        
        if (eventDate.equals(today)) {
            return "Ongoing";
        }
        if (eventDate.isAfter(today)) {
            return "Scheduled";
        }
        
        return event.getStatus();
    }
    
    public void sellTicketAndUpdateCount(Ticket ticket) {
        if (ticketDAO.addTicket(ticket)) {
            Event event = eventDAO.getEventById(ticket.getEventID());
            int newCount = event.getAvailableTickets() - 1;
            if (newCount < 0) newCount = 0;
            
            event.setAvailableTickets(newCount);
            eventDAO.updateEvent(event);
          
            if (newCount == 0 && !"Sold Out".equals(event.getStatus())) {
                event.setStatus("Sold Out");
                eventDAO.updateEvent(event);
                System.out.println("Event '" + event.getTitle() + "' is now SOLD OUT");
            }
        }
    }
    
    public boolean isLowTicketAlert(Event event) {
        LocalDate today = LocalDate.now();
        LocalDate eventDate = event.getEventDate().toLocalDate();
        long daysUntilEvent = java.time.temporal.ChronoUnit.DAYS.between(today, eventDate);
        
        int totalCapacity = event.getAvailableTickets() + getTicketsSoldCount(event.getEventID());
        double percentageRemaining = (double) event.getAvailableTickets() / totalCapacity * 100;
        
        return daysUntilEvent <= 7 && daysUntilEvent >= 0 && percentageRemaining < 10;
    }
    
    private int getTicketsSoldCount(int eventID) {
        List<Ticket> tickets = ticketDAO.getTicketsByUser(eventID); // This needs adjustment based on your DAO
        return tickets.size();
    }
    
    public boolean shouldAutoCancelEvent(Event event) {
        LocalDate today = LocalDate.now();
        LocalDate eventDate = event.getEventDate().toLocalDate();
        long daysUntilEvent = java.time.temporal.ChronoUnit.DAYS.between(today, eventDate);
        
        if (daysUntilEvent <= 7 && daysUntilEvent >= 0) {
            int totalCapacity = event.getAvailableTickets() + getTicketsSoldCount(event.getEventID());
            double percentageSold = (double) getTicketsSoldCount(event.getEventID()) / totalCapacity * 100;
            
          
            return percentageSold < 20;
        }
        return false;
    }
    
    
    public void generateAlerts() {
        List<Event> events = eventDAO.getAllEvents();
        System.out.println("\n=== EVENT ALERTS ===");
        
        for (Event event : events) {
            if ("Scheduled".equals(event.getStatus()) || "Ongoing".equals(event.getStatus())) {
                

                if (isLowTicketAlert(event)) {
                    System.out.println("LOW TICKETS: " + event.getTitle() + 
                                     " - Only " + event.getAvailableTickets() + " seats left!");
                }
                
            
                if (shouldAutoCancelEvent(event)) {
                    System.out.println("CONSIDER CANCELING: " + event.getTitle() + 
                                     " - Low sales with event approaching");
                }
                
             
                LocalDate today = LocalDate.now();
                long daysUntil = java.time.temporal.ChronoUnit.DAYS.between(
                    today, event.getEventDate().toLocalDate());
                
                if (daysUntil == 1) {
                    System.out.println("TOMORROW: " + event.getTitle() + 
                                     " - " + event.getAvailableTickets() + " tickets remaining");
                } else if (daysUntil == 0) {
                    System.out.println("TODAY: " + event.getTitle() + 
                                     " - " + event.getAvailableTickets() + " tickets remaining");
                }
            }
        }
        System.out.println("====================\n");
    }
    
    public boolean canUserSellTicket(String userRole) {
        return "Customer".equals(userRole) || "Admin".equals(userRole);
    }
    
    public void runAllAutomations() {
        autoUpdateEventStatus();
        generateAlerts();
    }
    
    public void runAllAutomationsWithLogging() {
        System.out.println("\n========================================");
        System.out.println("    RUNNING AUTOMATED TASKS");
        System.out.println("    Time: " + LocalDateTime.now());
        System.out.println("========================================\n");
        
        autoUpdateEventStatusWithLogging();
        generateAlerts();
        
        System.out.println("========================================");
        System.out.println("    AUTOMATION COMPLETE");
        System.out.println("========================================\n");
    }
}