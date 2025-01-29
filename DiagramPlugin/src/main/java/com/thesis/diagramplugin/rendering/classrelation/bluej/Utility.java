package com.thesis.diagramplugin.rendering.classrelation.bluej;

/*
 This file is part of the BlueJ program. 
 Copyright (C) 1999-2009,2011,2012,2013,2014  Michael Kolling and John Rosenberg 
 
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


import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;


/**
 * Some generally useful utility methods available to all of bluej.
 * 
 * @author Michael Cahill
 * @author Michael Kolling
 */
public class Utility
{
    /**
     * Draw a thick rectangle - another of the things missing from the AWT
     */
    public static void drawThickRect(Graphics g, int x, int y, int width, int height, int thickness)
    {
        for (int i = 0; i < thickness; i++)
            g.drawRect(x + i, y + i, width - 2 * i -1, height - 2 * i -1);
    }

    /**
     * Draw a string at a given location on screen centered in a given
     * rectangle.<br>
     * Left justifies the string if it is too long to fit all of the string
     * inside the rectangle.
     */
    public static void drawCentredText(Graphics g, String str, int x, int y, int width, int height)
    {
        FontMetrics fm = g.getFontMetrics();

        Shape oldClip = g.getClip();
        g.clipRect(x, y, width, height);
        int xOffset = (width - fm.stringWidth(str)) / 2;
        if (xOffset < 0) {
            xOffset = 0;
        }
        // This is the space left around the text, divided by 2 (equal gap above and below)
        // to get the top of the text, plus the ascent to get the baseline 
        int yOffset = fm.getAscent() + ((height - fm.getAscent() - fm.getDescent()) / 2);
        g.drawString(str, x + xOffset, y + yOffset);
        g.setClip(oldClip);
    }
    
    /**
     * Draw a string at a given location on screen centered in a given
     * rectangle.<br>
     * Left justifies the string if it is too long to fit all of the string
     * inside the rectangle.
     */
    public static void drawLeftText(Graphics g, String str, int x, int y, int width, int height)
    {
        FontMetrics fm = g.getFontMetrics();

        Shape oldClip = g.getClip();
        g.clipRect(x, y, width, height);
        int xOffset = 0;
        // This is the space left around the text, divided by 2 (equal gap above and below)
        // to get the top of the text, plus the ascent to get the baseline 
        int yOffset = fm.getAscent() + ((height - fm.getAscent() - fm.getDescent()) / 2);
        g.drawString(str, x + xOffset, y + yOffset);
        g.setClip(oldClip);
    }

    /**
     * Draw a string at a given location on screen right-aligned in a given
     * rectangle.
     */
    public static void drawRightText(Graphics g, String str, int x, int y, int width, int height)
    {
        FontMetrics fm = g.getFontMetrics();

        Shape oldClip = g.getClip();
        g.clipRect(x, y, width, height);
        g.drawString(str, x + width - fm.stringWidth(str), y + (height + fm.getAscent()) / 2);
        g.setClip(oldClip);
    }

    /**
     * Splits "string" by "Delimiter"
     * 
     * @param str - the string to be split
     * @param delimiter - the field delimiter within str
     * @returns an array of Strings
     */
    public static String[] split(String str, String delimiter)
    {
        List<String> strings = new ArrayList<String>();
        int start = 0;
        int len = str.length();
        int dlen = delimiter.length();
        int offset = str.lastIndexOf(delimiter); // First of all, find the
        // Last occurance of the Delimiter
        // Stop empty delimiters
        if (dlen < 1)
            return null;
        else if (offset < 0) // one element
        {
            String[] result = {str};
            return result;
        }

        //
        // Append the delimiter onto the end if it doesn't already exit
        //
        if (len > offset + dlen) {
            str += delimiter;
            len += dlen;
        }

        do {
            // Get the new Offset
            offset = str.indexOf(delimiter, start);
            strings.add(str.substring(start, offset));

            // Get the new Start position
            start = offset + dlen;
        } while ((start < len) && (offset != -1));

        // Convert the list into an Array of Strings
        String result[] = new String[strings.size()];
        strings.toArray(result);
        return result;
    }

}
