package com.entertainment.automation.gui.panels;

import com.entertainment.automation.dao.SponsorDAO;
import com.entertainment.automation.models.Sponsor;
import com.entertainment.automation.models.User;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class SponsorsPanel extends JPanel {
    private User currentUser;
    private JTable sponsorsTable;
    private DefaultTableModel tableModel;
    private SponsorDAO sponsorDAO;

    public SponsorsPanel(User user) {
        this.currentUser = user;
        this.sponsorDAO = new SponsorDAO();
        initializeUI();
        loadSponsors();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 241));

        // Title
        JLabel titleLabel = new JLabel("Sponsors Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Table
        String[] columnNames = {"Company Name", "Contact Person", "Email", "Phone", "Level", "Amount"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        sponsorsTable = new JTable(tableModel);
        sponsorsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sponsorsTable.setRowHeight(25);
        sponsorsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(sponsorsTable);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(new Color(236, 240, 241));

        JButton refreshButton = new JButton("Refresh");
        JButton addButton = new JButton("Add Sponsor");
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
         
            public void actionPerformed(ActionEvent e) {
                loadSponsors();
            }
        });
        
        addButton.addActionListener(new ActionListener() {
          
            public void actionPerformed(ActionEvent e) {
                showAddSponsorDialog();
            }
        });
        
        viewButton.addActionListener(new ActionListener() {
         
            public void actionPerformed(ActionEvent e) {
                showSponsorDetails();
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

    private void loadSponsors() {
        tableModel.setRowCount(0);
        List<Sponsor> sponsors = sponsorDAO.getAllSponsors();

        for (Sponsor sponsor : sponsors) {
            tableModel.addRow(new Object[]{
                sponsor.getCompanyName(),
                sponsor.getContactPerson(),
                sponsor.getEmail(),
                sponsor.getPhone(),
                sponsor.getSponsorshipLevel(),
                String.format("RWF %.2f", sponsor.getAmount())
            });
        }
    }

    private void showAddSponsorDialog() {
        final JDialog addDialog = new JDialog();
        addDialog.setTitle("Add New Sponsor");
        addDialog.setModal(true);
        addDialog.setSize(400, 350);
        addDialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        final JTextField companyField = new JTextField();
        final JTextField contactField = new JTextField();
        final JTextField emailField = new JTextField();
        final JTextField phoneField = new JTextField();
        final JComboBox<String> levelCombo = new JComboBox<>(new String[]{"Platinum", "Gold", "Silver", "Bronze"});
        final JTextField amountField = new JTextField();

        formPanel.add(new JLabel("Company Name:"));
        formPanel.add(companyField);
        formPanel.add(new JLabel("Contact Person:"));
        formPanel.add(contactField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Sponsorship Level:"));
        formPanel.add(levelCombo);
        formPanel.add(new JLabel("Amount:"));
        formPanel.add(amountField);

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
                Sponsor newSponsor = new Sponsor();
                newSponsor.setCompanyName(companyField.getText());
                newSponsor.setContactPerson(contactField.getText());
                newSponsor.setEmail(emailField.getText());
                newSponsor.setPhone(phoneField.getText());
                newSponsor.setSponsorshipLevel((String) levelCombo.getSelectedItem());
                newSponsor.setAmount(Double.parseDouble(amountField.getText()));
                
                if (sponsorDAO.addSponsor(newSponsor)) {
                    showSuccessDialog(addDialog, "Sponsor added successfully!");
                    addDialog.dispose();
                    loadSponsors();
                } else {
                    showErrorDialog(addDialog, "Error adding sponsor!");
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

    private void showSponsorDetails() {
        int selectedRow = sponsorsTable.getSelectedRow();
        if (selectedRow < 0) {
            showWarningDialog("Please select a sponsor first");
            return;
        }

        String companyName = (String) tableModel.getValueAt(selectedRow, 0);
        String contactPerson = (String) tableModel.getValueAt(selectedRow, 1);
        String email = (String) tableModel.getValueAt(selectedRow, 2);
        String phone = (String) tableModel.getValueAt(selectedRow, 3);
        String level = (String) tableModel.getValueAt(selectedRow, 4);
        String amount = (String) tableModel.getValueAt(selectedRow, 5);

        // Create the dialog matching Image 1 style
        final JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Sponsor Details - " + companyName, true);
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
            "Sponsor Details:\n\n" +
            "Company Name: %s\n\n" +
            "Contact Person: %s\n\n" +
            "Email: %s\n\n" +
            "Phone: %s\n\n" +
            "Sponsorship Level: %s\n\n" +
            "Amount: %s",
            companyName, contactPerson, email, phone, level, amount
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