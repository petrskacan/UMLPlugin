package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.role;

import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.ClassTarget;

import java.awt.*;

public class PackageRole extends ClassRole {

    public final static String CLASS_ROLE_NAME = "PackageTarget";


    @Override
    public String getStereotypeLabel() {
        return "package";
    }

    @Override
    public Paint getBackgroundPaint(int width, int height) {
        return new Color(255, 255, 204, 128);
    }

    @Override
    public String getRoleName() {
        return CLASS_ROLE_NAME;
    }
}
