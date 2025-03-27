/*
 This file is part of the BlueJ program. 
 Copyright (C) 1999-2009,2012,2013,2015  Michael Kolling and John Rosenberg 
 
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

import com.thesis.diagramplugin.rendering.classrelation.bluej.graph.Edge;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.Package;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.DependentTarget;

import java.awt.*;
import java.util.Properties;

/**
 * A dependency between two targets in a package.
 * 
 * @author Michael Cahill
 * @author Michael Kolling
 */
public abstract class Dependency extends Edge
{
    Package pkg;
    protected boolean selected = false;
    //    protected static final float strokeWithDefault = 1.0f;
    //    protected static final float strokeWithSelected = 2.0f;

    static final int SELECT_DIST = 4;

    public Dependency(Package pkg, DependentTarget from, DependentTarget to)
    {
        super(from, to);
        this.pkg = pkg;
//        this.component = new EdgeJComponent();
//        this.component.setFocusable(true);
////        this.component.setBounds(x, y, width, height);
//        this.component.setVisible(true);
    }

    public Dependency(Package pkg)
    {
        this(pkg, null, null);
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof Dependency))
            return false;
        Dependency d = (Dependency) other;
        return (d != null) && (d.getType() == getType()) && (d.from == from) && (d.to == to);
    }

    @Override
    public int hashCode()
    {
        return to.hashCode() - from.hashCode();
    }

    public void repaint()
    {
        pkg.repaint();
    }

    public DependentTarget getFrom()
    {
        return (DependentTarget) from;
    }

    public DependentTarget getTo()
    {
        return (DependentTarget) to;
    }

    /**
     * Returns the type of this dependency. This information is used by
     * extensions to distinguish between the different types of dependencies.
     * Subclasses must implement this method and return an appropriate constant
     * of
     * 
     * @return The type of this dependency;
     */
    public abstract DependencyType getType();

    /**
     * Determine the dependency's "to" and "from" nodes by loading their names from the
     * given Properties.
     * 
     * @return true if successful or false if the named targets could not be found
     */
    public boolean load(Properties props, String prefix)
    {
        String fromName = props.getProperty(prefix + ".from");
        if (fromName == null) {
            System.err.println("No 'from' target specified for dependency " + prefix);
            return false;
        }
        this.from = pkg.getTarget(fromName);
        if (! (this.from instanceof DependentTarget)) {
            System.err.println("Failed to find 'from' target " + fromName);
            return false;
        }
                
        String toName = props.getProperty(prefix + ".to");
        if (toName == null) {
            System.err.println("No 'to' target specified for dependency " + prefix);
            return false;
        }
        this.to = pkg.getTarget(toName);
        if (! (this.to instanceof DependentTarget)) {
            System.err.println("Failed to find 'to' target " + toName);
            return false;
        }
        
        return true;
    }

    public void save(Properties props, String prefix)
    {
        props.put(prefix + ".from", ((DependentTarget) from).getIdentifierName());
        props.put(prefix + ".to", ((DependentTarget) to).getIdentifierName());
    }

    public String toString()
    {
        return getFrom().getIdentifierName() + " --> " + getTo().getIdentifierName();
    }

    @Override
    public void setVisible(boolean visible)
    {
        if (visible != isVisible()) {
            super.setVisible(visible);
        }
    }

    @Override
    public boolean isVisible()
    {
        return super.isVisible() && from.isVisible() && to.isVisible();
    }

    @Override
    public void setSelected(boolean selected)
    {
        this.selected = selected;
        repaint();
    }

    @Override
    public boolean isSelected()
    {
        return selected;
    }

    @Override
    public boolean isHandle(int x, int y)
    {
        return false;
    }

    /**
     * Contains method for dependencies that are drawn as more or less straight
     * lines (e.g. extends). Should be overwritten for dependencies with
     * different shape.
     */
    @Override
    public boolean contains(int x, int y)
    {
        Line line = computeLine();
        Rectangle bounds = getBoxFromLine(line);

        // Now check if <p> is in the rectangle
        if (!bounds.contains(x, y)) {
            return false;
        }

        // Get the angle of the line from pFrom to p
        double theta = Math.atan2(-(line.from.y - y), line.from.x - x);

        double norm = normDist(line.from.x, line.from.y, x, y, Math.sin(line.angle - theta));
        return (norm < SELECT_DIST * SELECT_DIST);
    }

    static final double normDist(int ax, int ay, int bx, int by, double scale)
    {
        return ((ax - bx) * (ax - bx) + (ay - by) * (ay - by)) * scale * scale;
    }

    /**
     * Given the line describing start and end points of this dependency, return
     * its bounding box.
     */
    protected Rectangle getBoxFromLine(Line line)
    {
        int x = Math.min(line.from.x, line.to.x) - SELECT_DIST;
        int y = Math.min(line.from.y, line.to.y) - SELECT_DIST;
        int width = Math.max(line.from.x, line.to.x) - x + (2*SELECT_DIST);
        int height = Math.max(line.from.y, line.to.y) - y + (2*SELECT_DIST);

        return new Rectangle(x, y, width, height);
    }

    /**
     * Compute line information (start point, end point, angle) for the current
     * state of this dependency. This is accurate for dependencis that are drawn
     * as straight lines from and to the target border (such as extends
     * dependencies) and should be redefined for different shaped dependencies.
     */
    public Line computeLine()
    {
        // Compute centre points of source and dest target
        Point pFrom = new Point(from.getX() + from.getWidth() / 2, from.getY() + from.getHeight() / 2);
        Point pTo = new Point(to.getX() + to.getWidth() / 2, to.getY() + to.getHeight() / 2);

        // Get the angle of the line from pFrom to pTo.
        double angle = Math.atan2(-(pFrom.y - pTo.y), pFrom.x - pTo.x);

        // Compute intersection points with target border
        pFrom = ((DependentTarget) from).getAttachment(angle + Math.PI);
        pTo = ((DependentTarget) to).getAttachment(angle);

        return new Line(pFrom, pTo, angle);
    }

    /**
     * Inner class to describe the most important state of this dependency
     * (start point, end point, angle) concisely.
     */
    public class Line
    {
        public Point from;
        public Point to;
        double angle;

        Line(Point from, Point to, double angle)
        {
            this.from = from;
            this.to = to;
            this.angle = angle;
        }
    }
    
    public void singleSelected() { }

//    @Getter
//    JComponent component;
//
//    public class EdgeJComponent extends JComponent implements Accessible {
//        public AccessibleContext getAccessibleContext() {
//            if (accessibleContext == null) {
//                accessibleContext = new AccessibleJComponent() {
//                    @Override
//                    public String getAccessibleName() {
//                        return "edge";
//                    }
//
//                    @Override
//                    public AccessibleRole getAccessibleRole() {
//                        return AccessibleRole.LIST_ITEM;
//                    }
//                };
//            }
//            return accessibleContext;
//        }
//
//        @Override
//        public void paintComponent(Graphics g) {
//            PainterManager.paint((Graphics2D) g, Dependency.this, true);
//        };
//
//    };

}