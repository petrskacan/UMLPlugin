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

import com.intellij.ui.JBColor;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.Dependency;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.UsesDependency;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.ConnectionSide;

import java.awt.*;
import java.util.List;

/**
 * Paints usesDependencies
 * 
 * @author fisker
 * @author Michael Kolling
 */
public class UsesDependencyPainter
    implements DependencyPainter {
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


    public UsesDependencyPainter() {
    }

    public void paint(Graphics2D g, Dependency dependency, boolean hasFocus) {
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
        } else {
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

        drawArrow(g, dst_x, dst_y,d.getEndConnectionSide());
        g.setStroke(dashedStroke);

        paintLine(src_y, d, g, src_x, dst_x, dst_y, oldStroke);
    }

    protected void paintLine(int recalcSrcY, UsesDependency d,
                             Graphics2D g, int recalcSrcX, int recalcDstX, int recalcDstY, Stroke oldStroke) {

        int offsetX = 16;
        int offsetY = 16;


        // 1. Compute source and target "exit" points
        Point startPoint = new Point(recalcSrcX, recalcSrcY);
        if (d.isStartTop()) startPoint.y -= offsetY;
        else if (d.isStartBottom()) startPoint.y += offsetY;
        else if (d.isStartLeft()) startPoint.x -= offsetX;
        else if (d.isStartRight()) startPoint.x += offsetX;
        d.setRecalcStart(startPoint);

        Point endPoint = new Point(recalcDstX, recalcDstY);
        if (d.isEndTop()) endPoint.y -= offsetY;
        else if (d.isEndBottom()) endPoint.y += offsetY;
        else if (d.isEndLeft()) endPoint.x -= offsetX;
        else if (d.isEndRight()) endPoint.x += offsetX;
        d.setRecalcEnd(endPoint);

        if (d.from == d.to) {
            // Handle self-loop with 2 editable bend points
            List<Point> bends = d.getBendPoints();

            if (bends.size() != 2 || d.isAutoLayout()) {
                bends.clear();

                Point topRight = new Point(endPoint.x, startPoint.y);
                Point bottomRight = new Point(endPoint.x, endPoint.y);

                bends.add(topRight);
                bends.add(bottomRight);
                d.setAutoLayout(true);
            }

            Point bend1 = bends.get(0);
            Point bend2 = bends.get(1);

            if(!usesDiamond)
            g.drawLine(recalcSrcX, recalcSrcY, startPoint.x, startPoint.y);
            g.drawLine(startPoint.x, startPoint.y, bend1.x, bend1.y);
            g.drawLine(bend1.x, bend1.y, bend2.x, bend2.y);
            g.drawLine(bend2.x, bend2.y, recalcDstX, recalcDstY);

            if (d.isSelected()) {
                drawBendHandle(g, bends);
            }

            g.setStroke(oldStroke);
            return; // skip the rest of the normal path logic
        }

        // 2. Use bendPoints (if manually edited), otherwise regenerate
        List<Point> bends = d.getBendPoints();

        if (bends.size() != 2 || d.isAutoLayout()) {
            bends.clear(); // regen from geometry
            boolean startVertical = d.isStartTop() || d.isStartBottom();

            if (startVertical) {
                int bendX = (startPoint.x + endPoint.x) / 2;
                bends.add(new Point(bendX, startPoint.y));
                bends.add(new Point(bendX, endPoint.y));
            } else {
                int bendY = (startPoint.y + endPoint.y) / 2;
                bends.add(new Point(startPoint.x, bendY));
                bends.add(new Point(endPoint.x, bendY));
            }

            d.setAutoLayout(true); // still in auto mode
        }

        // 3. Draw: src ➝ bend1 ➝ bend2 ➝ end
        Point bend1 = bends.get(0);
        Point bend2 = bends.get(1);

        if(!usesDiamond)
        g.drawLine(recalcSrcX, recalcSrcY, startPoint.x, startPoint.y);
        g.drawLine(startPoint.x, startPoint.y, bend1.x, bend1.y);
        g.drawLine(bend1.x, bend1.y, bend2.x, bend2.y);
        g.drawLine(bend2.x, bend2.y, endPoint.x, endPoint.y);
        g.drawLine(endPoint.x, endPoint.y, recalcDstX, recalcDstY);

        // 4. Draw handles if selected
        if (d.isSelected()) {
            drawBendHandle(g, bends);
        }

        g.setStroke(oldStroke);
    }

    private void drawBendHandle(Graphics2D g, List<Point> points) {
        final int r = 5;
        g.setColor(JBColor.BLUE);
        for(Point point : points) {
            g.fillOval(point.x - r, point.y - r, r * 2, r * 2);
        }
    }

    private void drawArrow(Graphics2D g, int x, int y, ConnectionSide side)
    {
        int arrowLength = 10;
        int arrowWidth  = 4;
        int endX1 = 0, endY1 = 0, endX2 = 0, endY2 = 0;

        switch (side) {
            case TOP -> {
                endX1 = x - arrowWidth;
                endY1 = y - arrowLength;
                endX2 = x + arrowWidth;
                endY2 = y - arrowLength;
            }
            case BOTTOM -> {
                endX1 = x - arrowWidth;
                endY1 = y + arrowLength;
                endX2 = x + arrowWidth;
                endY2 = y + arrowLength;
            }
            case LEFT -> {
                endX1 = x - arrowLength;
                endY1 = y + arrowWidth;
                endX2 = x - arrowLength;
                endY2 = y - arrowWidth;
            }
            case RIGHT -> {
                endX1 = x + arrowLength;
                endY1 = y - arrowWidth;
                endX2 = x + arrowLength;
                endY2 = y + arrowWidth;
            }
        }
        g.drawLine(x, y, endX1, endY1);
        g.drawLine(x, y, endX2, endY2);
    }

    protected void drawDiamond(Graphics2D g, int x, int y, boolean filled, ConnectionSide side) {
        int[] rawX = {-4, 0, 4, 0};
        int[] rawY = {0, 8, 0, -8};

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