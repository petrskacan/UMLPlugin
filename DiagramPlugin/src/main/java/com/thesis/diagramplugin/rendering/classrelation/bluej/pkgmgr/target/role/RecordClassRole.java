/*
 This file is part of the BlueJ program.
 Copyright (C) 1999-2009,2010  Michael Kolling and John Rosenberg

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

import java.awt.*;

//import bluej.Config;
//import bluej.pkgmgr.PackageEditor;
//import bluej.pkgmgr.target.ClassTarget;
//import bluej.pkgmgr.target.ClassTarget.CreateTestAction;
//import bluej.pkgmgr.target.role.ClsMemberRecorderRole.*;
//import bluej.prefmgr.PrefMgr;
////~~omatviichuk
//import bluej.views.View;

/**
 * A role object to represent the behaviour of enums.
 *
 * @author Poul Henriksen <polle@mip.sdu.dk>
 */
public class RecordClassRole extends ClsMemberRecorderRole
{
    public final static String RECORD_ROLE_NAME = "RecordTarget";
    private static final Color bg = new Color(255, 153, 255);

    /**
     * Create the enum class role.
     */
    public RecordClassRole()
    {
    }

    public String getRoleName()
    {
        return RECORD_ROLE_NAME;
    }

    public String getStereotypeLabel()
    {
        return "record";
    }

    /**
     * Return the intended background colour for this type of target.
     */
    public Paint getBackgroundPaint(int width, int height)
    {
        return bg;
    }


    


}
