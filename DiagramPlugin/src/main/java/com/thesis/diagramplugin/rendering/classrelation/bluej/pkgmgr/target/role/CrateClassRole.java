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


import java.awt.Paint;

/**
 * A role object to represent the behaviour of a crate class. Crate class is
 * intended to hold some values. The values are represented as final public
 * attributes. The crate class also must have an accessible constructor.
 *
 * @author Oleksandr Matviichuk
 */
public class CrateClassRole extends StdClassRole {

    public final static String CRATE_ROLE_NAME = "CrateTarget";

    public String getRoleName() {
        return CRATE_ROLE_NAME;
    }

    public String getStereotypeLabel() {
        return "crate";
    }

    /**
     * Return the intended background colour for this type of target.
     */
    public Paint getBackgroundPaint(int width, int height) {
        return super.getBackgroundPaint(width, height);
    }
}
