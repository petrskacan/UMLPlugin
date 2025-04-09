package com.thesis.diagramplugin.rendering.classrelation.bluej.graph;

import com.intellij.openapi.ui.ComboBox;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.Package;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.CustomDependency;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.DependencyType;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.DependentTarget;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.Target;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class CustomDependencyDialog{
    private final JComboBox<String> packageFromCombo;
    private final JComboBox<String> packageToCombo;
    private final JComboBox<DependencyType> typeCombo;
    private static final Map<String, String> TARGET2TARGET = new HashMap<>();
    private final Package pkg;

    public CustomDependencyDialog(Package pkg) {
        this.pkg = pkg;
        typeCombo = new ComboBox<>(DependencyType.values());

        Set<String> packageNamesSet = new LinkedHashSet<>();
        for(Target target :  pkg.getTargets().getAllTargets())
        {
            if(!target.getDisplayName().equals(pkg.getBaseName())) {
                TARGET2TARGET.put(target.getDisplayName(), target.getIdentifierName());
                packageNamesSet.add(target.getDisplayName());
            }
        }
        String[] packageNames = packageNamesSet.toArray(new String[0]);
        packageFromCombo = new ComboBox<>(packageNames);
        packageToCombo = new ComboBox<>(packageNames);

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
            createCustomDependency();
            frame.dispose();
        });

        cancelButton.addActionListener(e -> frame.dispose());
    }

    private void createCustomDependency() {
        DependentTarget from = pkg.getDependentTarget(TARGET2TARGET.get(Objects.requireNonNull(packageFromCombo.getSelectedItem()).toString()));
        DependentTarget to = pkg.getDependentTarget(TARGET2TARGET.get(Objects.requireNonNull(packageToCombo.getSelectedItem()).toString()));
        new CustomDependency(pkg, from, to, (DependencyType) Objects.requireNonNull(typeCombo.getSelectedItem()));
    }

}

