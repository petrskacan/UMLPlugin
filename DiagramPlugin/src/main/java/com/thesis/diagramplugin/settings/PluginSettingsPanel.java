package com.thesis.diagramplugin.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.role.RoleColor;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PluginSettingsPanel {
    private JPanel panel;
    private Map<RoleColor, JButton> colorButtons;
    private PluginSettingsService settingsService;

    public PluginSettingsPanel() {
        settingsService = PluginSettingsService.getInstance();
        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // 🔹 Create a panel for the text and labels
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("<html><h2>Diagram Plugin Settings</h2></html>");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descriptionLabel = new JLabel("<html><p>Work In Progress: Here you can customize the background colors of different UML class roles. "
                + "Changes will take effect immediately after saving.</p></html>");
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(descriptionLabel);

        // 🔹 Create a panel for the color selection buttons
        JPanel colorsPanel = new JPanel(new GridLayout(0, 2));
        colorButtons = new HashMap<>();

        for (RoleColor role : RoleColor.values()) {
            colorsPanel.add(new JLabel(role.name()));
            JButton colorButton = new JButton("Choose Color");
            colorButton.setForeground(settingsService.getColor(role));
            colorButton.setBackground(settingsService.getColor(role));
            colorButton.addActionListener(e -> {
                Color newColor = JColorChooser.showDialog(null, "Choose Color", colorButton.getForeground());
                if (newColor != null) {
                    colorButton.setForeground(newColor);
                    colorButton.setBackground(newColor);
                    settingsService.setColor(role, newColor);
                }
            });

            colorButtons.put(role, colorButton);
            colorsPanel.add(colorButton);
        }
        JPanel buttonPanel = new JPanel();
        JButton resetButton = new JButton("Reset to Default");
        resetButton.addActionListener(e -> resetToDefault()); // Calls the reset method

        buttonPanel.add(resetButton);

        // 🔹 Add the header and colors panel to the main panel
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(colorsPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
    }

    public JPanel getPanel() {
        return panel;
    }

    public boolean isModified() {
        for (RoleColor role : RoleColor.values()) {
            if (!colorButtons.get(role).getForeground().equals(settingsService.getColor(role))) {
                return true;
            }
        }
        return false;
    }

    public void saveSettings() {
        for (RoleColor role : RoleColor.values()) {
            settingsService.setColor(role, colorButtons.get(role).getForeground());
            role.setColor(colorButtons.get(role).getForeground());
        }

        // 🔹 Apply changes to RoleColor dynamically
        for (RoleColor role : RoleColor.values()) {
            role.setColor(settingsService.getColor(role));
        }

        ApplicationManager.getApplication().invokeLater(() -> {
            Window window = SwingUtilities.getWindowAncestor(panel);
            if (window != null) {
                window.repaint();
            }
        });

        Messages.showInfoMessage("Settings saved successfully!", "Diagram Plugin Settings");
    }

    public void loadSettings() {
        for (RoleColor role : RoleColor.values()) {
            colorButtons.get(role).setBackground(settingsService.getColor(role));
            colorButtons.get(role).setForeground(settingsService.getColor(role));
        }
    }
    public void resetToDefault() {
        for (RoleColor role : RoleColor.values()) {
            Color defaultColor = settingsService.getDefaultColor(role); // 🔹 Get original default color
            settingsService.setColor(role, defaultColor);
            colorButtons.get(role).setBackground(defaultColor);
            colorButtons.get(role).setForeground(defaultColor);
        }
        JOptionPane.showMessageDialog(null, "Colors reset to their original defaults!", "Diagram Plugin Settings", JOptionPane.INFORMATION_MESSAGE);
    }
}
