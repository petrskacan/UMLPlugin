package com.thesis.diagramplugin.rendering.classrelation.bluej.graph;

import com.intellij.openapi.ui.ComboBox;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.Package;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.CustomDependency;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.DependencyType;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.DependentTarget;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class CustomDependencyDialog{
    private final JComboBox<String> packageFromCombo;
    private final JComboBox<String> packageToCombo;
    private final JComboBox<DependencyType> typeCombo;
    private final Package pkg;
    private String path = "";
    private boolean confirmed = false;

    public CustomDependencyDialog(Package pkg) {
        this.pkg = pkg;

        // Initialize components
        packageFromCombo = new ComboBox<>();
        packageToCombo = new ComboBox<>();
        typeCombo = new ComboBox<>(DependencyType.values());
        String[] split = new String[0];
        for(String pack : pkg.getAllClassnames())
        {
            split = pack.split("/");
            if(!split[split.length -1].contains("."))
            {
                packageFromCombo.addItem(split[split.length-1]);
                packageToCombo.addItem(split[split.length-1]);
            }
        }

        this.path = String.join("/", Arrays.copyOfRange(split, 0, split.length - 2)) + "/";

        // Build the form layout
        JPanel formPanel = new JPanel(new GridLayout(3, 2));
        formPanel.add(new JLabel("From Package:"));
        formPanel.add(packageFromCombo);
        formPanel.add(new JLabel("To Package:"));
        formPanel.add(packageToCombo);
        formPanel.add(new JLabel("Dependency Type:"));
        formPanel.add(typeCombo);

        // Create buttons
        JButton okButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        // Set up the main panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Set up the frame
        JFrame frame = new JFrame("Custom Dependency Dialog");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Button actions
        okButton.addActionListener(e -> {
            confirmed = true;
            createCustomDependency();
            frame.dispose();
        });

        cancelButton.addActionListener(e -> frame.dispose());
    }

    private void createCustomDependency() {
        DependentTarget from = pkg.getDependentTarget(path + pkg.getBaseName() + "/" + packageFromCombo.getSelectedItem().toString());
        DependentTarget to = pkg.getDependentTarget(path + pkg.getBaseName() + "/" + packageToCombo.getSelectedItem().toString());
        new CustomDependency(pkg, from, to, (DependencyType) typeCombo.getSelectedItem());
    }

    public boolean isConfirmed() {
        return confirmed;
    }

}

