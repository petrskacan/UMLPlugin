package com.thesis.diagramplugin.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.role.RoleColor;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@State(name = "PluginSettings", storages = {@Storage("PluginSettings.xml")})
public class PluginSettingsService implements PersistentStateComponent<PluginSettingsService.State> {
    private final Map<String, String> colorMap = new HashMap<>();
    private static final Map<RoleColor, Color> defaultColorMap = new HashMap<>(); // Stores original defaults

    public static class State {
        public Map<String, String> colorValues = new HashMap<>();
        public State() { }
    }

    public PluginSettingsService() {
        // 🔹 Initialize the default colors ONCE
        if (defaultColorMap.isEmpty()) {
            for (RoleColor role : RoleColor.values()) {
                defaultColorMap.put(role, role.getColor());
            }
        }
    }

    @Override
    public State getState() {
        State state = new State();
        state.colorValues.putAll(colorMap);
        return state;
    }

    @Override
    public void loadState(State state) {
        colorMap.clear();
        colorMap.putAll(state.colorValues);
    }

    public Color getColor(RoleColor role) {
        String colorHex = colorMap.get(role.name());
        if (colorHex != null) {
            try {
                return Color.decode(colorHex);
            } catch (NumberFormatException ignored) { }
        }
        return defaultColorMap.get(role);
    }

    public void setColor(RoleColor role, Color color) {
        colorMap.put(role.name(), String.format("#%06X", (0xFFFFFF & color.getRGB())));
    }

    public static PluginSettingsService getInstance() {
        return ApplicationManager.getApplication().getService(PluginSettingsService.class);
    }

    public Color getDefaultColor(RoleColor role) {
        return defaultColorMap.get(role);
    }
}
