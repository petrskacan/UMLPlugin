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

/**
 * A role object to represent the behavior of a library class.
 * Library class is just a wrapper for static methods.
 * So it doesn't have accessible constructor or factory methods to create/get
 * instance of this class and doesn't have any instance members.
 * All members are static!
 * 
 * @author Oleksandr Matviichuk
 */
public class LibraryClassRole extends StdClassRole {
    public final static String LIBRARY_ROLE_NAME = "LibraryTarget";
    
    public String getRoleName()
    {
        return LIBRARY_ROLE_NAME;
    }

    public String getStereotypeLabel()
    {
        return "library";
    }

    /**
     * Return the intended background colour for this type of target.
     */
    public Paint getBackgroundPaint(int width, int height)
    {
            return super.getBackgroundPaint(width, height);
    }
}
