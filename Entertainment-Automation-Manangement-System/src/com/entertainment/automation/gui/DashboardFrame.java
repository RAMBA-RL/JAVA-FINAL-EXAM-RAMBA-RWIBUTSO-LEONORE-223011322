package com.entertainment.automation.gui;

import com.entertainment.automation.models.User;
import com.entertainment.automation.gui.panels.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class DashboardFrame extends JFrame {
    private User currentUser;
    private JTabbedPane tabbedPane;

    private final Color PRIMARY_COLOR = new Color(41, 128, 185); 
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241); 
    private final Color TEXT_COLOR = new Color(44, 62, 80); 
    private final Color TAB_BACKGROUND = new Color(250, 250, 250); 
    private final Color TAB_TEXT_COLOR = new Color(44, 62, 80); 

    public DashboardFrame(User user) {
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Entertainment Automation Management System - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        JPanel headerPanel = createHeaderPanel();

        tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(JTabbedPane.LEFT);
        tabbedPane.setPreferredSize(new Dimension(350, 0));
        
        customizeSidebarColors();
        addTabsBasedOnRole();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }
    
    private void customizeSidebarColors() {
        UIManager.put("TabbedPane.background", TAB_BACKGROUND);
        UIManager.put("TabbedPane.foreground", TAB_TEXT_COLOR);
        UIManager.put("TabbedPane.selected", PRIMARY_COLOR); 
        UIManager.put("TabbedPane.selectedForeground", Color.WHITE); 

        UIManager.put("TabbedPane.tabInsets", new Insets(15, 20, 15, 20));
        UIManager.put("TabbedPane.tabAreaInsets", new Insets(10, 5, 10, 5));
        
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tabbedPane.setBackground(TAB_BACKGROUND);
        tabbedPane.setForeground(TAB_TEXT_COLOR);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getFullName());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);

        JLabel userInfoLabel = new JLabel("Role: " + currentUser.getRole() + " | " +
                "Last Login: " + (currentUser.getLastLogin() != null ?
                currentUser.getLastLogin().toString() : "First login"));
        userInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userInfoLabel.setForeground(Color.WHITE);

        // Settings button in top right corner
        final JButton settingsBtn = new JButton("Settings");
        settingsBtn.setBackground(new Color(52, 152, 219));
        settingsBtn.setForeground(Color.WHITE);
        settingsBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        settingsBtn.setFocusPainted(false);
        settingsBtn.setBorderPainted(false);
        settingsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        settingsBtn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        settingsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSettingsMenu(settingsBtn);
            }
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setBackground(PRIMARY_COLOR);
        rightPanel.add(userInfoLabel);
        rightPanel.add(settingsBtn);

        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private void showSettingsMenu(JButton sourceButton) {
        JPopupMenu settingsMenu = new JPopupMenu();
        settingsMenu.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        
        // Help option
        JMenuItem helpItem = new JMenuItem("Help");
        helpItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        helpItem.setBorder(BorderFactory.createEmptyBorder(12, 23, 12, 23));
        helpItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHelpDialog();
            }
        });
        
        // Logout option
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        logoutItem.setBorder(BorderFactory.createEmptyBorder(12, 23, 12, 23));
        logoutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });
        
        settingsMenu.add(helpItem);
        settingsMenu.addSeparator();
        settingsMenu.add(logoutItem);
        
        // Show menu below the settings button
        settingsMenu.show(sourceButton, 0, sourceButton.getHeight());
    }

    private void showHelpDialog() {
        String helpText;
        
        if ("Administrator".equals(currentUser.getRole()) || "EventManager".equals(currentUser.getRole())) {
            helpText = "===============================================\n" +
                       "   AutoEvent Manager - Help Guide\n" +
                       "===============================================\n\n" +
                       "ADMINISTRATOR FUNCTIONS:\n" +
                       "-----------------------------------------------\n" +
                       "• Manage Events - Create, edit, and delete events\n" +
                       "• Manage Venues - Add and configure venues\n" +
                       "• Manage Sponsors - Register and track sponsors\n" +
                       "• View Tickets - Monitor all ticket sales\n" +
                       "• View Reviews - See customer feedback\n\n" +
                       "TICKET MANAGEMENT:\n" +
                       "-----------------------------------------------\n" +
                       "• System automatically tracks seat availability\n" +
                       "• Each seat can only be sold once per event\n" +
                       "• Sold-out events are marked automatically\n\n" +
                       "TIPS:\n" +
                       "-----------------------------------------------\n" +
                       "• Use the Refresh button to see latest data\n" +
                       "• Select items before editing or deleting\n" +
                       "• Check event details before making changes\n\n" +
                       "===============================================\n" +
                       "For technical support:\n" +
                       "Email: support@autoevent.rw\n" +
                       "===============================================";
        } else {
            helpText = "===============================================\n" +
                       "   AutoEvent Manager - Help Guide\n" +
                       "===============================================\n\n" +
                       "CUSTOMER FUNCTIONS:\n" +
                       "-----------------------------------------------\n" +
                       "• Browse available events\n" +
                       "• Purchase tickets with seat selection\n" +
                       "• Write reviews for attended events\n\n" +
                       "HOW TO PURCHASE TICKETS:\n" +
                       "-----------------------------------------------\n" +
                       "1. Select an event from the list\n" +
                       "2. Click 'Buy Ticket' button\n" +
                       "3. Choose your ticket type (Regular/VIP)\n" +
                       "4. Enter desired seat number (e.g., S1, S2)\n" +
                       "5. Click 'Check' to verify seat availability\n" +
                       "6. Click 'Show Available Seats' to see all options\n" +
                       "7. Confirm your purchase\n\n" +
                       "SEAT SELECTION:\n" +
                       "-----------------------------------------------\n" +
                       "• Seats are numbered as S1, S2, S3, etc.\n" +
                       "• Each seat can only be sold once\n" +
                       "• Use 'Check' button to verify availability\n" +
                       "• System prevents duplicate bookings\n\n" +
                       "WRITING REVIEWS:\n" +
                       "-----------------------------------------------\n" +
                       "• Rate events from 1 to 5 stars\n" +
                       "• Share your experience with comments\n" +
                       "• Help other customers make decisions\n\n" +
                       "===============================================\n" +
                       "For assistance:\n" +
                       "Email: support@autoevent.rw\n" +
                       "===============================================";
        }
        
        JTextArea helpArea = new JTextArea(helpText);
        helpArea.setEditable(false);
        helpArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        helpArea.setBackground(new Color(250, 250, 250));
        helpArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JScrollPane scrollPane = new JScrollPane(helpArea);
        scrollPane.setPreferredSize(new Dimension(500, 450));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        
        JOptionPane.showMessageDialog(this, 
            scrollPane, 
            "Help - User Guide", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private void addTabsBasedOnRole() {
        addStyledTab("Events", new EventsPanel(currentUser));

        if (!"Customer".equals(currentUser.getRole())) {
            addStyledTab("Tickets", new TicketsPanel(currentUser));
            addStyledTab("Venues", new VenuesPanel(currentUser));
            addStyledTab("Sponsors", new SponsorsPanel(currentUser));
        }

        if ("Administrator".equals(currentUser.getRole()) || "EventManager".equals(currentUser.getRole())) {
            addStyledTab("Reviews", new ReviewsPanel(currentUser));
        }
    }
    
    private void addStyledTab(String title, JPanel panel) {
        tabbedPane.addTab(title, panel);
        
        int tabIndex = tabbedPane.getTabCount() - 1;
        
        JLabel tabLabel = new JLabel(title);
        tabLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabLabel.setForeground(TAB_TEXT_COLOR); 
        tabLabel.setOpaque(false);
        tabLabel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        tabLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        tabbedPane.setTabComponentAt(tabIndex, tabLabel);
    }
}