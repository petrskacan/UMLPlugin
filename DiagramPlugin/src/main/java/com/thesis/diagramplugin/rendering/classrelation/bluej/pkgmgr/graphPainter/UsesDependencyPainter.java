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
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.ConnectionSide;

import java.awt.*;

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
    protected boolean usesDiamond = false;
    protected Polygon diamond;

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
protected void paintLine(int recalcSrcY, UsesDependency d,
                         Graphics2D g, int recalcSrcX,  int recalcDstX, int recalcDstY, Stroke oldStroke) {
    // Determine start offset point based on side.
    int offsetX = 16;
    int offsetY = 16;
    int addX = 0, addY = 0;
    Point startPoint = new Point(recalcSrcX, recalcSrcY);
    if (d.isStartTop()) {
        startPoint.y = recalcSrcY - offsetY;
        addY-=5;
    } else if (d.isStartBottom()) {
        startPoint.y = recalcSrcY + offsetY;
        addY+=5;
    } else if (d.isStartLeft()) {
        startPoint.x = recalcSrcX - offsetX;
        addX-=5;
    } else if (d.isStartRight()) {
        startPoint.x = recalcSrcX + offsetX;
        addX+=5;
    }
    if (usesDiamond)
    {
        g.drawLine(startPoint.x, startPoint.y,startPoint.x+addX,startPoint.y+addY);
        startPoint.x+=addX;
        startPoint.y+=addY;
    }
    else
    {
        g.drawLine(recalcSrcX, recalcSrcY, startPoint.x, startPoint.y);
    }

    // Determine destination offset point based on side.
    Point destPoint = new Point(recalcDstX, recalcDstY);
    if (d.isEndTop()) {
        destPoint.y = recalcDstY - offsetY;
    } else if (d.isEndBottom()) {
        destPoint.y = recalcDstY + offsetY;
    } else if (d.isEndLeft()) {
        destPoint.x = recalcDstX - offsetX;
    } else if (d.isEndRight()) {
        destPoint.x = recalcDstX + offsetX;
    }
    // Draw the trailing segment from the destination port to the recalculated destination.
    g.drawLine(destPoint.x, destPoint.y, recalcDstX, recalcDstY);

    // Now, connect startPoint and destPoint with intermediate segments.
    // Decide the strategy based on the orientation of the start side.
    boolean startIsVertical = d.isStartTop() || d.isStartBottom();
    if (startIsVertical) {

        // For a vertical start, the primary offset is vertical.
        // Compute an intermediate x-coordinate as the average.
        int bendX = (startPoint.x + destPoint.x) / 2;
        // Draw a horizontal segment from startPoint to the bend.
        g.drawLine(startPoint.x, startPoint.y, bendX, startPoint.y);
        // Then draw a vertical segment from the first bend to the level of the destination port.
        g.drawLine(bendX, startPoint.y, bendX, destPoint.y);
        // Finally, draw a horizontal segment to the destination port.
        g.drawLine(bendX, destPoint.y, destPoint.x, destPoint.y);
    } else {
        // For a horizontal start, the primary offset is horizontal.
        // Compute an intermediate y-coordinate as the average.
        int bendY = (startPoint.y + destPoint.y) / 2;
        // Draw a vertical segment from startPoint to the bend.
        g.drawLine(startPoint.x, startPoint.y, startPoint.x, bendY);
        // Then draw a horizontal segment from the first bend to the level of the destination port.
        g.drawLine(startPoint.x, bendY, destPoint.x, bendY);
        // Finally, draw a vertical segment to the destination port.
        g.drawLine(destPoint.x, bendY, destPoint.x, destPoint.y);
    }

    g.setStroke(oldStroke);
}

    protected void drawDiamond(Graphics2D g, int x, int y, boolean filled, ConnectionSide side) {
        int[] rawX = { -4, 0, 4, 0 };
        int[] rawY = { 0, 8, 0, -8 };

        // Arrays for the final, possibly rotated shape
        int[] xPoints = new int[4];
        int[] yPoints = new int[4];

        switch (side) {
            case TOP -> {
                // No rotation needed
                for (int i = 0; i < 4; i++) {
                    xPoints[i] = rawX[i];
                    yPoints[i] = rawY[i];
                }
                y -= 8;
            }
            case BOTTOM -> {
                // Rotate 180°: (x, y) -> (-x, -y)
                for (int i = 0; i < 4; i++) {
                    xPoints[i] = -rawX[i];
                    yPoints[i] = -rawY[i];
                }
                y += 8;
            }
            case LEFT -> {
                // Rotate 90° CCW: (x, y) -> (-y, x)
                for (int i = 0; i < 4; i++) {
                    xPoints[i] = -rawY[i];
                    yPoints[i] = rawX[i];
                }
                x -= 8;
            }
            case RIGHT -> {
                // Rotate 90° CW: (x, y) -> (y, -x)
                for (int i = 0; i < 4; i++) {
                    xPoints[i] = rawY[i];
                    yPoints[i] = -rawX[i];
                }
                x += 8;
            }
        }

        // Translate the diamond so that (x, y) is the center
        for (int i = 0; i < 4; i++) {
            xPoints[i] += x;
            yPoints[i] += y;
        }

        // Draw or fill the polygon
        diamond = new Polygon(xPoints, yPoints, 4);
        if (filled) {
            g.fillPolygon(diamond);
        } else {
            g.drawPolygon(diamond);
        }
    }
}