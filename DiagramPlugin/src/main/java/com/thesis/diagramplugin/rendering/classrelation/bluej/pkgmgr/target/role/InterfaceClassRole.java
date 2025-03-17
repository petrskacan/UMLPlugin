/*
 This file is part of the BlueJ program.
 Copyright (C) 1999-2009  Michael Kolling and John Rosenberg

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
//import java.util.*;
//import java.util.List;
//
//import javax.swing.*;
//import bluej.Config;
//import bluej.debugmgr.Modifier2String;
//import bluej.pkgmgr.*;
//import bluej.pkgmgr.target.ClassTarget;
//import bluej.prefmgr.PrefMgr;
////~+omatviichuk
//import bluej.views.*;
//import java.awt.event.ActionEvent;

//~+omatviichuk

/**
 * A role object to represent the behaviour of interfaces.
 *
 * @author  Andrew Patterson
 * @version $Id: InterfaceClassRole.java 7594 2010-05-18 14:39:08Z nccb $
 */
public class InterfaceClassRole extends ClsMemberRecorderRole
{
    public final static String INTERFACE_ROLE_NAME = "InterfaceTarget";
    private static final Color bg = new Color(102, 255, 102);

    /**
     * Create the interface class role.
     */
    public InterfaceClassRole()
    {
    }

    public String getRoleName()
    {
        return INTERFACE_ROLE_NAME;
    }

    public String getStereotypeLabel()
    {
        return "interface";
    }
    
    /**
     * Return the intended background colour for this type of target.
     */
    public Paint getBackgroundPaint(int width, int height)
    {
        PluginSettingsService settingsService = ApplicationManager.getApplication().getService(PluginSettingsService.class);
        return (settingsService != null) ? settingsService.getColor(RoleColor.INTERFACE_CLASS_ROLE) : RoleColor.INTERFACE_CLASS_ROLE.getColor();

    }
}
