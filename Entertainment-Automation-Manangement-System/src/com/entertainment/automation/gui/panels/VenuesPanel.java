package com.entertainment.automation.gui.panels;

import com.entertainment.automation.dao.VenueDAO;
import com.entertainment.automation.models.Venue;
import com.entertainment.automation.models.User;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class VenuesPanel extends JPanel {
    private User currentUser;
    private JTable venuesTable;
    private DefaultTableModel tableModel;
    private VenueDAO venueDAO;

    public VenuesPanel(User user) {
        this.currentUser = user;
        this.venueDAO = new VenueDAO();
        initializeUI();
        loadVenues();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 241));

        // Title
        JLabel titleLabel = new JLabel("Venues Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Table
        String[] columnNames = {"Name", "Address", "Capacity", "Manager", "Contact"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        venuesTable = new JTable(tableModel);
        venuesTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        venuesTable.setRowHeight(25);
        venuesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(venuesTable);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(new Color(236, 240, 241));

        JButton refreshButton = new JButton("Refresh");
        JButton addButton = new JButton("Add Venue");
        JButton viewButton = new JButton("View Details");

        // Style buttons
        styleButton(refreshButton, new Color(52, 152, 219));
        styleButton(addButton, new Color(46, 204, 113));
        styleButton(viewButton, new Color(155, 89, 182));

        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);

        // Add components
        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

    
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadVenues();
            }
        });
        
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddVenueDialog();
            }
        });
        
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showVenueDetails();
            }
        });
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void loadVenues() {
        tableModel.setRowCount(0);
        List<Venue> venues = venueDAO.getAllVenues();

        for (Venue venue : venues) {
            tableModel.addRow(new Object[]{
                venue.getName(),
                venue.getAddress(),
                venue.getCapacity(),
                venue.getManager(),
                venue.getContact()
            });
        }
    }

    private void showAddVenueDialog() {
        final JDialog addDialog = new JDialog();
        addDialog.setTitle("Add New Venue");
        addDialog.setModal(true);
        addDialog.setSize(400, 300);
        addDialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        final JTextField nameField = new JTextField();
        final JTextField addressField = new JTextField();
        final JTextField capacityField = new JTextField();
        final JTextField managerField = new JTextField();
        final JTextField contactField = new JTextField();

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Address:"));
        formPanel.add(addressField);
        formPanel.add(new JLabel("Capacity:"));
        formPanel.add(capacityField);
        formPanel.add(new JLabel("Manager:"));
        formPanel.add(managerField);
        formPanel.add(new JLabel("Contact:"));
        formPanel.add(contactField);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        styleButton(saveButton, new Color(46, 204, 113));
        styleButton(cancelButton, new Color(231, 76, 60));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        addDialog.add(formPanel, BorderLayout.CENTER);
        addDialog.add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Venue newVenue = new Venue();
                newVenue.setName(nameField.getText());
                newVenue.setAddress(addressField.getText());
                newVenue.setCapacity(Integer.parseInt(capacityField.getText()));
                newVenue.setManager(managerField.getText());
                newVenue.setContact(contactField.getText());
                
                if (venueDAO.addVenue(newVenue)) {
                    showSuccessDialog(addDialog, "Venue added successfully!");
                    addDialog.dispose();
                    loadVenues();
                } else {
                    showErrorDialog(addDialog, "Error adding venue!");
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addDialog.dispose();
            }
        });

        addDialog.setVisible(true);
    }

    private void showVenueDetails() {
        int selectedRow = venuesTable.getSelectedRow();
        if (selectedRow < 0) {
            showWarningDialog("Please select a venue first");
            return;
        }

        String venueName = (String) tableModel.getValueAt(selectedRow, 0);
        String address = (String) tableModel.getValueAt(selectedRow, 1);
        String capacity = tableModel.getValueAt(selectedRow, 2).toString();
        String manager = (String) tableModel.getValueAt(selectedRow, 3);
        String contact = (String) tableModel.getValueAt(selectedRow, 4);

        // Create the dialog matching Image 1 style
        final JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Venue Details - " + venueName, true);
        dialog.setSize(500, 400);
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
        String detailsText = String.format(
            "Venue Details:\n\n" +
            "Name: %s\n\n" +
            "Address: %s\n\n" +
            "Capacity: %s\n\n" +
            "Manager: %s\n\n" +
            "Contact: %s",
            venueName, address, capacity, manager, contact
        );

        // Text area for details
        JTextArea textArea = new JTextArea(detailsText);
        textArea.setEditable(false);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textArea.setBackground(Color.WHITE);
        textArea.setForeground(Color.BLACK);
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(textArea);
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

    // Helper methods for showing dialogs with visible text
    private void showWarningDialog(String message) {
        showCustomDialog(this, "Warning", message, new Color(241, 196, 15));
    }

    private void showErrorDialog(Component parent, String message) {
        showCustomDialog(parent, "Error", message, new Color(231, 76, 60));
    }

    private void showSuccessDialog(Component parent, String message) {
        showCustomDialog(parent, "Success", message, new Color(46, 204, 113));
    }

    private void showCustomDialog(Component parent, String title, String message, Color borderColor) {
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