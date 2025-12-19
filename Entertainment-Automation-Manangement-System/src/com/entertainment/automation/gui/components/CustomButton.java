package com.entertainment.automation.gui.components;

import javax.swing.*;
import java.awt.*;

public class CustomButton extends JButton {
    private Color backgroundColor;
    private Color hoverColor;
    private Color pressedColor;

    public CustomButton(String text, Color bgColor) {
        super(text);
        this.backgroundColor = bgColor;
        this.hoverColor = bgColor.brighter();
        this.pressedColor = bgColor.darker();
        setUI(new CustomButtonUI());
    }

    private class CustomButtonUI extends javax.swing.plaf.basic.BasicButtonUI {
      
        public void installUI(JComponent c) {
            super.installUI(c);
            AbstractButton button = (AbstractButton) c;
            button.setOpaque(false);
            button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            button.setForeground(Color.WHITE);
            button.setBackground(backgroundColor);
            button.setFocusPainted(false);
        }

      
        public void paint(Graphics g, JComponent c) {
            AbstractButton b = (AbstractButton) c;
            paintBackground(g, b, b.getModel().isPressed() ? pressedColor : 
                b.getModel().isRollover() ? hoverColor : backgroundColor);
            super.paint(g, c);
        }

        private void paintBackground(Graphics g, JComponent c, Color color) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 10, 10);
            g2.dispose();
        }
    }
}

    