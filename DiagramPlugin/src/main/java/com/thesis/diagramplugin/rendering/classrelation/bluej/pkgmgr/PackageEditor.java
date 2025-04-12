/*
 This file is part of the BlueJ program.
 Copyright (C) 1999-2009,2012,2013,2014  Michael Kolling and John Rosenberg

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
package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr;

import com.thesis.diagramplugin.rendering.classrelation.bluej.graph.CustomDependencyDialog;
import com.thesis.diagramplugin.rendering.classrelation.bluej.graph.CustomDependencyMultiEditDialog;
import com.thesis.diagramplugin.rendering.classrelation.bluej.graph.GraphEditor;
import com.thesis.diagramplugin.rendering.classrelation.bluej.graph.Vertex;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.ClassTarget;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.PackageTarget;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Canvas to allow editing of packages
 *
 * @author  Andrew Patterson
 */
public final class PackageEditor extends GraphEditor
{
    private PackageEditorListener listener;

    private Set<ClassTarget> hidden = new HashSet<>();

    /**
     * Construct a package editor for the given package.
     */
    public PackageEditor(Package pkg, PackageEditorListener listener)
    {
        super(pkg);
        this.graphPainter = PackagePainter.getInstance();
        this.selectionController = new PackageSelectionController(this);
        this.listener = listener;
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
                if (v instanceof PackageTarget) {
                    add(((ClassTarget) v).getGhostComponent(), Integer.valueOf(2));
                    moveToBack(v.getComponent());
                } else if (v instanceof ClassTarget) {
                    add(((ClassTarget) v).getGhostComponent(), Integer.valueOf(2));
                    moveToFront(v.getComponent());
                }
                v.getComponent().addFocusListener(focusListener);
                v.getComponent().addFocusListener(selectionController);
                if (v.isSelected()) {
                    selectionController.addToSelection(v);
                }
            }
            // If it's in the vertices, keep it:
            keep.put(v.getComponent(), true);
            if (v instanceof ClassTarget ct) {
                keep.put(((ClassTarget) v).getGhostComponent(), true);
            }
        }


        // Remove what needs to be removed (i.e. what we didn't see in the vertices):
        for (Map.Entry<Component, Boolean> e : keep.entrySet()) {
            if (e.getValue().booleanValue() == false) {
                System.out.println("Remove: " + e.getKey().getName());
                remove(e.getKey());
                e.getKey().removeFocusListener(focusListener);
                e.getKey().removeFocusListener(selectionController);
            }
        }
        revalidate();
        repaint();
    }


    @Override
    public void setPermFocus(boolean focus)
    {
        boolean wasFocussed = hasPermFocus();
        super.setPermFocus(focus);
        if (focus && ! wasFocussed) {
            listener.pkgEditorGotFocus();
        }
        else if (! focus && wasFocussed) {
            listener.pkgEditorLostFocus();
        }
    }


    private class PopClickListener extends MouseAdapter implements KeyListener {

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
            JMenuItem menuHideItem = new JMenuItem("Hide selected items");
            menuHideItem.setFont(new Font("Arial", Font.PLAIN, 12));
            menuHideItem.addActionListener((e) -> {
                PackageEditor.this.hidden.addAll(selectionController.hideSelected(e));
                ((Package) PackageEditor.this.getGraph()).recalcArrows();
                PackageEditor.this.graphChanged();
            });
            JMenuItem menuShowHidden = new JMenuItem("Show  all hidden items");
            menuShowHidden.setFont(new Font("Arial", Font.PLAIN, 12));
            menuShowHidden.addActionListener((e) -> {
                for (ClassTarget ct : ((Package)PackageEditor.this.getGraph()).getHiddenItems()) {
                    ct.setVisible(true);
                }
                PackageEditor.this.hidden.clear();
                ((Package) PackageEditor.this.getGraph()).recalcArrows();
                PackageEditor.this.graphChanged();
            });
            JMenuItem addCustomDepItem = new JMenuItem("Add Custom Dependency...");
            addCustomDepItem.addActionListener(e -> {
                new CustomDependencyDialog((Package)PackageEditor.this.getGraph());
            });

            JMenuItem manageDepItem = new JMenuItem("Manage custom dependecies");
            manageDepItem.addActionListener(e -> {
                new CustomDependencyMultiEditDialog((Package)PackageEditor.this.getGraph());
            });

            if(selectionController.getSelectedBendPoint() != null)
            {
                JMenuItem deleteSelectedBendPoint = new JMenuItem("Delete Selected Bend Point");
                deleteSelectedBendPoint.setFont(new Font("Arial", Font.PLAIN, 12));
                deleteSelectedBendPoint.addActionListener((e) -> {
                    deleteBendPoint();
                });
                JMenuItem manageBendPoints = new JMenuItem("Manage Bend Points");
                manageBendPoints.setFont(new Font("Arial", Font.PLAIN, 12));
                manageBendPoints.addActionListener((e) -> {
                });

                menu.add(deleteSelectedBendPoint);
                menu.add(manageBendPoints);
                menu.addSeparator();
            }

            menu.add(addCustomDepItem);
            menu.add(manageDepItem);
            menu.addSeparator();
            menu.add(menuExpandItem);
            menu.add(menuColapseItem);
            menu.add(menuHideItem);
            menu.add(menuShowHidden);
            menu.add(createShowHiddenItemMenu());
            menu.addSeparator();
            menu.add(menuImageItem);
            return menu;
        }

        private JMenu createShowHiddenItemMenu() {
            //sub menu
            JMenu hiddenItemsMenu = new JMenu("Hidden Items... ");

            for (ClassTarget ct : ((Package)PackageEditor.this.getGraph()).getHiddenItems()) {
                JMenuItem itemMenu = new JMenuItem(ct.getDisplayName());
                itemMenu.addActionListener((e) -> {
                    PackageEditor.this.showHidden(ct);
                });
                hiddenItemsMenu.add(itemMenu);
            }

            return hiddenItemsMenu;
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

        /**
         * Invoked when a key has been typed.
         * See the class description for {@link KeyEvent} for a definition of
         * a key typed event.
         *
         * @param e the event to be processed
         */
        @Override
        public void keyTyped(KeyEvent e) {

        }

        /**
         * Invoked when a key has been pressed.
         * See the class description for {@link KeyEvent} for a definition of
         * a key pressed event.
         *
         * @param e the event to be processed
         */
        @Override
        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_DELETE && selectionController.getSelectedBendPoint() != null)
            {
                deleteBendPoint();
            }
        }

        /**
         * Invoked when a key has been released.
         * See the class description for {@link KeyEvent} for a definition of
         * a key released event.
         *
         * @param e the event to be processed
         */
        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    private void deleteBendPoint() {
        selectionController.getSelectedDependency().getBendPoints().remove(selectionController.getSelectedBendPoint());
        selectionController.setSelectedBendPoint(null);
        repaint();
    }

    private void showHidden(ClassTarget ct) {
        PackageEditor.this.hidden.remove(ct);
        ct.setVisible(true);
        if (ct instanceof PackageTarget pt) {
            for (ClassTarget child : ((Package)PackageEditor.this.getGraph()).getPackages().get(pt).stream().filter(c -> c instanceof ClassTarget).map(c -> (ClassTarget)c).toList()) {
                PackageEditor.this.hidden.remove(child);
                child.setVisible(true);
            }
        }
        PackageEditor.this.graphChanged();
        ((Package) PackageEditor.this.getGraph()).recalcArrows();
    }

    @Override
    public void startMouseListening() {
        addMouseMotionListener(this);
        addMouseMotionListener(selectionController);
        addMouseListener(selectionController);
        addMouseListener(new PackageEditor.PopClickListener());
    }
}
