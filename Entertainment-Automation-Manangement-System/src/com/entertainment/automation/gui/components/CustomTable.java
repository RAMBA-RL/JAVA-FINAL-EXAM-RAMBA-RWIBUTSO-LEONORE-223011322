package com.entertainment.automation.gui.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CustomTable extends JTable {
    
    public CustomTable() {
        setShowGrid(true);
        setGridColor(new Color(220, 220, 220));
        setRowHeight(25);
        setFont(new Font("Segoe UI", Font.PLAIN, 12));
        getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        getTableHeader().setBackground(new Color(41, 128, 185));
        getTableHeader().setForeground(Color.WHITE);
     
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        getTableHeader().setDefaultRenderer(centerRenderer);
    }
}