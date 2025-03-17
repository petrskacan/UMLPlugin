package com.thesis.diagramplugin.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PluginSettingsConfigurable implements Configurable {
    private PluginSettingsPanel settingsPanel;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "UML Plugin";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        settingsPanel = new PluginSettingsPanel();
        return settingsPanel.getPanel();
    }

    @Override
    public boolean isModified() {
        return settingsPanel.isModified();
    }

    @Override
    public void apply() {
        settingsPanel.saveSettings();
    }

    @Override
    public void reset() {
        settingsPanel.loadSettings();
    }

    @Override
    public void disposeUIResources() {
        settingsPanel = null;
    }
}
