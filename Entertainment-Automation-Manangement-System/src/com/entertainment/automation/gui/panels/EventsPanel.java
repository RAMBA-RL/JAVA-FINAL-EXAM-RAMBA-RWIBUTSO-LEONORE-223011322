package com.entertainment.automation.gui.panels;

import com.entertainment.automation.DatabaseConnection;
import com.entertainment.automation.dao.EventDAO;
import com.entertainment.automation.dao.ReviewDAO;
import com.entertainment.automation.dao.TicketDAO;
import com.entertainment.automation.dao.VenueDAO;
import com.entertainment.automation.models.Event;
import com.entertainment.automation.models.Review;
import com.entertainment.automation.models.TicketType;
import com.entertainment.automation.models.User;
import com.entertainment.automation.models.Venue;
import com.entertainment.automation.service.AutomationService;
import com.entertainment.automation.service.TicketPurchaseDialog;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class EventsPanel extends JPanel {

    private User currentUser;
    private JTable eventsTable;
    private DefaultTableModel tableModel;
    private EventDAO eventDAO;
    private TicketDAO ticketDAO;
    private ReviewDAO reviewDAO;
    private VenueDAO venueDAO;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public EventsPanel(User user) {
        this.currentUser = user;
        this.eventDAO = new EventDAO();
        this.ticketDAO = new TicketDAO();
        this.reviewDAO = new ReviewDAO();
        this.venueDAO = new VenueDAO();

        initializeUI();
        loadEvents();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 241));

        JLabel titleLabel = new JLabel("Events Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        String[] columnNames = {"Reference ID", "Title", "Venue", "Date", "Status", "Price Range", "Available"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        eventsTable = new JTable(tableModel);
        eventsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        eventsTable.setRowHeight(25);
        eventsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(eventsTable);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(new Color(236, 240, 241));

        JButton refreshButton = new JButton("Refresh");
        JButton addButton = new JButton("Add Event");
        JButton viewButton = new JButton("View Details");
        JButton editButton = new JButton("Edit Event");
        JButton deleteButton = new JButton("Delete Event");
        JButton buyButton = new JButton("Buy Ticket");
        JButton addReviewButton = new JButton("Add Review");

        styleButton(refreshButton, new Color(52, 152, 219));
        styleButton(addButton, new Color(46, 204, 113));
        styleButton(viewButton, new Color(155, 89, 182));
        styleButton(editButton, new Color(241, 196, 15));
        styleButton(deleteButton, new Color(231, 76, 60));
        styleButton(buyButton, new Color(155, 89, 182));
        styleButton(addReviewButton, new Color(46, 204, 113));

        buttonPanel.add(refreshButton);

        if ("Customer".equalsIgnoreCase(currentUser.getRole())) {
            buttonPanel.add(buyButton);
            buttonPanel.add(addReviewButton);
        } else {
            buttonPanel.add(addButton);
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);
        }
        buttonPanel.add(viewButton);

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action Listeners (Java 7 Compatible)
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { loadEvents(); }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { showAddEventDialog(); }
        });

        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { showEventDetails(); }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { showEditEventDialog(); }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { deleteEvent(); }
        });

        buyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { showBuyTicketDialog(); }
        });

        addReviewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { showAddReviewDialog(); }
        });
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }

    private void loadEvents() {
        tableModel.setRowCount(0);

        List<Event> events = eventDAO.getAllEvents();
        for (Event event : events) {
            String priceRange = getPriceRangeForEvent(event.getEventID());
            tableModel.addRow(new Object[]{
                event.getReferenceID(),
                event.getTitle(),
                event.getVenueName(),
                event.getEventDate().format(dateFormatter),
                event.getStatus(),
                priceRange,
                event.getAvailableTickets()
            });
        }
    }

    private String getPriceRangeForEvent(int eventID) {
        List<TicketType> ticketTypes = ticketDAO.getTicketTypesByEvent(eventID);

        if (ticketTypes.isEmpty()) return "No prices set";

        double min = ticketTypes.get(0).getPrice();
        double max = ticketTypes.get(0).getPrice();

        for (TicketType t : ticketTypes) {
            if (t.getPrice() < min) min = t.getPrice();
            if (t.getPrice() > max) max = t.getPrice();
        }

        if (min == max) return "RWF " + (int) min;
        return "RWF " + (int) min + " - " + (int) max;
    }

    /**
     * Helper method to parse date input - handles both date-only and date-time formats
     */
    private LocalDateTime parseDateInput(String dateInput) throws DateTimeParseException {
        String trimmed = dateInput.trim();
        
        // Try parsing as date-time first (yyyy-MM-dd HH:mm)
        try {
            return LocalDateTime.parse(trimmed, dateTimeFormatter);
        } catch (DateTimeParseException e1) {
            // Try parsing as date only (yyyy-MM-dd) and add default time of 18:00
            try {
                LocalDate date = LocalDate.parse(trimmed, dateFormatter);
                return LocalDateTime.of(date, LocalTime.of(18, 0)); // Default to 6:00 PM
            } catch (DateTimeParseException e2) {
                // If both fail, throw the exception
                throw new DateTimeParseException("Date must be in format 'yyyy-MM-dd' or 'yyyy-MM-dd HH:mm'", trimmed, 0);
            }
        }
    }

    private void showAddEventDialog() {
        final JDialog dialog = new JDialog((Frame) null, "Add New Event", true);
        dialog.setSize(540, 520);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setLocationRelativeTo(null);

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Event Details Section
        JPanel eventDetailsPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        TitledBorder eventDetailsBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            "Event Details",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            new Color(52, 152, 219)
        );
        eventDetailsPanel.setBorder(BorderFactory.createCompoundBorder(
            eventDetailsBorder,
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        final JTextField refField = new JTextField();
        final JTextField titleField = new JTextField();
        final JTextField descField = new JTextField();
        final JTextField dateField = new JTextField();
        final JTextField ticketsField = new JTextField();
        final JTextField remarksField = new JTextField();

        // Add placeholder/hint for date field
        dateField.setToolTipText("Format: yyyy-MM-dd or yyyy-MM-dd HH:mm (e.g., 2025-12-01 or 2025-12-01 18:00)");

        // Load venues dynamically
        final List<Venue> venues = venueDAO.getAllVenues();
        final DefaultComboBoxModel<String> venueModel = new DefaultComboBoxModel<String>();
        for (Venue v : venues) venueModel.addElement(v.getName());
        final JComboBox<String> venueCombo = new JComboBox<String>(venueModel);

        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setToolTipText("Format: yyyy-MM-dd or yyyy-MM-dd HH:mm");

        eventDetailsPanel.add(new JLabel("Reference ID:"));
        eventDetailsPanel.add(refField);
        eventDetailsPanel.add(new JLabel("Title:"));
        eventDetailsPanel.add(titleField);
        eventDetailsPanel.add(new JLabel("Description:"));
        eventDetailsPanel.add(descField);
        eventDetailsPanel.add(new JLabel("Venue:"));
        eventDetailsPanel.add(venueCombo);
        eventDetailsPanel.add(dateLabel);
        eventDetailsPanel.add(dateField);
        
        // Add hint label below date field
        JLabel dateHintLabel = new JLabel("<html><font size='2' color='gray'>Format: yyyy-MM-dd or yyyy-MM-dd HH:mm</font></html>");
        eventDetailsPanel.add(new JLabel("")); // Empty label for spacing
        eventDetailsPanel.add(dateHintLabel);
        
        eventDetailsPanel.add(new JLabel("Available Tickets:"));
        eventDetailsPanel.add(ticketsField);
        eventDetailsPanel.add(new JLabel("Remarks:"));
        eventDetailsPanel.add(remarksField);

        // Ticket Prices Section
        JPanel pricesPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        TitledBorder pricesBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(46, 204, 113), 2),
            "Ticket Prices (RWF)",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            new Color(46, 204, 113)
        );
        pricesPanel.setBorder(BorderFactory.createCompoundBorder(
            pricesBorder,
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        final JTextField regularPriceField = new JTextField();
        final JTextField vipPriceField = new JTextField();

        pricesPanel.add(new JLabel("Regular Price:"));
        pricesPanel.add(regularPriceField);
        pricesPanel.add(new JLabel("VIP Price:"));
        pricesPanel.add(vipPriceField);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton saveBtn = new JButton("Save Event");
        JButton cancelBtn = new JButton("Cancel");

        styleButton(saveBtn, new Color(46, 204, 113));
        styleButton(cancelBtn, new Color(231, 76, 60));

        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Validate required fields
                    if (refField.getText().trim().isEmpty()) {
                        showStyledMessageDialog(dialog, "Error", "Reference ID is required!", new Color(231, 76, 60));
                        return;
                    }
                    if (titleField.getText().trim().isEmpty()) {
                        showStyledMessageDialog(dialog, "Error", "Title is required!", new Color(231, 76, 60));
                        return;
                    }
                    if (dateField.getText().trim().isEmpty()) {
                        showStyledMessageDialog(dialog, "Error", "Date is required!", new Color(231, 76, 60));
                        return;
                    }
                    if (ticketsField.getText().trim().isEmpty()) {
                        showStyledMessageDialog(dialog, "Error", "Available Tickets is required!", new Color(231, 76, 60));
                        return;
                    }
                    if (regularPriceField.getText().trim().isEmpty()) {
                        showStyledMessageDialog(dialog, "Error", "Regular Price is required!", new Color(231, 76, 60));
                        return;
                    }
                    if (vipPriceField.getText().trim().isEmpty()) {
                        showStyledMessageDialog(dialog, "Error", "VIP Price is required!", new Color(231, 76, 60));
                        return;
                    }

                    Event newEvent = new Event();
                    newEvent.setReferenceID(refField.getText().trim());
                    newEvent.setTitle(titleField.getText().trim());
                    newEvent.setDescription(descField.getText().trim());
                    
                    // Parse date with improved handling
                    LocalDateTime eventDateTime = parseDateInput(dateField.getText());
                    newEvent.setEventDate(eventDateTime);
                    
                    newEvent.setAvailableTickets(Integer.parseInt(ticketsField.getText().trim()));
                    newEvent.setRemarks(remarksField.getText().trim());
                    newEvent.setStatus("Scheduled");
                    newEvent.setCreatedBy(currentUser.getUserID());

                    // Prices
                    double regularPrice = Double.parseDouble(regularPriceField.getText().trim());
                    double vipPrice = Double.parseDouble(vipPriceField.getText().trim());

                    newEvent.setTicketPrice(regularPrice);

                    // Venue
                    Venue selectedVenue = venues.get(venueCombo.getSelectedIndex());
                    newEvent.setVenueID(selectedVenue.getVenueID());
                    newEvent.setVenueName(selectedVenue.getName());

                    int eventId = eventDAO.addEventAndGetId(newEvent);
                    if (eventId > 0 && ticketDAO.addTicketTypesForEvent(eventId, regularPrice, vipPrice)) {
                        showStyledMessageDialog(dialog, "Success", "Event added successfully!", new Color(46, 204, 113));
                        dialog.dispose();
                        loadEvents();
                    } else {
                        showStyledMessageDialog(dialog, "Error", "Failed to add event!", new Color(231, 76, 60));
                    }
                } catch (DateTimeParseException dtpe) {
                    showStyledMessageDialog(dialog, "Error", 
                        "Invalid date format!\n\nPlease use:\n- yyyy-MM-dd (e.g., 2025-12-01)\n- yyyy-MM-dd HH:mm (e.g., 2025-12-01 18:00)", 
                        new Color(231, 76, 60));
                } catch (NumberFormatException nfe) {
                    showStyledMessageDialog(dialog, "Error", "Please enter valid numbers for tickets and prices!", new Color(231, 76, 60));
                } catch (Exception ex) {
                    showStyledMessageDialog(dialog, "Error", "Error: " + ex.getMessage(), new Color(231, 76, 60));
                    ex.printStackTrace();
                }
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { dialog.dispose(); }
        });

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        // Combine panels
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.add(eventDetailsPanel, BorderLayout.CENTER);
        contentPanel.add(pricesPanel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void showEditEventDialog() {
        int row = eventsTable.getSelectedRow();
        if (row < 0) {
            showStyledMessageDialog(this, "Warning", "Please select an event to edit.", new Color(241, 196, 15));
            return;
        }
        String refId = (String) tableModel.getValueAt(row, 0);
        final Event event = eventDAO.getEventByReferenceId(refId);
        if (event == null) return;

        final JDialog dialog = new JDialog((Frame) null, "Edit Event", true);
        dialog.setSize(540, 480);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setLocationRelativeTo(null);

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Event Details Section
        JPanel eventDetailsPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        TitledBorder eventDetailsBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            "Event Details",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            new Color(52, 152, 219)
        );
        eventDetailsPanel.setBorder(BorderFactory.createCompoundBorder(
            eventDetailsBorder,
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        final JTextField titleField = new JTextField(event.getTitle());
        final JTextField descField = new JTextField(event.getDescription());
        
        // Format date with time - handle cases where time might be missing
        String formattedDate;
        try {
            formattedDate = event.getEventDate().format(dateTimeFormatter);
        } catch (Exception ex) {
            formattedDate = event.getEventDate().format(dateFormatter);
        }
        final JTextField dateField = new JTextField(formattedDate);
        
        final JTextField ticketsField = new JTextField(String.valueOf(event.getAvailableTickets()));
        final JTextField remarksField = new JTextField(event.getRemarks() != null ? event.getRemarks() : "");
        final JTextField regularPriceField = new JTextField(String.valueOf(event.getTicketPrice()));

        dateField.setToolTipText("Format: yyyy-MM-dd or yyyy-MM-dd HH:mm");

        final List<Venue> venues = venueDAO.getAllVenues();
        final DefaultComboBoxModel<String> venueModel = new DefaultComboBoxModel<String>();
        int selectedIndex = 0;
        for (int i = 0; i < venues.size(); i++) {
            Venue v = venues.get(i);
            venueModel.addElement(v.getName());
            if (v.getVenueID() == event.getVenueID()) selectedIndex = i;
        }
        final JComboBox<String> venueCombo = new JComboBox<String>(venueModel);
        venueCombo.setSelectedIndex(selectedIndex);

        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setToolTipText("Format: yyyy-MM-dd or yyyy-MM-dd HH:mm");

        eventDetailsPanel.add(new JLabel("Title:"));
        eventDetailsPanel.add(titleField);
        eventDetailsPanel.add(new JLabel("Description:"));
        eventDetailsPanel.add(descField);
        eventDetailsPanel.add(dateLabel);
        eventDetailsPanel.add(dateField);
        
        // Add hint label
        JLabel dateHintLabel = new JLabel("<html><font size='2' color='gray'>Format: yyyy-MM-dd or yyyy-MM-dd HH:mm</font></html>");
        eventDetailsPanel.add(new JLabel(""));
        eventDetailsPanel.add(dateHintLabel);
        
        eventDetailsPanel.add(new JLabel("Tickets:"));
        eventDetailsPanel.add(ticketsField);
        eventDetailsPanel.add(new JLabel("Remarks:"));
        eventDetailsPanel.add(remarksField);
        eventDetailsPanel.add(new JLabel("Regular Price:"));
        eventDetailsPanel.add(regularPriceField);
        eventDetailsPanel.add(new JLabel("Venue:"));
        eventDetailsPanel.add(venueCombo);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton saveBtn = new JButton("Save Changes");
        JButton cancelBtn = new JButton("Cancel");

        styleButton(saveBtn, new Color(46, 204, 113));
        styleButton(cancelBtn, new Color(231, 76, 60));

        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Validate required fields
                    if (titleField.getText().trim().isEmpty()) {
                        showStyledMessageDialog(dialog, "Error", "Title is required!", new Color(231, 76, 60));
                        return;
                    }
                    if (dateField.getText().trim().isEmpty()) {
                        showStyledMessageDialog(dialog, "Error", "Date is required!", new Color(231, 76, 60));
                        return;
                    }
                    if (ticketsField.getText().trim().isEmpty()) {
                        showStyledMessageDialog(dialog, "Error", "Available Tickets is required!", new Color(231, 76, 60));
                        return;
                    }
                    if (regularPriceField.getText().trim().isEmpty()) {
                        showStyledMessageDialog(dialog, "Error", "Regular Price is required!", new Color(231, 76, 60));
                        return;
                    }
                    
                    event.setTitle(titleField.getText().trim());
                    event.setDescription(descField.getText().trim());
                    
                    // Parse date with improved handling
                    LocalDateTime eventDateTime = parseDateInput(dateField.getText());
                    event.setEventDate(eventDateTime);
                    
                    event.setAvailableTickets(Integer.parseInt(ticketsField.getText().trim()));
                    event.setRemarks(remarksField.getText().trim());
                    event.setTicketPrice(Double.parseDouble(regularPriceField.getText().trim()));

                    Venue selectedVenue = venues.get(venueCombo.getSelectedIndex());
                    event.setVenueID(selectedVenue.getVenueID());
                    event.setVenueName(selectedVenue.getName());

                    if (eventDAO.updateEvent(event)) {
                        showStyledMessageDialog(dialog, "Success", "Event updated successfully!", new Color(46, 204, 113));
                        dialog.dispose();
                        loadEvents();
                    } else {
                        showStyledMessageDialog(dialog, "Error", "Failed to update event!", new Color(231, 76, 60));
                    }
                } catch (DateTimeParseException dtpe) {
                    showStyledMessageDialog(dialog, "Error", 
                        "Invalid date format!\n\nPlease use:\n- yyyy-MM-dd (e.g., 2025-12-01)\n- yyyy-MM-dd HH:mm (e.g., 2025-12-01 18:00)", 
                        new Color(231, 76, 60));
                    dtpe.printStackTrace();
                } catch (NumberFormatException nfe) {
                    showStyledMessageDialog(dialog, "Error", "Please enter valid numbers for tickets and prices!", new Color(231, 76, 60));
                    nfe.printStackTrace();
                } catch (Exception ex) {
                    showStyledMessageDialog(dialog, "Error", "Error: " + ex.getMessage(), new Color(231, 76, 60));
                    ex.printStackTrace();
                }
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { dialog.dispose(); }
        });

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        mainPanel.add(eventDetailsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void deleteEvent() {
        int row = eventsTable.getSelectedRow();
        if (row < 0) return;

        String refId = (String) tableModel.getValueAt(row, 0);
        Event event = eventDAO.getEventByReferenceId(refId);
        if (event == null) return;

        boolean confirmed = showStyledConfirmDialog(
            this, 
            "Delete Event", 
            "Are you sure you want to delete the event:\n"+ event.getTitle() + "?\n\nThis action cannot be undone."
        );
        
        if (confirmed && eventDAO.deleteEvent(event.getEventID())) {
            showStyledMessageDialog(this, "Success", "Event deleted successfully!", new Color(46, 204, 113));
            loadEvents();
        }
    }

    private void showEventDetails() {
        int row = eventsTable.getSelectedRow();
        if (row < 0) {
            showStyledMessageDialog(this, "Warning", "Please select an event to view details.", new Color(241, 196, 15));
            return;
        }

        String refId = (String) tableModel.getValueAt(row, 0);
        Event event = eventDAO.getEventByReferenceId(refId);
        if (event == null) return;

        // Create the dialog matching Image 1 style
        final JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Event Details - " + event.getTitle(), true);
        dialog.setSize(500, 450);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(Color.WHITE);

        // Details panel with border
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(15, 15, 5, 15),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
                "Details",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                new Color(52, 152, 219)
            )
        ));

        // Build details text
        StringBuilder detailsText = new StringBuilder();
        detailsText.append("Event: ").append(event.getTitle()).append("\n\n");
        detailsText.append("Venue: ").append(event.getVenueName()).append("\n\n");
        detailsText.append("Date: ").append(event.getEventDate().format(dateTimeFormatter)).append("\n\n");
        
        // Available tickets with SOLD OUT in red if 0
        detailsText.append("Available Tickets: ").append(event.getAvailableTickets());
        boolean isSoldOut = (event.getAvailableTickets() <= 0);
        if (!isSoldOut) {
            detailsText.append("\n\n");
        }

        // Get ticket prices
        List<TicketType> tickets = ticketDAO.getTicketTypesByEvent(event.getEventID());
        if (!tickets.isEmpty()) {
            detailsText.append("\nTicket Prices:\n");
            for (TicketType t : tickets) {
                detailsText.append(t.getTypeName()).append(": RWF ").append((int) t.getPrice()).append("\n");
            }
        }

        // Text area for details
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textPane.setBackground(Color.WHITE);
        textPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Set text content
        textPane.setText(detailsText.toString());
        
        // If sold out, add red " (SOLD OUT)" text
        if (isSoldOut) {
            javax.swing.text.StyledDocument doc = textPane.getStyledDocument();
            javax.swing.text.Style style = textPane.addStyle("Red", null);
            javax.swing.text.StyleConstants.setForeground(style, Color.RED);
            javax.swing.text.StyleConstants.setBold(style, true);
            
            try {
                int ticketLineStart = detailsText.indexOf("Available Tickets: " + event.getAvailableTickets());
                int insertPos = ticketLineStart + ("Available Tickets: " + event.getAvailableTickets()).length();
                doc.insertString(insertPos, " (SOLD OUT)", style);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setBorder(null);
        detailsPanel.add(scrollPane, BorderLayout.CENTER);

        // OK button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));

        JButton okButton = new JButton("OK");
        okButton.setBackground(new Color(52, 152, 219));
        okButton.setForeground(Color.WHITE);
        okButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        okButton.setFocusPainted(false);
        okButton.setBorder(BorderFactory.createEmptyBorder(8, 30, 8, 30));
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        buttonPanel.add(okButton);

        mainPanel.add(detailsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void showBuyTicketDialog() {
        int row = eventsTable.getSelectedRow();
        if (row < 0) return;

        String refId = (String) tableModel.getValueAt(row, 0);
        Event event = eventDAO.getEventByReferenceId(refId);
        if (event == null || event.getAvailableTickets() <= 0) {
            showStyledMessageDialog(this, "Warning", "No tickets available for this event.", new Color(241, 196, 15));
            return;
        }

        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        TicketPurchaseDialog dialog = new TicketPurchaseDialog(parentFrame, event.getEventID(), row, event.getTitle(), currentUser);
        dialog.setVisible(true);

        if (dialog.isPurchaseSuccessful()) {
            event.setAvailableTickets(event.getAvailableTickets() - 1);
            eventDAO.updateEvent(event);
            loadEvents();
        }
    }

    private void showAddReviewDialog() {
        int row = eventsTable.getSelectedRow();
        if (row < 0) {
            showStyledMessageDialog(this, "Warning", "Please select an event to review.", new Color(241, 196, 15));
            return;
        }

        String refId = (String) tableModel.getValueAt(row, 0);
        final Event event = eventDAO.getEventByReferenceId(refId);
        if (event == null) return;

        final JDialog dialog = new JDialog((Frame) null, "Add Review", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 1));
        final JComboBox<Integer> ratingCombo = new JComboBox<Integer>(new Integer[]{1,2,3,4,5});
        final JTextArea commentArea = new JTextArea();
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Review review = new Review();
                review.setEventID(event.getEventID());
                review.setUserID(currentUser.getUserID());
                review.setRating((Integer) ratingCombo.getSelectedItem());
                review.setComment(commentArea.getText().trim());
                if (reviewDAO.addReview(review)) {
                    showStyledMessageDialog(dialog, "Success", "Review added successfully!", new Color(46, 204, 113));
                    dialog.dispose();
                    loadEvents();
                } else {
                    showStyledMessageDialog(dialog, "Error", "Failed to add review!", new Color(231, 76, 60));
                }
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { dialog.dispose(); }
        });

        panel.add(new JLabel("Rating:")); panel.add(ratingCombo);
        panel.add(new JLabel("Comment:")); panel.add(new JScrollPane(commentArea));

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(saveBtn); btnPanel.add(cancelBtn);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ===== STYLED DIALOG METHODS =====

    private boolean showStyledConfirmDialog(Component parent, String title, String message) {
        final JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), title, true);
        dialog.setSize(450, 250);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        TitledBorder contentBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(230, 126, 34), 2),
            "Confirmation",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            new Color(230, 126, 34)
        );
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            contentBorder,
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JTextArea messageArea = new JTextArea(message);
        messageArea.setEditable(false);
        messageArea.setWrapStyleWord(true);
        messageArea.setLineWrap(true);
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageArea.setForeground(Color.BLACK);
        messageArea.setBackground(Color.WHITE);
        messageArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentPanel.add(messageArea, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        JButton yesButton = new JButton("Yes");
        JButton noButton = new JButton("No");

        styleButton(yesButton, new Color(46, 204, 113));
        styleButton(noButton, new Color(231, 76, 60));

        final boolean[] result = {false};

        yesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                result[0] = true;
                dialog.dispose();
            }
        });

        noButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                result[0] = false;
                dialog.dispose();
            }
        });

        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);

        return result[0];
    }

    private void showStyledMessageDialog(Component parent, String title, String message, Color borderColor) {
        final JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), title, true);
        dialog.setSize(450, 220);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        TitledBorder contentBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(borderColor, 2),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            borderColor
        );
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            contentBorder,
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel messageLabel = new JLabel("<html><div style='width: 350px; text-align: left;'>" + message + "</div></html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(Color.BLACK);
        messageLabel.setVerticalAlignment(SwingConstants.TOP);
        contentPanel.add(messageLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        JButton okButton = new JButton("OK");
        styleButton(okButton, borderColor);

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        buttonPanel.add(okButton);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
}