package com.entertainment.automation.service;

import com.entertainment.automation.dao.EventDAO;
import com.entertainment.automation.dao.VenueDAO;
import com.entertainment.automation.models.Event;
import com.entertainment.automation.models.User;
import com.entertainment.automation.models.Venue;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EventDialog extends JDialog {
    private EventDAO eventDAO;
    private VenueDAO venueDAO;
    private Event event;
    private User currentUser;
    private boolean saved = false;
    
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JTextField dateField;
    private JComboBox<String> venueCombo;
    private JTextField priceField;
    private JTextField ticketsField;
    private JComboBox<String> statusCombo;
    private JTextArea remarksArea;

    public EventDialog(JFrame parent, Event event, User currentUser) {
        super(parent, event == null ? "Add New Event" : "Edit Event", true);
        this.event = event;
        this.currentUser = currentUser;
        this.eventDAO = new EventDAO();
        this.venueDAO = new VenueDAO();
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setSize(500, 600);
        setLocationRelativeTo(getParent());
        
        JPanel formPanel = new JPanel(new GridLayout(9, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        formPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        formPanel.add(titleField);
        
        // Description
        formPanel.add(new JLabel("Description:"));
        descriptionArea = new JTextArea(3, 20);
        formPanel.add(new JScrollPane(descriptionArea));
        
        // Date
        formPanel.add(new JLabel("Event Date (yyyy-MM-dd HH:mm):"));
        dateField = new JTextField();
        formPanel.add(dateField);
        
        // Venue
        formPanel.add(new JLabel("Venue:"));
        venueCombo = new JComboBox<>();
        loadVenues();
        formPanel.add(venueCombo);
        
        // Ticket Price
        formPanel.add(new JLabel("Ticket Price (RWF):"));
        priceField = new JTextField();
        formPanel.add(priceField);
        
        // Available Tickets
        formPanel.add(new JLabel("Available Tickets:"));
        ticketsField = new JTextField();
        formPanel.add(ticketsField);
        
        // Status
        formPanel.add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(new String[]{"Active", "Completed", "Cancelled", "Sold Out"});
        formPanel.add(statusCombo);
        
        // Remarks
        formPanel.add(new JLabel("Remarks:"));
        remarksArea = new JTextArea(2, 20);
        formPanel.add(new JScrollPane(remarksArea));
        
        if (event != null) {
            loadEventData();
        }
        
        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        
        saveBtn.setBackground(new Color(46, 204, 113));
        saveBtn.setForeground(Color.WHITE);
        cancelBtn.setBackground(new Color(231, 76, 60));
        cancelBtn.setForeground(Color.WHITE);
        
        saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveEvent();
            }
        });
        
        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadVenues() {
        List<Venue> venues = venueDAO.getAllVenues();
        for (Venue venue : venues) {
            venueCombo.addItem(venue.getName());
        }
    }
    
    private void loadEventData() {
        if (event != null) {
            titleField.setText(event.getTitle());
            descriptionArea.setText(event.getDescription());
            
            if (event.getEventDate() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                dateField.setText(event.getEventDate().format(formatter));
            }
            
            priceField.setText(String.valueOf(event.getTicketPrice()));
            ticketsField.setText(String.valueOf(event.getAvailableTickets()));
            statusCombo.setSelectedItem(event.getStatus());
            remarksArea.setText(event.getRemarks());
            
            // Set venue if available
            if (event.getVenueName() != null) {
                venueCombo.setSelectedItem(event.getVenueName());
            }
        }
    }
    
    private void saveEvent() {
        try {
            if (event == null) {
                event = new Event();
                event.setReferenceID(generateReferenceID());
            }
            
            // Validate fields
            if (titleField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter event title!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Set event properties
            event.setTitle(titleField.getText().trim());
            event.setDescription(descriptionArea.getText().trim());
            event.setTicketPrice(Double.parseDouble(priceField.getText().trim()));
            event.setAvailableTickets(Integer.parseInt(ticketsField.getText().trim()));
            event.setStatus((String) statusCombo.getSelectedItem());
            event.setRemarks(remarksArea.getText().trim());
            event.setCreatedBy(currentUser.getUserID());
            
            // Parse date
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime eventDate = LocalDateTime.parse(dateField.getText().trim(), formatter);
                event.setEventDate(eventDate);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid date format! Use: yyyy-MM-dd HH:mm", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get venue ID from selected venue name
            String selectedVenue = (String) venueCombo.getSelectedItem();
            List<Venue> venues = venueDAO.getAllVenues();
            for (Venue venue : venues) {
                if (venue.getName().equals(selectedVenue)) {
                    event.setVenueID(venue.getVenueID());
                    break;
                }
            }
            
            // Save event
            boolean success;
            if (event.getEventID() == 0) {
                success = eventDAO.addEvent(event);
            } else {
                success = eventDAO.updateEvent(event);
            }
            
            if (success) {
                saved = true;
                JOptionPane.showMessageDialog(this, "Event saved successfully!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error saving event!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for price and tickets!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String generateReferenceID() {
        return "EVT" + System.currentTimeMillis();
    }
    
    public boolean isSaved() {
        return saved;
    }
    
    public Event getEvent() {
        return event;
    }
}