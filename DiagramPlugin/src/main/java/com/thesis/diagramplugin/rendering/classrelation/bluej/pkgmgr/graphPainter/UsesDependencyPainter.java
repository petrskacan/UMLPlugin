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

import com.thesis.diagramplugin.rendering.classrelation.bluej.graph.GraphEditor;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.BendPoint;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.Dependency;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.UsesDependency;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.ConnectionSide;

import java.awt.*;
import java.util.ArrayList;
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
    private LineStyle style = GraphEditor.getLineStyle();


    public UsesDependencyPainter() {
    }

    public void paint(Graphics2D g, Dependency dependency, boolean hasFocus) {
        if (!(dependency instanceof UsesDependency)) {
            throw new IllegalArgumentException("Not a UsesDependency");
        }
        style = GraphEditor.getLineStyle();
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

    protected void paintOrthogonalLineAlternative(int recalcSrcX, int recalcSrcY, UsesDependency d,
                                                  Graphics2D g, int recalcDstX, int recalcDstY, Stroke oldStroke) {
        // Offsets to adjust the starting/ending positions relative to the node
        int offsetX = 16;
        int offsetY = 16;

        // 1. Compute adjusted exit points at the nodes
        Point startPoint = new Point(recalcSrcX, recalcSrcY);
        if (d.isStartTop()) {
            startPoint.y -= offsetY;
        } else if (d.isStartBottom()) {
            startPoint.y += offsetY;
        } else if (d.isStartLeft()) {
            startPoint.x -= offsetX;
        } else if (d.isStartRight()) {
            startPoint.x += offsetX;
        }
        d.setRecalcStart(startPoint);

        Point endPoint = new Point(recalcDstX, recalcDstY);
        if (d.isEndTop()) {
            endPoint.y -= offsetY;
        } else if (d.isEndBottom()) {
            endPoint.y += offsetY;
        } else if (d.isEndLeft()) {
            endPoint.x -= offsetX;
        } else if (d.isEndRight()) {
            endPoint.x += offsetX;
        }
        d.setRecalcEnd(endPoint);

        // Save the current stroke and use the provided stroke during drawing.
        Stroke previousStroke = g.getStroke();
        g.setStroke(oldStroke);

        // 2. Handle self-loop separately:
        if (d.from == d.to) {
            // For a self-loop, create a simple loop shape using two bend points.
            List<BendPoint> loopBendPoints = new ArrayList<>();
            // Option: go upward first then sideways (you may adjust the multipliers as needed)
            BendPoint bend1 = new BendPoint(startPoint.x, startPoint.y - 2 * offsetY);
            BendPoint bend2 = new BendPoint(endPoint.x + 2 * offsetX, startPoint.y - 2 * offsetY);
            loopBendPoints.add(bend1);
            loopBendPoints.add(bend2);

            // Draw the self-loop segments:
            // From the original source coordinate to the adjusted start point
            g.drawLine(recalcSrcX, recalcSrcY, startPoint.x, startPoint.y);
            // From the start point to the first bend point
            g.drawLine(startPoint.x, startPoint.y, bend1.x, bend1.y);
            // From the first to the second bend point
            g.drawLine(bend1.x, bend1.y, bend2.x, bend2.y);
            // From the second bend point to the adjusted end point
            g.drawLine(bend2.x, bend2.y, endPoint.x, endPoint.y);
            // Finally, if needed, draw a short line from endPoint to the true destination
            g.drawLine(endPoint.x, endPoint.y, recalcDstX, recalcDstY);

            // If dependency is selected, draw handles on the loop bend points.
            if (d.isSelected()) {
                drawBendHandle(g, loopBendPoints);
            }
            g.setStroke(previousStroke);
            return;
        }

        // 3. Compute two-bend orthogonal route for non-self-loop connectors.
        List<BendPoint> bendPoints = new ArrayList<>();
        int dx = endPoint.x - startPoint.x;
        int dy = endPoint.y - startPoint.y;

        // Choose the route based on which distance is larger
        if (Math.abs(dx) > Math.abs(dy)) {
            // If horizontal distance is greater, go horizontally halfway first, then vertically.
            int midX = startPoint.x + dx / 2;
            bendPoints.add(new BendPoint(midX, startPoint.y));
            bendPoints.add(new BendPoint(midX, endPoint.y));
        } else {
            // Otherwise, move vertically halfway first, then horizontally.
            int midY = startPoint.y + dy / 2;
            bendPoints.add(new BendPoint(startPoint.x, midY));
            bendPoints.add(new BendPoint(endPoint.x, midY));
        }

        // Optionally update the dependency's stored bend points.
        d.getBendPoints().addAll(bendPoints);

        // 4. Draw the connector in segments:
        // Start: from the original coordinate to the start point.
        g.drawLine(recalcSrcX, recalcSrcY, startPoint.x, startPoint.y);
        // Then, connect each bend point sequentially.
        Point current = startPoint;
        for (Point bend : bendPoints) {
            g.drawLine(current.x, current.y, bend.x, bend.y);
            current = bend;
        }
        // Connect the last bend to the adjusted end point.
        g.drawLine(current.x, current.y, endPoint.x, endPoint.y);
        // Finally, draw from the adjusted end point to the destination coordinate.
        g.drawLine(endPoint.x, endPoint.y, recalcDstX, recalcDstY);

        // 5. Draw selection handles if the dependency is selected.
        if (d.isSelected()) {
            drawBendHandle(g, bendPoints);
        }

        // Restore the original stroke.
        g.setStroke(previousStroke);
    }




    protected void paintLine(int recalcSrcY, UsesDependency d,
                             Graphics2D g, int recalcSrcX, int recalcDstX, int recalcDstY, Stroke oldStroke) {
            // Assume that d.getRecalcStart() and d.getRecalcEnd() have been computed with offsets.
        int offsetX = 16;
        int offsetY = 16;
        Point startPoint = new Point(recalcSrcX, recalcSrcY);
        if (d.isStartTop()) {
            startPoint.y -= offsetY;
        } else if (d.isStartBottom()) {
            startPoint.y += offsetY;
        } else if (d.isStartLeft()) {
            startPoint.x -= offsetX;
        } else if (d.isStartRight()) {
            startPoint.x += offsetX;
        }
        d.setRecalcStart(startPoint);

        Point endPoint = new Point(recalcDstX, recalcDstY);
        if (d.isEndTop()) {
            endPoint.y -= offsetY;
        } else if (d.isEndBottom()) {
            endPoint.y += offsetY;
        } else if (d.isEndLeft()) {
            endPoint.x -= offsetX;
        } else if (d.isEndRight()) {
            endPoint.x += offsetX;
        }
        d.setRecalcEnd(endPoint);

            // Draw from the original source coordinate to the adjusted start point.
            g.drawLine(recalcSrcX, recalcSrcY, startPoint.x, startPoint.y);

            // Draw each segment between bend points.
            Point current = startPoint;
            for (Point bend : d.getBendPoints()) {
                g.drawLine(current.x, current.y, bend.x, bend.y);
                current = bend;
            }

            // Draw from the last bend point to the adjusted end point, then to the real destination.
            g.drawLine(current.x, current.y, endPoint.x, endPoint.y);
            g.drawLine(endPoint.x, endPoint.y, recalcDstX, recalcDstY);

            // (Optionally, draw handles if selected, etc.)
            if (d.isSelected()) {
                drawBendHandle(g, d.getBendPoints());
            }

            // Restore the original stroke if needed.
            g.setStroke(oldStroke);
    }

    private void drawBendHandle(Graphics2D g, List<BendPoint> points) {
        final int r = 5;
        for(BendPoint point : points) {
            g.setColor(point.getFill());
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

    private List<BendPoint> generateOrthogonalBendPoints(Point start, Point end) {
        List<BendPoint> bendPoints = new ArrayList<>();

        // Možný L-tvar se zalomením jen v jednom bodě
        boolean preferHorizontal = Math.abs(end.x - start.x) > Math.abs(end.y - start.y);

        if (preferHorizontal) {
            bendPoints.add(new BendPoint(end.x, start.y)); // L-tvar
        } else {
            bendPoints.add(new BendPoint(start.x, end.y));
        }

        return bendPoints;
    }


}