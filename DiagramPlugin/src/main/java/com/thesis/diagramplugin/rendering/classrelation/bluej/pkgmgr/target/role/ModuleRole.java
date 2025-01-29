package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.role;

import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.ClassTarget;

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
        return new Color(153, 254, 255);
    }

    @Override
    public String getRoleName() {
        return CLASS_ROLE_NAME;
    }
}
