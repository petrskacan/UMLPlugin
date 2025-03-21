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
package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.graphPainter;

import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.Dependency;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.UsesDependency;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

/**
 * Paints usesDependencies
 * 
 * @author fisker
 * @author Michael Kolling
 */
public class UsesDependencyPainter
    implements DependencyPainter
{
    protected static final float strokeWidthDefault = 1.0f;
    protected static final float strokeWidthSelected = 2.0f;
    static final int ARROW_SIZE = 10; // pixels
    static final double ARROW_ANGLE = Math.PI / 6; // radians

    private static final Color normalColour = Color.BLACK;

    private static final float dash1[] = {5.0f, 2.0f};
    private static final BasicStroke dashedUnselected = new BasicStroke(strokeWidthDefault, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
    private static final BasicStroke dashedSelected = new BasicStroke(strokeWidthSelected, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
    private static final BasicStroke normalSelected = new BasicStroke(strokeWidthSelected);
    private static final BasicStroke normalUnselected = new BasicStroke(strokeWidthDefault);

    public UsesDependencyPainter()
    {
    }

    public void paint(Graphics2D g, Dependency dependency, boolean hasFocus)
    {
        if (!(dependency instanceof UsesDependency)) {
            throw new IllegalArgumentException("Not a UsesDependency");
        }
        Stroke oldStroke = g.getStroke();
        UsesDependency d = (UsesDependency) dependency;
        Stroke dashedStroke, normalStroke;
        boolean isSelected = d.isSelected() && hasFocus;
        if (isSelected) {
            dashedStroke = dashedSelected;
            normalStroke = normalSelected;
        }
        else {
            dashedStroke = dashedUnselected;
            normalStroke = normalUnselected;
        }
        g.setStroke(normalStroke);
        int src_x = d.getSourceX();
        int src_y = d.getSourceY();
        int dst_x = d.getDestX();
        int dst_y = d.getDestY();
        ;

        g.setColor(normalColour);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Draw the end arrow
        int delta_x = d.isEndLeft() ? -10 : 10;

        g.drawLine(dst_x, dst_y, dst_x + delta_x, dst_y + 4);
        g.drawLine(dst_x, dst_y, dst_x + delta_x, dst_y - 4);
        g.setStroke(dashedStroke);

        paintLine(src_y, d, g, src_x, dst_x, dst_y, oldStroke);
    }

    protected void paintLine(int src_y, UsesDependency d, Graphics2D g, int src_x, int dst_x, int dst_y, Stroke oldStroke) {
        // Draw the start
        int corner_y = src_y + (d.isStartTop() ? -15 : 15);
        g.drawLine(src_x, corner_y, src_x, src_y);
        src_y = corner_y;

        // Draw the last line segment
        int corner_x = dst_x + (d.isEndLeft() ? -15 : 15);
        g.drawLine(corner_x, dst_y, dst_x, dst_y);
        dst_x = corner_x;

        // if arrow vertical corner, draw first segment up to corner
        if ((src_y != dst_y) && (d.isStartTop() == (src_y < dst_y))) {
            corner_x = ((src_x + dst_x) / 2) + (d.isEndLeft() ? 15 : -15);
            corner_x = (d.isEndLeft() ? Math.min(dst_x, corner_x) : Math.max(dst_x, corner_x));
            g.drawLine(src_x, src_y, corner_x, src_y);
            src_x = corner_x;
        }

        // if arrow horiz. corner, draw first segment up to corner
        if ((src_x != dst_x) && (d.isEndLeft() == (src_x > dst_x))) {
            corner_y = ((src_y + dst_y) / 2) + (d.isStartTop() ? 15 : -15);
            corner_y = (d.isStartTop() ? Math.min(src_y, corner_y) : Math.max(src_y, corner_y));
            g.drawLine(dst_x, corner_y, dst_x, dst_y);
            dst_y = corner_y;
        }

        // draw the middle bit
        g.drawLine(src_x, src_y, src_x, dst_y);
        g.drawLine(src_x, dst_y, dst_x, dst_y);

        g.setStroke(oldStroke);
    }
}