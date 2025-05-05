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
        panel = new JPanel(new BorderLayout());

        // 🔹 Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel("<html><h2>Diagram Plugin Settings</h2></html>");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel descriptionLabel = new JLabel("<html><p>Here you can customize the background colors of different UML class roles. "
                + "Changes will take effect immediately after saving.</p></html>");
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(titleLabel);
        headerPanel.add(descriptionLabel);

        // 🔹 Color buttons with FlowLayout wrappers
        JPanel colorsPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        colorButtons = new HashMap<>();

        for (RoleColor role : RoleColor.values()) {
            // Label
            colorsPanel.add(new JLabel(role.name()));

            // Button
            JButton colorButton = new JButton("Choose Color");
            Color initial = settingsService.getColor(role);
            colorButton.setForeground(initial);
            colorButton.setBackground(initial);
            colorButton.addActionListener(e -> {
                Color newColor = JColorChooser.showDialog(
                        panel,
                        "Choose Color for " + role.name(),
                        colorButton.getForeground()
                );
                if (newColor != null) {
                    colorButton.setForeground(newColor);
                    colorButton.setBackground(newColor);
                    settingsService.setColor(role, newColor);
                }
            });
            colorButtons.put(role, colorButton);

            // Wrapper so button stays at preferred size
            JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            wrapper.add(colorButton);
            colorsPanel.add(wrapper);
        }

        // 🔹 Reset button
        JPanel buttonPanel = new JPanel();
        JButton resetButton = new JButton("Reset to Default");
        resetButton.addActionListener(e -> resetToDefault());
        buttonPanel.add(resetButton);

        // 🔹 Assemble
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
            Color chosen = colorButtons.get(role).getForeground();
            settingsService.setColor(role, chosen);
            role.setColor(chosen);
        }
        // Apply to UI
        ApplicationManager.getApplication().invokeLater(() -> {
            Window window = SwingUtilities.getWindowAncestor(panel);
            if (window != null) window.repaint();
        });
        Messages.showInfoMessage("Settings saved successfully!", "Diagram Plugin Settings");
    }

    public void loadSettings() {
        for (RoleColor role : RoleColor.values()) {
            Color c = settingsService.getColor(role);
            JButton btn = colorButtons.get(role);
            btn.setForeground(c);
            btn.setBackground(c);
        }
    }

    public void resetToDefault() {
        for (RoleColor role : RoleColor.values()) {
            Color defaultColor = settingsService.getDefaultColor(role);
            settingsService.setColor(role, defaultColor);
            JButton btn = colorButtons.get(role);
            btn.setForeground(defaultColor);
            btn.setBackground(defaultColor);
        }
        JOptionPane.showMessageDialog(
                null,
                "Colors reset to their original defaults!",
                "Diagram Plugin Settings",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
