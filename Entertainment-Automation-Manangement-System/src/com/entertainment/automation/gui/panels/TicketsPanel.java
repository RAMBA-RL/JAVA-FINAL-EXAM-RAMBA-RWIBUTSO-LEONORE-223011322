package com.entertainment.automation.gui.panels;

import com.entertainment.automation.dao.TicketDAO;
import com.entertainment.automation.models.Ticket;
import com.entertainment.automation.models.User;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class TicketsPanel extends JPanel {
    private User currentUser;
    private JTable ticketsTable;
    private DefaultTableModel tableModel;
    private TicketDAO ticketDAO;

    public TicketsPanel(User user) {
        this.currentUser = user;
        this.ticketDAO = new TicketDAO();
        initializeUI();
        loadTickets();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 241));
        JLabel titleLabel = new JLabel("Tickets Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        String[] columnNames = {"Ticket Number", "Event", "User", "Seat", "Type", "Price", "Status", "Purchase Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ticketsTable = new JTable(tableModel);
        ticketsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ticketsTable.setRowHeight(25);
        ticketsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(ticketsTable);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(new Color(236, 240, 241));

        JButton refreshButton = new JButton("Refresh");
        JButton viewButton = new JButton("View Details");

        styleButton(refreshButton, new Color(52, 152, 219));
        styleButton(viewButton, new Color(155, 89, 182));

        buttonPanel.add(refreshButton);
        buttonPanel.add(viewButton);

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTickets();
            }
        });

        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTicketDetails();
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

    private void loadTickets() {
        tableModel.setRowCount(0);
        List<Ticket> tickets;

        if ("Customer".equals(currentUser.getRole())) {
            tickets = ticketDAO.getTicketsByUser(currentUser.getUserID());
        } else {
            tickets = ticketDAO.getAllTickets();
        }
        for (Ticket ticket : tickets) {
            tableModel.addRow(new Object[]{
                ticket.getTicketNumber(),
                ticket.getEventTitle(),     // Always display event title, not ID
                ticket.getUserName(),       // Always display user name, not ID
                ticket.getSeatNumber(),
                ticket.getTicketType(),
                String.format("RWF %.2f", ticket.getPrice()),
                ticket.getStatus(),
                ticket.getPurchaseDate() != null ? ticket.getPurchaseDate().toString() : ""
            });
        }
    }

    private void showTicketDetails() {
        int selectedRow = ticketsTable.getSelectedRow();
        if (selectedRow < 0) {
            showWarningDialog("Please select a ticket first");
            return;
        }

        String ticketNumber = (String) tableModel.getValueAt(selectedRow, 0);
        String eventName = (String) tableModel.getValueAt(selectedRow, 1);
        String userName = (String) tableModel.getValueAt(selectedRow, 2);
        String seat = (String) tableModel.getValueAt(selectedRow, 3);
        String type = (String) tableModel.getValueAt(selectedRow, 4);
        String price = (String) tableModel.getValueAt(selectedRow, 5);
        String status = (String) tableModel.getValueAt(selectedRow, 6);
        String date = (String) tableModel.getValueAt(selectedRow, 7);

        // Create the dialog matching Image 1 style
        final JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Ticket Details - " + ticketNumber, true);
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
        String detailsText = String.format(
            "Ticket Details:\n\n" +
            "Ticket Number: %s\n\n" +
            "Event: %s\n\n" +
            "User: %s\n\n" +
            "Seat: %s\n\n" +
            "Type: %s\n\n" +
            "Price: %s\n\n" +
            "Status: %s\n\n" +
            "Purchase Date: %s",
            ticketNumber, eventName, userName, seat, type, price, status, date
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