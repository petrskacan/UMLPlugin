/*
 This file is part of the BlueJ program. 
 Copyright (C) 1999-2009,2013,2014  Michael Kolling and John Rosenberg 
 
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

import com.thesis.diagramplugin.rendering.classrelation.FileListener;
import com.thesis.diagramplugin.rendering.classrelation.FileSubject;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.Package;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.graphPainter.GraphPainterStdImpl;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.ClassTarget;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Component to allow editing of general graphs.
 *
 * @author Michael Cahill
 * @author Michael Kolling
 */
public class GraphEditor extends JLayeredPane
        implements MouseMotionListener, GraphListener, FileSubject {
//    protected final Color envOpColour = new Color(152,32,32); // Config.ENV_COLOUR;

    private final static Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
    private final static Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    private final static Cursor resizeCursor = new Cursor(Cursor.SE_RESIZE_CURSOR);

    /**
     * The grid resolution for graph layout.
     */
    public static final int GRID_SIZE = 10;

    protected Graph graph;
    protected GraphPainter graphPainter;
    private MarqueePainter marqueePainter;

    protected SelectionController selectionController;

    private Cursor currentCursor = defaultCursor;  // currently shown cursor

    protected FocusListener focusListener;

    /**
     * Create a graph editor.
     *
     * @param graph The graph being edited by this editor.
     */
    public GraphEditor(Graph graph) {
        this.graph = graph;
        marqueePainter = new MarqueePainter();
        graphPainter = GraphPainterStdImpl.getInstance();
        selectionController = new SelectionController(this);
        graph.addListener(this);
        setToolTipText(""); // Turn on tool-tips for this component

        // The focus listener will get focus events from the editor itself, and components within it.
        this.focusListener = new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (!hasPermFocus) {
                    setPermFocus(true);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (hasPermFocus && !e.isTemporary()) {
                    Component opposite = e.getOppositeComponent();
                    if (opposite == null || (opposite != GraphEditor.this && opposite.getParent() != GraphEditor.this)) {
                        setPermFocus(false);
                    }
                }
            }
        };

        addFocusListener(this.focusListener);

        // Get everything added:
        setLayout(null);
        setBackground(new Color(0, 0, 0, 0));
        graphChanged();
        
        listeners = new HashSet<>();
    }

    /**
     * Start our mouse listener. This is not done in the constructor, because we
     * want to give others (the PkgMgrFrame) the chance to listen first.
     */
    public void startMouseListening() {
        addMouseMotionListener(this);
        addMouseMotionListener(selectionController);
        addMouseListener(selectionController);
        addMouseListener(new PopClickListener());
    }

    /**
     * Tell how big we would like to be. The preferred size of the graph editor
     * the the size of the edited graph.
     */
    @Override
    public Dimension getPreferredSize() {
        return graph.getMinimumSize();
    }

    /**
     * Tell how big we would like to be. The minimum size of the graph editor
     * the the size of the edited graph.
     */
    @Override
    public Dimension getMinimumSize() {
        return graph.getMinimumSize();
    }

    /**
     * Paint this graph editor (this may be on screen or on a printer).
     */
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
        g2D.setPaint(new Color(247, 242, 223));

        graphPainter.paint(g2D, this);
        marqueePainter.paint(g2D, selectionController.getMarquee());
    }

    // ---- MouseMotionListener interface: ----
    /**
     * The mouse was dragged.
     */
    public void mouseDragged(MouseEvent evt) {
    }

    /**
     * The mouse was moved - check whether we should adjust the cursor.
     */
    public void mouseMoved(MouseEvent evt) {
        int x = evt.getX();
        int y = evt.getY();
        SelectableGraphElement element = graph.findGraphElement(x, y);
        Cursor newCursor = defaultCursor;
        if (element != null) {
            if (element.isResizable() && element.isHandle(x, y)) {
                newCursor = resizeCursor;
            } else {
                newCursor = handCursor;
            }
        }
        if (currentCursor != newCursor) {
            setCursor(newCursor);
            currentCursor = newCursor;
        }
    }

    // ---- end of MouseMotionListener interface ----
    /**
     * Process mouse events. This is a bug work-around: we prefer to handle the
     * mouse events in the mouse listener methods in the selection controller,
     * but on Windows the isPopupTrigger flag is not correctly set in the
     * mousePressed event. This method seems to be the only place to reliably
     * get it. So unfortunately, we need to process the popup trigger here.
     *
     * This method is called after the corresponding mousePressed method.
     */
    @Override
    protected void processMouseEvent(MouseEvent evt) {
        super.processMouseEvent(evt);
    }

    /**
     * Clear the set of selected classes. (Nothing will be selected after this.)
     */
    public void clearSelection() {
        selectionController.clearSelection();
    }

    /**
     * Clear the current selection.
     */
    public void removeFromSelection(SelectableGraphElement element) {
        selectionController.removeFromSelection(element);
    }

    /**
     * Add to the current selection
     *
     * @param element the element to add
     */
    public void addToSelection(SelectableGraphElement element) {
        selectionController.addToSelection(element);
    }

    /**
     * Return the rubber band information.
     */
    public RubberBand getRubberBand() {
        return selectionController.getRubberBand();
    }

    /**
     * Return the graph currently being edited.
     */
    public Graph getGraph() {
        return graph;
    }

    public void popupMenu(int x, int y) {
        // by default, do nothing
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        int x = event.getX();
        int y = event.getY();
        SelectableGraphElement element = graph.findGraphElement(x, y);

        if (element == null) {
            return null;
        } else {
            return element.getTooltipText();
        }
    }

    // ---- GraphListener interface ----
    public void selectableElementRemoved(SelectableGraphElement element) {
        removeFromSelection(element);
    }

    @Override
    public void graphChanged() {
        HashMap<Component, Boolean> keep = new HashMap<Component, Boolean>();

        // We assume all components currently in the graph belong to vertices.
        // We first mark all of them as no longer needed:
        for (Component c : getComponents()) {
            keep.put(c, false);
        }

        // Add what needs to be added:
        Iterator<? extends Vertex> it = graph.getVertices();
        while (it.hasNext()) {
            Vertex v = it.next();
            if (!keep.containsKey(v.getComponent())) {
                add(v.getComponent(), Integer.valueOf(0));
                moveToFront(v.getComponent());
                if (v instanceof ClassTarget) {
                    add(((ClassTarget) v).getGhostComponent(), Integer.valueOf(2));
                    if (((ClassTarget) v).isUnitTest() && !((ClassTarget) v).isExpanded()) {
                        moveToBack(v.getComponent());
                    }
                }
                v.getComponent().addFocusListener(focusListener);
                v.getComponent().addFocusListener(selectionController);
                if (v.isSelected()) {
                    selectionController.addToSelection(v);
                }
            }
            // If it's in the vertices, keep it:
            keep.put(v.getComponent(), true);
            keep.put(((ClassTarget) v).getGhostComponent(), true);
        }
        // Remove what needs to be removed (i.e. what we didn't see in the vertices):
        for (Entry<Component, Boolean> e : keep.entrySet()) {
            if (e.getValue().booleanValue() == false) {
                remove(e.getKey());
                e.getKey().removeFocusListener(focusListener);
                e.getKey().removeFocusListener(selectionController);
            }
        }
        revalidate();
        repaint();
    }

    /**
     * Notify the graph editor that the graph is closed, and it should free up
     * any resources associated with editing the graph.
     */
    public void graphClosed() {
        for (Component c : getComponents()) {
            c.removeFocusListener(focusListener);
            c.removeFocusListener(selectionController);
        }
    }

    private boolean hasPermFocus;

    /* whether we are focussed within window */
    /**
     * Set whether the editor has focus within its parent.
     *
     * @param focus
     */
    public void setPermFocus(boolean focus) {
        hasPermFocus = focus;
    }

    /**
     * Check whether the editor has focus within its parent.
     */
    public boolean hasPermFocus() {
        return hasPermFocus;
    }

    private class PopClickListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 1) {
                JPopupMenu menu = createPopupMenu(e.getComponent());
                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        private JPopupMenu createPopupMenu(Component component) {
            JPopupMenu menu = new JPopupMenu();
            JMenuItem menuImageItem = saveImageItem(component);
            JMenuItem menuExpandItem = new JMenuItem("Expand selected classes");
            menuExpandItem.setFont(new Font("Arial", Font.PLAIN, 12));
            menuExpandItem.addActionListener((e) -> {
                selectionController.expand(e);
            });
            JMenuItem menuColapseItem = new JMenuItem("Collapse selected classes");
            menuColapseItem.setFont(new Font("Arial", Font.PLAIN, 12));
            menuColapseItem.addActionListener((e) -> {
                selectionController.colapse(e);
            });

            menu.add(menuExpandItem);
            menu.add(menuColapseItem);
            menu.addSeparator();
            menu.add(menuImageItem);
            return menu;
        }

        protected JMenuItem saveImageItem(Component component) {
            JMenuItem menuItem = new JMenuItem("Save as image");
            menuItem.setFont(new Font("Arial", Font.PLAIN, 12));
            menuItem.addActionListener((e) -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save class diagram");
                if (graph instanceof Package) {
                    fileChooser.setCurrentDirectory(((Package) graph).getPath());
                }
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Images", "png"));
                fileChooser.setSelectedFile(new File("classDiagram.png"));

                int userSelection = fileChooser.showSaveDialog(component);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    try {
                        BufferedImage bi = new BufferedImage(component.getSize().width, component.getSize().height, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2 = bi.createGraphics();
                        paint(g2);
                        ImageIO.write(bi, "png", fileToSave);
                        notifyListeners(fileToSave.getAbsolutePath());
                    } catch (IOException ex) {
                        Logger.getLogger(GraphEditor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            return menuItem;
        }
    }
    
    //----------------FileSubject-------------------------
    private final HashSet<FileListener> listeners;

    @Override
    public void notifyListeners(String newFilePath) {
        listeners.forEach((listener) -> {
            listener.update(newFilePath);
        });
    }

    @Override
    public void addListener(FileListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(FileListener listener) {
        listeners.remove(listener);
    }
}
