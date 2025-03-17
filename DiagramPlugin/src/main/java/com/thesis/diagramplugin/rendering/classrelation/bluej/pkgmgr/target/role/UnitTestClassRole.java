/*
 This file is part of the BlueJ program.
 Copyright (C) 1999-2009,2010,2011,2012,2014  Michael Kolling and John Rosenberg

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

 This file is subject to the Classpath exception as provided in the
 LICENSE.txt file that accompanied this code.
 */
package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.role;

import com.intellij.openapi.application.ApplicationManager;
import com.thesis.diagramplugin.settings.PluginSettingsService;

import java.awt.*;

/**
 * A role object for Junit unit tests.
 *
 * @author Andrew Patterson
 */
public class UnitTestClassRole extends ClsMemberRecorderRole {

    public static final String UNITTEST_ROLE_NAME = "UnitTestTarget";
    public static final String UNITTEST_ROLE_NAME_JUNIT4 = "UnitTestTargetJunit4";
    
    private static final Color bg = new Color(204,204,153);

    /**
     * Whether this is a Junit 4 test class. If false, it's a Junit 3 test
     * class.
     */
    private boolean isJunit4;

    /**
     * Create the unit test class role.
     */
    public UnitTestClassRole(boolean isJunit4) {
        this.isJunit4 = isJunit4;
    }

    @Override
    public String getRoleName() {
        if (isJunit4) {
            return UNITTEST_ROLE_NAME_JUNIT4;
        } else {
            return UNITTEST_ROLE_NAME;
        }
    }

    @Override
    public String getStereotypeLabel() {
        return "unit test";
    }

    /**
     * Return the intended background colour for this type of target.
     */
    @Override
    public Paint getBackgroundPaint(int width, int height) {
        PluginSettingsService settingsService = ApplicationManager.getApplication().getService(PluginSettingsService.class);
        return (settingsService != null) ? settingsService.getColor(RoleColor.UNIT_TEST_CLASS_ROLE) : RoleColor.UNIT_TEST_CLASS_ROLE.getColor();

    }
}
