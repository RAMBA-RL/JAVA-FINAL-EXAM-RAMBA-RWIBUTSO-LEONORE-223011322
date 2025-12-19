package com.entertainment.automation.gui.panels;

import com.entertainment.automation.dao.EventDAO;
import com.entertainment.automation.dao.ReviewDAO;
import com.entertainment.automation.models.Event;
import com.entertainment.automation.models.Review;
import com.entertainment.automation.models.User;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ReviewsPanel extends JPanel {
    private User currentUser;
    private JTable reviewsTable;
    private DefaultTableModel tableModel;
    private ReviewDAO reviewDAO;

    public ReviewsPanel(User user) {
        this.currentUser = user;
        this.reviewDAO = new ReviewDAO();
        initializeUI();
        loadReviews();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 241));

        JLabel titleLabel = new JLabel("Reviews Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        String[] columnNames = {"Event", "User", "Rating", "Comment", "Review Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reviewsTable = new JTable(tableModel);
        reviewsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        reviewsTable.setRowHeight(25);
        reviewsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(reviewsTable);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(new Color(236, 240, 241));

        JButton refreshButton = new JButton("Refresh");
        JButton viewButton = new JButton("View Details");

        styleButton(refreshButton, new Color(52, 152, 219));
        styleButton(viewButton, new Color(155, 89, 182));

        buttonPanel.add(refreshButton);

        //  Add Review button only if Customer role
        if ("Customer".equalsIgnoreCase(currentUser.getRole())) {
            JButton addButton = new JButton("Add Review");
            styleButton(addButton, new Color(46, 204, 113));
            buttonPanel.add(addButton);

            addButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showAddReviewDialog();
                }
            });
        }

        buttonPanel.add(viewButton);

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadReviews();
            }
        });

        viewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showReviewDetails();
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

    private void loadReviews() {
        tableModel.setRowCount(0);
        List<Review> reviews = reviewDAO.getAllReviews();

        for (Review review : reviews) {
            tableModel.addRow(new Object[]{
                review.getEventTitle(),
                review.getUserName(),
                getStarRating(review.getRating()),
                review.getComment().length() > 50 ?
                        review.getComment().substring(0, 50) + "..." : review.getComment(),
                review.getReviewDate().toString()
            });
        }
    }

    private String getStarRating(int rating) {
        String stars = "";
        for (int i = 0; i < rating; i++) stars += "*";
        for (int i = rating; i < 5; i++) stars += "-";
        return stars + " (" + rating + "/5)";
    }

    private void showAddReviewDialog() {
        // Prevent Admin from accessing add review dialog
        if (!"Customer".equalsIgnoreCase(currentUser.getRole())) {
            showWarningDialog("Only customers can add reviews.");
            return;
        }

        // Load events from database
        EventDAO eventDAO = new EventDAO();
        final List<Event> eventList = eventDAO.getAllEvents();
        
        if (eventList == null || eventList.isEmpty()) {
            showErrorDialog(this, "No events available to review!");
            return;
        }

        final JDialog addDialog = new JDialog((Frame) null, "Add Review", true);
        addDialog.setSize(520, 440);
        addDialog.setLocationRelativeTo(this);
        addDialog.setLayout(new BorderLayout(0, 0));
        addDialog.setResizable(false);

        // ===== HEADER PANEL =====
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(520, 55));
        headerPanel.setLayout(new GridBagLayout());

        JLabel headerLabel = new JLabel("Add Review");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        // ===== CONTENT PANEL =====
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null); // Using absolute positioning for precise control
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setPreferredSize(new Dimension(520, 280));

        int labelX = 40;
        int fieldX = 40;
        int fieldWidth = 440;
        int currentY = 25;

        // Event Label
        JLabel eventLabel = new JLabel("Event:");
        eventLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        eventLabel.setForeground(new Color(52, 73, 94));
        eventLabel.setBounds(labelX, currentY, 100, 20);
        contentPanel.add(eventLabel);
        currentY += 25;

        // Event ComboBox
        String[] eventTitles = new String[eventList.size() + 1];
        eventTitles[0] = "Select Event";
        for (int i = 0; i < eventList.size(); i++) {
            eventTitles[i + 1] = eventList.get(i).getTitle();
        }
        
        final JComboBox eventCombo = new JComboBox(eventTitles);
        eventCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        eventCombo.setBounds(fieldX, currentY, fieldWidth, 35);
        eventCombo.setBackground(Color.WHITE);
        contentPanel.add(eventCombo);
        currentY += 50;

        // Rating Label
        JLabel ratingLabel = new JLabel("Rating:");
        ratingLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        ratingLabel.setForeground(new Color(52, 73, 94));
        ratingLabel.setBounds(labelX, currentY, 100, 20);
        contentPanel.add(ratingLabel);
        currentY += 25;

        // Rating ComboBox
        final JComboBox ratingCombo = new JComboBox(new Integer[]{1, 2, 3, 4, 5});
        ratingCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ratingCombo.setBounds(fieldX, currentY, fieldWidth, 35);
        ratingCombo.setBackground(Color.WHITE);
        contentPanel.add(ratingCombo);
        currentY += 50;

        // Comment Label
        JLabel commentLabel = new JLabel("Comment:");
        commentLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        commentLabel.setForeground(new Color(52, 73, 94));
        commentLabel.setBounds(labelX, currentY, 100, 20);
        contentPanel.add(commentLabel);
        currentY += 25;

        // Comment TextArea
        final JTextArea commentArea = new JTextArea();
        commentArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        
        JScrollPane commentScroll = new JScrollPane(commentArea);
        commentScroll.setBounds(fieldX, currentY, fieldWidth, 90);
        commentScroll.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        contentPanel.add(commentScroll);

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setPreferredSize(new Dimension(520, 60));

        JButton saveButton = new JButton("Save");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createEmptyBorder(10, 35, 10, 35));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cancelButton.setBackground(new Color(149, 165, 166));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // ===== ASSEMBLE DIALOG =====
        addDialog.add(headerPanel, BorderLayout.NORTH);
        addDialog.add(contentPanel, BorderLayout.CENTER);
        addDialog.add(buttonPanel, BorderLayout.SOUTH);

        // ===== ACTION LISTENERS =====
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String comment = commentArea.getText().trim();
                int selectedIndex = eventCombo.getSelectedIndex();
                
                if (selectedIndex == 0) {
                    showErrorDialog(addDialog, "Please select an event!");
                    return;
                }
                if (comment.equals("")) {
                    showErrorDialog(addDialog, "Please enter a comment!");
                    return;
                }

                // Get the actual event ID from the selected event
                Event selectedEvent = eventList.get(selectedIndex - 1);
                
                Review newReview = new Review();
                newReview.setEventID(selectedEvent.getEventID());
                newReview.setUserID(currentUser.getUserID());
                newReview.setRating(((Integer) ratingCombo.getSelectedItem()).intValue());
                newReview.setComment(comment);

                if (reviewDAO.addReview(newReview)) {
                    showSuccessDialog(addDialog, "Review added successfully!");
                    addDialog.dispose();
                    loadReviews();
                } else {
                    showErrorDialog(addDialog, "Error adding review!");
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addDialog.dispose();
            }
        });

        addDialog.setVisible(true);
    }

    private void showReviewDetails() {
        int selectedRow = reviewsTable.getSelectedRow();
        if (selectedRow < 0) {
            showWarningDialog("Please select a review first");
            return;
        }

        String eventName = (String) tableModel.getValueAt(selectedRow, 0);
        String userName = (String) tableModel.getValueAt(selectedRow, 1);
        String rating = (String) tableModel.getValueAt(selectedRow, 2);
        String comment = (String) tableModel.getValueAt(selectedRow, 3);
        String reviewDate = (String) tableModel.getValueAt(selectedRow, 4);

        // Create the dialog matching Image 1 style
        final JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Review Details - " + eventName, true);
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
        String detailsText = "Review Details:\n\n" +
                "Event: " + eventName + "\n\n" +
                "User: " + userName + "\n\n" +
                "Rating: " + rating + "\n\n" +
                "Comment: " + comment + "\n\n" +
                "Review Date: " + reviewDate;

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