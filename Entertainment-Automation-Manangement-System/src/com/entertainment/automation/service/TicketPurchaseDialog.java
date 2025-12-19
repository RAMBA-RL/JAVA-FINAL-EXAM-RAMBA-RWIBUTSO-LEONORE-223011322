package com.entertainment.automation.service;

import com.entertainment.automation.dao.TicketDAO;
import com.entertainment.automation.dao.VenueDAO;
import com.entertainment.automation.models.Ticket;
import com.entertainment.automation.models.TicketType;
import com.entertainment.automation.models.User;
import com.entertainment.automation.models.Venue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public class TicketPurchaseDialog extends JDialog {
    private TicketDAO ticketDAO;
    private VenueDAO venueDAO;
    private int eventID;
    private int venueID;
    private String eventTitle;
    private User currentUser;
    private JComboBox<String> ticketTypeCombo;
    private List<TicketType> ticketTypes;
    private JTextField seatNumberField;
    private JLabel priceLabel;
    private JButton checkAvailabilityBtn;
    private JButton showAvailableSeatsBtn;
    private boolean purchaseSuccessful = false;
    
    public TicketPurchaseDialog(Frame parent, int eventID, int venueID, String eventTitle, User currentUser) {
        super(parent, "Buy Ticket - " + eventTitle, true);
        this.ticketDAO = new TicketDAO();
        this.venueDAO = new VenueDAO();
        this.eventID = eventID;
        this.venueID = venueID;
        this.eventTitle = eventTitle;
        this.currentUser = currentUser;
        
        initializeUI();
        loadTicketTypes();
        
        setSize(500, 500);
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Buy Ticket - " + eventTitle);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        // Ticket Type Label
        gbc.gridy = 0;
        JLabel typeLabel = new JLabel("Select Ticket Type:");
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(typeLabel, gbc);
        
        // Ticket Type Combo
        gbc.gridy = 1;
        ticketTypeCombo = new JComboBox<>();
        ticketTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ticketTypeCombo.setPreferredSize(new Dimension(420, 35));
        ticketTypeCombo.setBackground(Color.WHITE);
        formPanel.add(ticketTypeCombo, gbc);
        
        // Space
        gbc.gridy = 2;
        formPanel.add(Box.createVerticalStrut(15), gbc);

        // Seat Number Label
        gbc.gridy = 3;
        JLabel seatLabel = new JLabel("Seat Number:");
        seatLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(seatLabel, gbc);
        
        // Seat Number Field with buttons panel
        gbc.gridy = 4;
        JPanel seatPanel = new JPanel(new BorderLayout(10, 0));
        seatPanel.setBackground(Color.WHITE);
        
        seatNumberField = new JTextField();
        seatNumberField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        seatNumberField.setPreferredSize(new Dimension(250, 35));
        seatNumberField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        checkAvailabilityBtn = new JButton("Check");
        checkAvailabilityBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        checkAvailabilityBtn.setBackground(new Color(52, 152, 219));
        checkAvailabilityBtn.setForeground(Color.WHITE);
        checkAvailabilityBtn.setFocusPainted(false);
        checkAvailabilityBtn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        checkAvailabilityBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        seatPanel.add(seatNumberField, BorderLayout.CENTER);
        seatPanel.add(checkAvailabilityBtn, BorderLayout.EAST);
        formPanel.add(seatPanel, gbc);
        
        // Show Available Seats Button
        gbc.gridy = 5;
        gbc.insets = new Insets(5, 0, 15, 0);
        showAvailableSeatsBtn = new JButton("Show Available Seats");
        showAvailableSeatsBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        showAvailableSeatsBtn.setBackground(new Color(155, 89, 182));
        showAvailableSeatsBtn.setForeground(Color.WHITE);
        showAvailableSeatsBtn.setFocusPainted(false);
        showAvailableSeatsBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        showAvailableSeatsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formPanel.add(showAvailableSeatsBtn, gbc);
        
        // Reset insets
        gbc.insets = new Insets(5, 0, 5, 0);

        // Price Panel
        gbc.gridy = 6;
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pricePanel.setBackground(new Color(232, 246, 239));
        pricePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(46, 204, 113), 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        JLabel priceTitleLabel = new JLabel("Price:");
        priceTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        priceTitleLabel.setForeground(new Color(44, 62, 80));
        
        priceLabel = new JLabel("RWF 0");
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        priceLabel.setForeground(new Color(39, 174, 96));
        
        pricePanel.add(priceTitleLabel);
        pricePanel.add(priceLabel);
        formPanel.add(pricePanel, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(Color.WHITE);

        JButton purchaseButton = new JButton("Purchase Ticket");
        JButton cancelButton = new JButton("Cancel");

        styleButton(purchaseButton, new Color(39, 174, 96), Color.WHITE);
        styleButton(cancelButton, new Color(149, 165, 166), Color.WHITE);

        buttonPanel.add(purchaseButton);
        buttonPanel.add(cancelButton);

        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Event listeners
        ticketTypeCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePrice();
            }
        });

        checkAvailabilityBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkSeatAvailability();
            }
        });
        
        showAvailableSeatsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAvailableSeats();
            }
        });

        purchaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handlePurchase();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void loadTicketTypes() {
        ticketTypes = ticketDAO.getTicketTypesByEvent(eventID);
        
        if (ticketTypes.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No ticket types available for this event",
                "No Tickets",
                JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }

        int index = 1;
        for (TicketType type : ticketTypes) {
            String option = String.format("%d. %s - RWF %.0f", 
                index++, type.getTypeName(), type.getPrice());
            ticketTypeCombo.addItem(option);
        }

        if (ticketTypes.size() > 0) {
            ticketTypeCombo.setSelectedIndex(0);
            updatePrice();
        }
    }
    private void updatePrice() {
        int selectedIndex = ticketTypeCombo.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < ticketTypes.size()) {
            TicketType selected = ticketTypes.get(selectedIndex);
            priceLabel.setText(String.format("RWF %.0f", selected.getPrice()));
        }
    }
    
    private void checkSeatAvailability() {
        String seatNumber = seatNumberField.getText().trim();
        
        if (seatNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a seat number first!",
                "No Seat Number",
                JOptionPane.WARNING_MESSAGE);
            seatNumberField.requestFocus();
            return;
        }
        
        if (ticketDAO.isSeatTaken(eventID, seatNumber)) {
            JOptionPane.showMessageDialog(this,
                "Seat " + seatNumber + " is already taken!\n" +
                "Please choose another seat.",
                "Seat Unavailable",
                JOptionPane.ERROR_MESSAGE);
            seatNumberField.selectAll();
            seatNumberField.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this,
                "Seat " + seatNumber + " is available!\n" +
                "You can proceed with purchase.",
                "Seat Available",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    private void showAvailableSeats() {
        Venue venue = venueDAO.getVenueById(venueID);
        
        if (venue == null) {
            JOptionPane.showMessageDialog(this,
                "Unable to retrieve venue information.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int totalSeats = venue.getCapacity();
        int takenSeats = ticketDAO.getTakenSeatsCount(eventID);
        int availableCount = totalSeats - takenSeats;
        
        if (availableCount <= 0) {
            JOptionPane.showMessageDialog(this,
                "No seats available! This event is SOLD OUT.",
                "Sold Out",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<String> availableSeats = ticketDAO.getAvailableSeats(eventID, totalSeats);
        
        // Create display text
        StringBuilder displayText = new StringBuilder();
        displayText.append(String.format("Total Seats: %d\n", totalSeats));
        displayText.append(String.format("Taken: %d\n", takenSeats));
        displayText.append(String.format("Available: %d\n\n", availableCount));
        displayText.append("Available Seats:\n");
        displayText.append("--------------------------------\n");
        
        int count = 0;
        for (String seat : availableSeats) {
            displayText.append(String.format("%-6s", seat));
            count++;
            if (count % 10 == 0) {
                displayText.append("\n");
            }
        }
        JTextArea seatsArea = new JTextArea(displayText.toString());
        seatsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        seatsArea.setEditable(false);
        seatsArea.setBackground(new Color(245, 245, 245));
        seatsArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(seatsArea);
        scrollPane.setPreferredSize(new Dimension(450, 300));
        
        JOptionPane.showMessageDialog(this,
            scrollPane,
            "Available Seats - " + eventTitle,
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void handlePurchase() {
        int selectedIndex = ticketTypeCombo.getSelectedIndex();
        if (selectedIndex < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a ticket type",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String seatNumber = seatNumberField.getText().trim();
        if (seatNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a seat number",
                "Missing Information",
                JOptionPane.WARNING_MESSAGE);
            seatNumberField.requestFocus();
            return;
        }
        
        if (ticketDAO.isSeatTaken(eventID, seatNumber)) {
            JOptionPane.showMessageDialog(this,
                "Sorry, seat " + seatNumber + " is already taken!\n" +
                "Please choose another seat.",
                "Seat Unavailable",
                JOptionPane.ERROR_MESSAGE);
            seatNumberField.selectAll();
            seatNumberField.requestFocus();
            return;
        }

        TicketType selectedType = ticketTypes.get(selectedIndex);

        Ticket ticket = new Ticket();
        ticket.setEventID(eventID);
        ticket.setUserID(currentUser.getUserID());
        ticket.setTicketTypeID(selectedType.getTicketTypeID());
        ticket.setTicketNumber(generateTicketNumber());
        ticket.setSeatNumber(seatNumber);
        ticket.setTicketType(selectedType.getTypeName());
        ticket.setPrice(selectedType.getPrice());
        ticket.setStatus("Active");
        ticket.setPurchaseDate(LocalDateTime.now());

        if (ticketDAO.addTicket(ticket)) {
            purchaseSuccessful = true;
            JOptionPane.showMessageDialog(this,
                String.format("Ticket Purchased Successfully!\n\n" +
                    "Ticket No: %s\n" +
                    "Type: %s\n" +
                    "Seat: %s\n" +
                    "Price: RWF %.0f\n\n" +
                    "Thank you for your purchase!",
                    ticket.getTicketNumber(),
                    ticket.getTicketType(),
                    ticket.getSeatNumber(),
                    ticket.getPrice()),
                "Purchase Success",
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to purchase ticket.\nThe seat may have been taken by another customer.",
                "Purchase Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private String generateTicketNumber() {
        return "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public boolean isPurchaseSuccessful() {
        return purchaseSuccessful;
    }
}