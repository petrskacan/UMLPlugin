package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.role;

import java.awt.*;

public enum RoleColor {
    CLASS_ROLE(new Color(255, 204, 102)),
    ABSTRACT_CLASS_ROLE(new Color(255, 204, 204)),
    ENUM_CLASS_ROLE(new Color(255, 255, 0)),
    INTERFACE_CLASS_ROLE(new Color(102, 255, 102)),
    MODULE_ROLE(new Color(153, 254, 255)),
    RECORD_CLASS_ROLE(new Color(255, 153, 255)),
    UNIT_TEST_CLASS_ROLE(new Color(204, 204, 153)),
    PACKAGE_ROLE(new Color(255, 255, 204, 128)),
    SINGLETON_CLASS_ROLE(new Color(255, 204, 102)),
    CRATE_CLASS_ROLE(new Color(204, 153, 255)),
    LIBRARY_CLASS_ROLE(new Color(200, 200, 200));

    private Color color;

    RoleColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
