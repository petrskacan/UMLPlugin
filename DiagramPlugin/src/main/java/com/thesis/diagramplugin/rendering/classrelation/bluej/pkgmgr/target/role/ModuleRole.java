package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.role;

import com.intellij.openapi.application.ApplicationManager;
import com.thesis.diagramplugin.settings.PluginSettingsService;

import java.awt.*;

public class ModuleRole extends ClassRole {

    public final static String CLASS_ROLE_NAME = "ModuleTarget";
    private static final Color bg = new Color(153, 254, 255);


    @Override
    public String getStereotypeLabel() {
        return "module";
    }

    @Override
    public Paint getBackgroundPaint(int width, int height) {
        PluginSettingsService settingsService = ApplicationManager.getApplication().getService(PluginSettingsService.class);
        return (settingsService != null) ? settingsService.getColor(RoleColor.MODULE_ROLE) : RoleColor.MODULE_ROLE.getColor();

    }

    @Override
    public String getRoleName() {
        return CLASS_ROLE_NAME;
    }
}
