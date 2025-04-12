/*
 This file is part of the BlueJ program. 
 Copyright (C) 1999-2009,2012,2015  Michael Kolling and John Rosenberg 
 
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
package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency;


import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.Package;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.ConnectionSide;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.DependentTarget;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * A dependency between two targets in a package
 *
 * @author  Michael Kolling
 * @version $Id: UsesDependency.java 14822 2015-10-16 15:47:55Z davmac $
 */
public class UsesDependency extends Dependency
{
    private int sourceX, sourceY, destX, destY;
    private boolean startTop, endLeft;
    private ConnectionSide startConnectionSide;
    private List<BendPoint> bendPoints = new ArrayList<>();
    private boolean autoLayout = true;

    public boolean isAutoLayout() {
        return autoLayout;
    }

    public void setAutoLayout(boolean autoLayout) {
        this.autoLayout = autoLayout;
    }

    public List<BendPoint> getBendPoints() { return bendPoints; }
    public void setBendPoint(BendPoint point) { this.bendPoints.add(point); }

    public ConnectionSide getEndConnectionSide() {
        return endConnectionSide;
    }

    public void setEndConnectionSide(ConnectionSide endConnectionSide) {
        this.endConnectionSide = endConnectionSide;
    }

    private ConnectionSide endConnectionSide;

    public ConnectionSide getStartConnectionSide() {
        return startConnectionSide;
    }

    public void setStartConnectionSide(ConnectionSide connectionSide) {
        this.startConnectionSide = connectionSide;
    }

    private boolean flag;    // flag to mark some dependencies

    public UsesDependency(Package pkg, DependentTarget from, DependentTarget to)
    {
        super(pkg, from, to);
        flag = false;
    }

    public UsesDependency(Package pkg)
    {
        this(pkg, null, null);
    }

    public void setSourceCoords(int src_x, int src_y, ConnectionSide side)
    {
        this.sourceX = src_x;
        this.sourceY = src_y;
        this.setStartConnectionSide(side);
    }

    public void setDestCoords(int dst_x, int dst_y, ConnectionSide side)
    {
        this.destX = dst_x;
        this.destY = dst_y;
        this.setEndConnectionSide(side);
    }

    public void setCoord(int dst_x, int dst_y, ConnectionSide side, boolean isOutgoing)
    {
        if(isOutgoing)
        {
            setSourceCoords(dst_x, dst_y, side);
        }
        else
        {
            setDestCoords(dst_x, dst_y, side);
        }
    }
    /**
     * Test whether (x,y) is in rectangle (x0,x1,y0,y1),
     */
    static final boolean inRect(int x, int y, int x0, int y0, int x1, int y1)
    {
        int xmin = Math.min(x0, x1);
        int xmax = Math.max(x0, x1);
        int ymin = Math.min(y0, y1);
        int ymax = Math.max(y0, y1);
        return (xmin <= x) && (ymin <= y) && (x < xmax) && (y < ymax);
    }

    @Override
    public boolean contains(int x, int y)
    {
        List<Point> points = new ArrayList<>();
        points.add(new Point(getSourceX(), getSourceY()));
        points.add(getRecalcStart());
        if (bendPoints != null) {
            points.addAll(bendPoints);
        }
        points.add(getRecalcEnd());
        points.add(new Point(getDestX(), getDestY()));

        // Check every line segment between the points
        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            if (lineContainsPoint(p1.x, p1.y, p2.x, p2.y, x, y, SELECT_DIST)) {
                return true;
            }
        }

        return false;
    }
    private boolean lineContainsPoint(int x1, int y1, int x2, int y2, int px, int py, int tolerance) {
        Rectangle hitBox = new Rectangle(
                Math.min(x1, x2) - tolerance,
                Math.min(y1, y2) - tolerance,
                Math.abs(x1 - x2) + 2 * tolerance,
                Math.abs(y1 - y2) + 2 * tolerance
        );
        if (!hitBox.contains(px, py)) {
            return false;
        }
        double distance = Line2D.ptSegDist(x1, y1, x2, y2, px, py);
        return distance <= tolerance;
    }

    
    /**
     * Compute line information (start point, end point, angle)
     * for the current state of this dependency.
     */
    public Line computeLine()
    {
        return new Line(new Point(sourceX, sourceY), new Point(destX, destY), 0.0);
    }
    

    public void save(Properties props, String prefix)
    {
        super.save(props, prefix);

        // This may be overridden by decendents
        props.put(prefix + ".type", "UsesDependency");
    }

    public void setFlag(boolean value)
    {
        flag = value;
    }

    public boolean isFlagged()
    {
        return flag;
    }
    
    /**
     * @return Returns the sourceX.
     */
    public int getSourceX() {
        return sourceX;
    }
    /**
     * @return Returns the sourceY.
     */
    public int getSourceY() {
        return sourceY;
    }
    /**
     * @param sourceY The sourceY to set.
     */
    public void setSourceY(int sourceY) {
        this.sourceY = sourceY;
    }
    /**
     * @return Returns the destX.
     */
    public int getDestX() {
        return destX;
    }
    /**
     * @return Returns the destY.
     */
    public int getDestY() {
        return destY;
    }

    public boolean isStartTop() {
        return getStartConnectionSide() == ConnectionSide.TOP;
    }
    public boolean isStartLeft() {
        return getStartConnectionSide() == ConnectionSide.LEFT;
    }
    public boolean isStartBottom() {
        return getStartConnectionSide() == ConnectionSide.BOTTOM;
    }
    public boolean isStartRight() {
        return getStartConnectionSide() == ConnectionSide.RIGHT;
    }

    public boolean isEndLeft() {
        return getEndConnectionSide() == ConnectionSide.LEFT;
    }
    public boolean isEndRight() {
        return getEndConnectionSide() == ConnectionSide.RIGHT;
    }
    public boolean isEndTop() {
        return getEndConnectionSide() == ConnectionSide.TOP;
    }
    public boolean isEndBottom() {
        return getEndConnectionSide() == ConnectionSide.BOTTOM;
    }

    public boolean isResizable()
    {
        return false;
    }

    @Override
    public DependencyType getType()
    {
        return DependencyType.USES;
    }
}
