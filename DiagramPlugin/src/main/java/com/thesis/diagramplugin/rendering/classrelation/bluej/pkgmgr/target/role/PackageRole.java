package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.role;

import com.intellij.openapi.application.ApplicationManager;
import com.thesis.diagramplugin.settings.PluginSettingsService;

import java.awt.*;

public class PackageRole extends ClassRole {

    public final static String CLASS_ROLE_NAME = "PackageTarget";


    @Override
    public String getStereotypeLabel() {
        return "package";
    }

    @Override
    public Paint getBackgroundPaint(int width, int height) {
        PluginSettingsService settingsService = ApplicationManager.getApplication().getService(PluginSettingsService.class);
        return (settingsService != null) ? settingsService.getColor(RoleColor.PACKAGE_ROLE) : RoleColor.PACKAGE_ROLE.getColor();

    }

    @Override
    public String getRoleName() {
        return CLASS_ROLE_NAME;
    }
}
