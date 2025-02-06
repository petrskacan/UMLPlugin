/*
 This file is part of the BlueJ program.
 Copyright (C) 1999-2009,2011,2014  Michael Kolling and John Rosenberg

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

import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.ClassTarget;

import java.awt.Color;
import java.awt.Paint;

/**
 * A class role in a class target, providing behaviour specific to particular
 * class types
 *
 * @author Bruce Quig
 */
public abstract class ClassRole {

    public final static String CLASS_ROLE_NAME = null;

    private ClassTarget ct;
    private static final Color bg = new Color(255, 204, 102);


    public String getRoleName() {
        return CLASS_ROLE_NAME;
    }

    /**
     * Return the default background colour for targets that don't want to
     * define their own colour.
     *
     * @param width Width of total area to paint
     * @param height Height of total area to paint
     */
    public Paint getBackgroundPaint(int width, int height) {
        return bg;
    }

    /**
     * Get the "stereotype label" for this class role. This will be displayed on
     * classes in the UML diagram along with the class name. It may return null
     * if there is no stereotype label.
     */
    public String getStereotypeLabel() {
        return null;
    }

    public void setClassTarget(ClassTarget ct) {
        this.ct = ct;
    }

    public ClassTarget getClassTarget() {
        return ct;
    }
}
