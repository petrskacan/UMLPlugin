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

import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.BendPoint;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.Dependency;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.UsesDependency;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.ClassTarget;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.DependentTarget;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.Target;

import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.List;

/**
 * This class controls the selection (the set of selected elements in the graph).
 * To do this, it maintains a selection set, a marquee (a graphical selection rectangle)
 * and a rubber band (for drawing new edges). Both the marquee and the rubber band can
 * be inactive.
 */
public class SelectionController
    implements FocusListener, MouseListener, MouseMotionListener
{
    protected GraphEditor graphEditor;
    protected Graph graph;
    
    protected Marquee marquee;
    protected SelectionSet selection;   // Contains the elements that have been selected
    protected RubberBand rubberBand;

    protected boolean moving = false;
    protected boolean resizing = false;

    protected int dragStartX;
    protected int dragStartY;

    private int keyDeltaX;
    private int keyDeltaY;
    protected UsesDependency selectedDependency;
    protected BendPoint selectedBendPoint;
    protected Point selectedSegmentStart = null;
    protected Point selectedSegmentEnd = null;
    protected boolean draggingSegment = false;


    private int currentDependencyIndex;  // for cycling through dependencies

    private TraverseStrategy traverseStragegiImpl = new TraverseStrategyImpl();




    /**
     * Create the controller for a given graph editor.
     * @param graphEditor
     * @param
     */
    public SelectionController(GraphEditor graphEditor)
    {
        this.graphEditor = graphEditor;
        this.graph = graphEditor.getGraph();
        marquee = new Marquee(graph);
        selection = new SelectionSet();
        for (Iterator<? extends Vertex> i = graph.getVertices(); i.hasNext(); ) {
            Vertex v = i.next();
            if (v.isSelected()) {
                selection.addExisting(v);
            }
        }
        for (Iterator<? extends Edge> i = graph.getEdges(); i.hasNext(); ) {
            Edge e = i.next();
            if (e.isSelected()) {
                selection.addExisting(e);
            }
        }
    }

    public List<ClassTarget> hideSelected(ActionEvent e) {
        return this.selection.hide(e);
    }

    // ======= MouseListener interface =======

    /**
     * A mouse-pressed event. Analyse what we should do with it.
     */
    public void mousePressed(MouseEvent evt)
    {
        graphEditor.requestFocus();
        int clickX = evt.getX();
        int clickY = evt.getY();

        SelectableGraphElement clickedElement = graph.findGraphElement(clickX, clickY);
        // notifyPackage(clickedElement);
        
        if (clickedElement == null) {                           // nothing hit
            if (!isMultiselectionKeyDown(evt)) {
                selection.clear();
            }
            if (isButtonOne(evt))
                marquee.start(clickX, clickY);
        }
        else if (isButtonOne(evt)) {                            // clicked on something
            if (isMultiselectionKeyDown(evt)) {
                // a class was clicked, while multiselectionKey was down.
                if (clickedElement.isSelected()) {
                    selection.remove(clickedElement);
                }
                else {
                    selection.add(clickedElement);
                }
            }
            else {
                // a class was clicked without multiselection
                if (! clickedElement.isSelected()) {
                    selection.selectOnly(clickedElement);
                }
            }

            if(isDrawingDependency(evt)) {
                if (clickedElement instanceof Target)
                    rubberBand = new RubberBand(clickX, clickY, clickX, clickY);
            }
            else {
                dragStartX = clickX;
                dragStartY = clickY;

                if(clickedElement.isHandle(clickX, clickY)) {
                    resizing = true;
                }
                else {
                    moving = true;                        
                }
            }
        }
    }

    /**
     * The mouse was released.
     */
    public void mouseReleased(MouseEvent evt)
    {
        rubberBand = null;
        SelectionSet newSelection = marquee.stop();     // may or may not have had a marquee...
        if(newSelection != null) {
            selection.addAll(newSelection);
            graphEditor.repaint();
        }
        
        if(moving || resizing) {
            endMove();
            graphEditor.revalidate();
            graphEditor.repaint();
        }
    }
    
    /**
     * A mouse-clicked event. This is only interesting if it was a double
     * click. If so, inform every element in the current selection.
     */
    public void mouseClicked(MouseEvent evt)
    {
        if (isButtonOne(evt)) {
            if (evt.getClickCount() > 1) {
                selection.doubleClick(evt);
            }
        }
    }

    /**
     * The mouse pointer entered this component.
     */
    public void mouseEntered(MouseEvent e) {}

    /**
     * The mouse pointer exited this component.
     */
    public void mouseExited(MouseEvent e) {}

    // ======= end of MouseListener interface =======

    // ======= MouseMotionListener interface: =======

    /**
     * The mouse was moved - not interested here.
     */
    public void mouseMoved(MouseEvent evt) {}

    /**
     * The mouse was dragged - either draw a marquee or move some classes.
     */
    public void mouseDragged(MouseEvent evt)
    {
        if (isButtonOne(evt)) {
            if (marquee.isActive()) {
                Rectangle oldRect = marquee.getRectangle();                
                marquee.move(evt.getX(), evt.getY());
                Rectangle newRect = (Rectangle) marquee.getRectangle().clone();  
                if(oldRect != null) {
                    newRect.add(oldRect);
                }
                newRect.width++;
                newRect.height++;
                graphEditor.repaint(newRect);
            }
            else if (rubberBand != null) {
                rubberBand.setEnd(evt.getX(), evt.getY());
                graphEditor.repaint();
            }
            else 
            {
                if(! selection.isEmpty()) {
                    int deltaX = snapToGrid(evt.getX() - dragStartX);
                    int deltaY = snapToGrid(evt.getY() - dragStartY);
    
                    if(resizing) {
                        selection.resize(deltaX, deltaY);
                    }
                    else if (moving) {
                        selection.move(deltaX, deltaY);
                    }
                }
                graphEditor.repaint();
            }

        }
    }

    // ======= end of MouseMotionListener interface =======
    
    /**
     * Tell whether the package is currently drawing a dependency.
     */
    public boolean isDrawingDependency(MouseEvent evt)
    {
        return evt.getSource() instanceof Dependency;
    }

    
    private static boolean isArrowKey(KeyEvent evt)
    {
        return evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN
                || evt.getKeyCode() == KeyEvent.VK_LEFT || evt.getKeyCode() == KeyEvent.VK_RIGHT;
    }

    /**
     * Move the current selection to another selected class, depending on
     * current selection and the key pressed.
     */
    private void navigate(KeyEvent evt)
    {
        Vertex currentTarget = findSingleVertex();
        currentTarget = traverseStragegiImpl.findNextVertex(graph, currentTarget, evt.getKeyCode());
        selection.selectOnly(currentTarget);
    }

    /**
     * Prepare a key-based move operation.
     */
    private void startKeyboardMove()
    {
        keyDeltaX = 0;
        keyDeltaY = 0;
        moving = true;
    }
    
    /**
     * End a move or resize gesture.
     *
     */
    protected void endMove()
    {
        System.out.println("IN END MOVE METHOD");
        selection.moveStopped();
        moving = false;
        resizing = false;
    }
    
    /**
     * Prepare a key-based resize operation.
     */
    private void startKeyboardResize()
    {
        keyDeltaX = 0;
        keyDeltaY = 0;
        resizing = true;
    }
     
    /**
     * Move all targets according to the supplied key.
     */
    private void setKeyDelta(KeyEvent evt)
    {
        switch(evt.getKeyCode()) {
            case KeyEvent.VK_UP : {
                keyDeltaY -= GraphEditor.GRID_SIZE;
                break;
            }
            case KeyEvent.VK_DOWN : {
                keyDeltaY += GraphEditor.GRID_SIZE;
                break;
            }
            case KeyEvent.VK_LEFT : {
                keyDeltaX -= GraphEditor.GRID_SIZE;
                break;
            }
            case KeyEvent.VK_RIGHT : {
                keyDeltaX += GraphEditor.GRID_SIZE;
                break;
            }
        }
    }

    /**
     * Is the pressed key a plus or minus key?
     */
    private boolean isPlusOrMinusKey(KeyEvent evt)
    {
        return evt.getKeyChar() == '+' || evt.getKeyChar() == '-';
    }

    private void resizeWithFixedRatio(KeyEvent evt)
    {
        int delta = (evt.getKeyChar() == '+' ? GraphEditor.GRID_SIZE : -GraphEditor.GRID_SIZE);
        selection.resize(delta, delta);
        selection.moveStopped();
    }
    
    private void selectDependency(KeyEvent evt)
    {
        Vertex vertex = selection.getAnyVertex();
        if(vertex != null && vertex instanceof DependentTarget) {
            selection.selectOnly(vertex);
            List<Dependency> dependencies = ((DependentTarget) vertex).dependentsAsList();

            Dependency currentDependency = dependencies.get(currentDependencyIndex);
            if (currentDependency != null) {
                selection.remove(currentDependency);
            }
            currentDependencyIndex += (evt.getKeyCode() == KeyEvent.VK_PAGE_UP ? 1 : -1);
            currentDependencyIndex %= dependencies.size();
            if (currentDependencyIndex < 0) {//% is not a real modulo
                currentDependencyIndex = dependencies.size() - 1;
            }
            currentDependency = (Dependency) dependencies.get(currentDependencyIndex);
            if (currentDependency != null) {
                selection.add(currentDependency);
            }
        }
    }

    /**
     * Return the marquee of this conroller.
     */
    public Marquee getMarquee()
    {
        return marquee;
    }

    
    private Vertex findSingleVertex()
    {
        Vertex vertex = selection.getAnyVertex();

        // if there is no selection we select an existing vertex
        if (vertex == null) {
            vertex = (Vertex) graph.getVertices().next();
        }
        return vertex;
    }


    /**
     * Clear the current selection.
     */
    public void clearSelection()
    {
        selection.clear();
    }

    /** 
     * Select all graph vertices.
     */
    private void selectAll()
    {
        for(Iterator<? extends Vertex> i = graph.getVertices(); i.hasNext(); ) {
            selection.add(i.next());
        }
    }
    
    /**
     * Clear the current selection.
     */
    public void removeFromSelection(SelectableGraphElement element)
    {
        selection.remove(element);
    }

    /**
     * Add to the current selection
     * @param element
     */
    public void addToSelection(SelectableGraphElement element)
    {
        selection.add(element);
    }
   
    /**
     * Check whether this mouse event was from button one.
     * (Ctrl-button one on MacOS does not count - that posts the menu
     * so we consider that button two.)
     */
    protected boolean isButtonOne(MouseEvent evt)
    {
        return !evt.isPopupTrigger() && ((evt.getModifiers() & MouseEvent.BUTTON1_DOWN_MASK) != MouseEvent.BUTTON1_DOWN_MASK);
    }

    /**
     * Check whether the key used for multiple selections is down.
     */
    protected boolean isMultiselectionKeyDown(MouseEvent evt)
    {
//        if (Config.isMacOS()) {
//            return evt.isShiftDown() || evt.isMetaDown();
//        }
//        else {
            return evt.isShiftDown() || evt.isControlDown();
//        }

    }

    /**
     * Modify the given point to be one of the deined grid points.
     * 
     *   The original point
     * @return      A point close to the original which is on the grid.
     */
    protected int snapToGrid(int x)
    {
        int steps = x / GraphEditor.GRID_SIZE;
        int new_x = steps * GraphEditor.GRID_SIZE;//new x-coor w/ respect to
                                                  // grid
        return new_x;
    }

    /**
     * Return the rubber band of this graph.
     * @return  The rubber band instance, or null if no rubber band is currently in use.
     */
    public RubberBand getRubberBand()
    {
        return rubberBand;
    }

    @Override
    public void focusGained(FocusEvent e)
    {
        Iterator<? extends Vertex> it = graph.getVertices();
        while (it.hasNext())
        {
            Vertex v = it.next();
            if (v.getComponent() == e.getComponent())
            {
                selection.selectOnly(v);
            }
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
    }
    
    public void expand(ActionEvent e) {
        selection.expand(e);
    }
    
    public void colapse(ActionEvent e) {
        selection.colapse(e);
    }

    public UsesDependency getSelectedDependency() {
        return selectedDependency;
    }

    public Point getSelectedBendPoint() {
        return selectedBendPoint;
    }

    public void setSelectedBendPoint(BendPoint selectedBendPoint) {
        this.selectedBendPoint = selectedBendPoint;
        if (selectedBendPoint != null) {
            this.selectedBendPoint.setSelected(true);
        }
    }
}
