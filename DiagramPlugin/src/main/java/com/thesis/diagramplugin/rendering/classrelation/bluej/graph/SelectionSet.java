/*
 This file is part of the BlueJ program. 
 Copyright (C) 1999-2009,2010,2013,2014  Michael Kolling and John Rosenberg 
 
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
package com.thesis.diagramplugin.rendering.classrelation.bluej.graph;

import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.ClassTarget;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.DependentTarget;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.PackageTarget;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SelectionSet holds a set of selected graph elements. By inserting an
 * element into this set, it is automatically set to selected.
 * 
 * @author fisker
 * @author Michael Kolling
 */
public final class SelectionSet
{
    private Set<SelectableGraphElement> elements = new HashSet<SelectableGraphElement>();

    /**
     * 
     *
     */
    public SelectionSet()
    {}

    /**
     * Add an unselected selectable graphElement to the set and
     * set it's 'selected' flag.
     * 
     * @param element  The element to add
     */
    public void add(SelectableGraphElement element)
    {
        if (!element.isSelected()) {
            element.setSelected(true);
            elements.add(element);
        }
    }
    
    /**
     * Add an already selected element to the set; to be used during initialisation only.
     * 
     * @param element   The element, which is marked selected, to be tracked in the selection set
     */
    public void addExisting(SelectableGraphElement element)
    {
        elements.add(element);
    }
    
    /**
     * Add all the elements from another selection set to this one.
     */
    public void addAll(SelectionSet newSet)
    {
        elements.addAll(newSet.elements);
    }

    /**
     * Remove the graphElement and set it's 'selected' flag false.
     * 
     *
     */
    public void remove(SelectableGraphElement element)
    {
        if (element != null) {
            element.setSelected(false);
            elements.remove(element);
        }
    }

    /**
     * Remove all the graphElements from the list. Set each removed grahpElement
     * 'selected' flag to false. Does NOT selfuse remove method.
     */
    public void clear()
    {
        for (SelectableGraphElement element : elements) {
            element.setSelected(false);
        }
        elements.clear();
    }

    /**
     * Perform a double click on the selection.
     * 
     * @param evt  The mouse event that originated this double click.
     */
    public void doubleClick(MouseEvent evt)
    {
        final MouseEvent event = evt;
        for (SelectableGraphElement element : elements) {
            element.doubleClick(event);
        }
    }
    
    /**
     * Move the selected elements by the specified deltas.
     */
    public void move(int deltaX, int deltaY)
    {
        for (GraphElement element : elements) {
            if(element instanceof Moveable) {
                Moveable target = (Moveable) element;
                if (target.isMoveable()) {
                    Point delta = restrictDelta(deltaX, deltaY);
                    delta = restrictContained(target, delta);
                    target.setDragging(true);
                    target.setGhostPosition(delta.x, delta.y);
                    if (target instanceof PackageTarget packageTarget && packageTarget.getPackage() != null) {;
                        for (DependentTarget dt : packageTarget.getPackage().getPackages().get(packageTarget)) {
                            if (dt instanceof Moveable mt) {
                                mt.setDragging(true);
                                mt.setGhostPosition(delta.x, delta.y);
                            }
                        }
                    }
                }
            }
        }
    }

    private Point restrictContained(Moveable t, Point delta) {
        if (t instanceof ClassTarget target && target.getContainerTarget() != null) {
            Point loc = target.getContainerTarget().getComponent().getLocation();
            int width = target.getContainerTarget().getComponent().getWidth();
            int height = target.getContainerTarget().getComponent().getHeight();

            if (target.getX() + delta.x < loc.x) {
                delta.x = 0;
            } else if (target.getX() + (target.isExpanded() ? target.getExpWidth() : target.getWidth()) + delta.x > loc.x + width) {
                delta.x = 0;
            }

            int contentOffset = 0;
            if (target.getContainerTarget() instanceof PackageTarget pkg) {
                contentOffset += pkg.getHeaderHeight();
            }
            if (target.getY() + delta.y < loc.y + contentOffset) {
                delta.y = 0;
            } else if (target.getY() + (target.isExpanded() ? target.getExpHeight() : target.getHeight()) + delta.y > loc.y + height) {
                delta.y = 0;
            }
        }
        return delta;
    }

    /**
     * Restrict the delta so that no target moves out of the screen.
     */
    private Point restrictDelta(int deltaX, int deltaY)
    {
        for (GraphElement element : elements) {
            if(element instanceof Moveable) {
                Moveable target = (Moveable) element;

                if(target.getX() + deltaX < 0) {
                    deltaX = -target.getX();
                }
                if(target.getY() + deltaY < 0) {
                    deltaY = -target.getY();
                }
            }
        }
        return new Point(deltaX, deltaY);
    }


    /**
     * A move gesture (either move or resize) has stopped. Inform all elements
     * in this selection that they shoudl react.
     */
    public void moveStopped()
    {
        for (GraphElement element : elements) {
            if(element instanceof Moveable) {
                Moveable moveable = (Moveable) element;
                moveable.setPositionToGhost();
                if (moveable instanceof PackageTarget packageTarget && packageTarget.getPackage() != null) {;
                    for (DependentTarget dt : packageTarget.getPackage().getPackages().get(packageTarget)) {
                        if (dt instanceof Moveable mt) {
                            mt.setPositionToGhost();
                        }
                    }
                }
            }
        }   
    }
    

    /**
     * A resize operation has initiated (or continued). Inform al elements
     * that they should react to the resize.
     * 
     * @param deltaX  The current x offset from the start of the resize.
     * @param deltaY  The current y offset from the start of the resize.
     */
    public void resize(int deltaX, int deltaY)
    {
        for (GraphElement element : elements) {
            if(element instanceof Moveable) {
                Moveable target = (Moveable) element;
                if (target.isResizable()) {
                    Point delta = new Point(deltaX, deltaY);
                    if (target instanceof PackageTarget packageTarget) {
                        delta = restrictResizeDelta(packageTarget, deltaX, deltaY);
                    }
                    target.setDragging(true);
                    target.setGhostSize(delta.x, delta.y);
                }
            }
        }
    }

    private Point restrictResizeDelta(PackageTarget packageTarget, int deltaX, int deltaY) {
        List<DependentTarget> children = packageTarget.getPackage().getPackages().get(packageTarget);
        Point pkgLocation = packageTarget.getComponent().getLocation();

        for (DependentTarget child : children) {
            if (child instanceof ClassTarget clsTarget) {
                boolean stop = false;
                if (pkgLocation.x + packageTarget.getWidth() + deltaX < clsTarget.getX() + (clsTarget.isExpanded() ? clsTarget.getExpWidth() : clsTarget.getWidth())) {
                    deltaX = 0;
                    stop = true;
                }
                if (pkgLocation.y + packageTarget.getHeight() + deltaY < clsTarget.getY() + (clsTarget.isExpanded() ? clsTarget.getExpHeight() : clsTarget.getHeight())) {
                    deltaY = 0;
                    stop = true;
                }
                if (stop) {
                    break;
                }
            }
        }

        return new Point(deltaX, deltaY);
    }

    /**
     * Tell whether the selection is empty.
     * 
     * @return  true, if the selection is empty.
     */
    public boolean isEmpty()
    {
        return elements.isEmpty();
    }
    
    /**
     * Change the selection to contain only the specified element.
     * 
     * @param element  The single element to hold in the selection. 
     */
    public void selectOnly(SelectableGraphElement element)
    {
        clear();
        add(element);
        element.singleSelected();
    }
    
    /** 
     * Return a random vertex from this selection.
     * @return  An vertex, or null, if none exists.
     */
    public Vertex getAnyVertex()
    {
        for (GraphElement element : elements) {
            if(element instanceof Vertex) {
                return (Vertex) element;
            }
        }
        return null;
    }

    
    /** 
     * Return a random vertex from this selection.
     * @return  An vertex, or null, if none exists.
     */
    public Edge getAnyEdge()
    {
        for (GraphElement element : elements) {
            if(element instanceof Edge) {
                return (Edge) element;
            }
        }
        return null;
    }
    
    public void expand(ActionEvent e) {
        for (SelectableGraphElement element : elements) {
            if(element instanceof IExpandable iExpandable) {
                iExpandable.expand();
            }
        }
    }
    
    public void colapse(ActionEvent e) {
        for (SelectableGraphElement element : elements) {
            if(element instanceof IExpandable iExpandable) {
                iExpandable.collapse();
            }
        }
    }

    public List<ClassTarget> hide(ActionEvent e) {
        List<ClassTarget> hidden = new ArrayList<>();
        for (SelectableGraphElement element : elements) {
            element.setVisible(false);
            if (element instanceof ClassTarget tmp) {
                hidden.add(tmp);
            }
            if (element instanceof PackageTarget packageTarget && packageTarget.getPackage() != null) {;
                for (DependentTarget dt : packageTarget.getPackage().getPackages().get(packageTarget)) {
                    dt.setVisible(false);
                    if (dt instanceof ClassTarget tmp) {
                        hidden.add(tmp);
                    }
                }
            }
        }
        return hidden;
    }

}
