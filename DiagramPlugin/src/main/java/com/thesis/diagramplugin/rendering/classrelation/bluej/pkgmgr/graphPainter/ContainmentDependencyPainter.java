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

import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.ContainmentDependency;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.Dependency;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.ConnectionSide;

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
public class ContainmentDependencyPainter extends UsesDependencyPainter
    implements DependencyPainter
{
    protected static final float strokeWidthDefault = 1.0f;
    protected static final float strokeWidthSelected = 2.0f;
    static final int ARROW_SIZE = 10; // pixels
    static final double ARROW_ANGLE = Math.PI / 6; // radians

    private static final Color normalColour = Color.BLACK;

    private static final BasicStroke normalSelected = new BasicStroke(strokeWidthSelected);
    private static final BasicStroke normalUnselected = new BasicStroke(strokeWidthDefault);

    public ContainmentDependencyPainter()
    {
    }

    public void paint(Graphics2D g, Dependency dependency, boolean hasFocus)
    {
        if (!(dependency instanceof ContainmentDependency d)) {
            throw new IllegalArgumentException("Not a ContainmentDependency");
        }
        Stroke oldStroke = g.getStroke();
        Stroke normalStroke;
        boolean isSelected = d.isSelected() && hasFocus;
        if (isSelected) {
            normalStroke = normalSelected;
        }
        else {
            normalStroke = normalUnselected;
        }
        g.setStroke(normalStroke);
        int src_x = d.getSourceX();
        int src_y = d.getSourceY();
        int dst_x = d.getDestX();
        int dst_y = d.getDestY();

        g.setColor(normalColour);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        paintOval(g, src_x, src_y, d.getEndConnectionSide());
        g.setStroke(normalStroke);

        paintLine(src_y, d, g, src_x, dst_x, dst_y, oldStroke);
    }

    private void paintOval(Graphics2D g,int x, int y, ConnectionSide side)
    {
        int circleDiameter = 10;
        int circleRadius = circleDiameter / 2;

        int offsetX = 0;
        int offsetY = 0;

        switch (side) {
            case TOP -> offsetY = -10;
            case BOTTOM -> offsetY = 10;
            case LEFT -> offsetX = -10;
            case RIGHT -> offsetX = 10;
        }

        int ovalX = x + offsetX - circleRadius;
        int ovalY = y + offsetY - circleRadius;

        g.drawOval(ovalX, ovalY, circleDiameter, circleDiameter);

        int centerX = ovalX + circleRadius;
        int centerY = ovalY + circleRadius;

        g.drawLine(centerX, centerY - circleRadius, centerX, centerY + circleRadius);
        g.drawLine(centerX - circleRadius, centerY, centerX + circleRadius, centerY);


    }
}