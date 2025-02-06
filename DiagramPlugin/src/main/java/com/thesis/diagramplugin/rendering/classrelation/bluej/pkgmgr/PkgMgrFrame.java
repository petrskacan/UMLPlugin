/*
 This file is part of the BlueJ program.
 Copyright (C) 1999-2010,2011,2012,2013,2014  Michael Kolling and John Rosenberg

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

//~+ ANTOS
import com.thesis.diagramplugin.rendering.classrelation.FileListener;
import com.thesis.diagramplugin.rendering.classrelation.FileSubject;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.Properties;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * The main user interface frame which allows editing of packages
 */
public class PkgMgrFrame extends JPanel
        implements MouseListener, PackageEditorListener, FileSubject {

    /**
     * Placeholder used instead of "this" keyword as current instance name when
     * defining a new constructor.
     */
    public static final String _THIS = "_this";
    public static final String THIS = "this";

    // keys for properties
    public static final String EDITOR_WIDTH = "package.editor.width";
    public static final String EDITOR_HEIGHT = "package.editor.height";
    public static final String EDITOR_X = "package.editor.x";
    public static final String EDITOR_Y = "package.editor.y";
    public static final String OBJECTBENCH_WIDTH = "objectbench.width";
    public static final String OBJECTBENCH_HEIGHT = "objectbench.height";
    public static final String SHOW_USES = "package.showUses";
    public static final String SHOW_EXTENDS = "package.showExtends";
    public static final String PROJECT_CHARSET = "project.charset";

    public static String NULL_OBJECT_INFO;

//    private static Font pkgMgrFont = PrefMgr.getStandardFont();
    private static Font pkgMgrFont = new Font("SansSerif-bold", Font.PLAIN, 12);

    static final int DEFAULT_WIDTH = 560;
    static final int DEFAULT_HEIGHT = 400;


    /* The scroller which holds the PackageEditor we use to edit packages */
    private JScrollPane classScroller = null;

    /*
     * The package that this frame is working on or null for the case where
     * there is no package currently being edited (check with isEmptyFrame())
     */
    private Package pkg = null;

    /*
     * The graph editor which works on the package or null for the case where
     * there is no package current being edited (isEmptyFrame() == true)
     */
    private PackageEditor editor = null;

    //-Marek Chadim
    /**
     * Open a PkgMgrFrame with no package. Packages can be installed into this
     * frame using the methods openPackage/closePackage.
     */
    private static PkgMgrFrame createFrame() {
        PkgMgrFrame frame = new PkgMgrFrame();
        return frame;
    }

    /**
     * Open a PkgMgrFrame with a package. This may create a new frame or return
     * an existing frame if this package is already being edited by a frame. If
     * an empty frame exists, that frame will be used to show the package.
     */
    public static PkgMgrFrame createFrame(Package pkg) {
        PkgMgrFrame pmf;

        pmf = createFrame();
        pmf.openPackage(pkg);
        return pmf;
    }

    /**
     * Displays the package in the frame for editing
     */
    public void openPackage(Package pkg) {
        if (pkg == null) {
            throw new NullPointerException();
        }

        this.pkg = pkg;

        this.editor = new PackageEditor(pkg, this);
        editor.getAccessibleContext().setAccessibleName("pkgmgr.graphEditor.title");
        editor.setFocusable(true);
        editor.addMouseListener(this);  // This mouse listener MUST be before
        editor.startMouseListening();   //  the editor's listener itself!
        pkg.setEditor(this.editor);

        classScroller.setViewportView(editor);
        Properties p = pkg.getLastSavedProperties();

        String width_str = p.getProperty(EDITOR_WIDTH, Integer.toString(
                DEFAULT_WIDTH));
        String height_str = p.getProperty(EDITOR_HEIGHT, Integer
                .toString(DEFAULT_HEIGHT));

        classScroller.setPreferredSize(new Dimension(Integer.parseInt(
                width_str), Integer.parseInt(height_str)));
        String x_str = p.getProperty(EDITOR_X, "30");
        String y_str = p.getProperty(EDITOR_Y, "30");

        int x = Integer.parseInt(x_str);
        int y = Integer.parseInt(y_str);

        setLocation(x, y);

        String uses_str = p.getProperty(SHOW_USES, "true");
        String extends_str = p.getProperty(SHOW_EXTENDS, "true");
        updateShowUsesInPackage();
        updateShowExtendsInPackage();
        editor.revalidate();
        editor.requestFocus();

        updateWindowTitle();
        setVisible(true);
    }

    /**
     * Set the window title to show the current package name.
     */
    private void updateWindowTitle() {
        String title = "Class diagram";
        
        title = title + "  [" + getPackage().getBaseName() + "]";
//        setTitle(title);
    }
    
     /**
     * Return the package shown by this frame.
     * 
     * This call should be bracketed by a call to isEmptyFrame() before use.
     */
    public Package getPackage()
    {
        return pkg;
    }

    /**
     * Toggle the state of the "show uses arrows" switch.
     */
    public void updateShowUsesInPackage() {
        pkg.setShowUses(true);
        editor.repaint();
    }

    public void updateShowExtendsInPackage() {
        pkg.setShowExtends(true);
        editor.repaint();
    }

    @Override
    public void mousePressed(MouseEvent evt) {
    }

    @Override
    public void mouseReleased(MouseEvent evt) {
    }

    @Override
    public void mouseClicked(MouseEvent evt) {
    }

    @Override
    public void mouseEntered(MouseEvent evt) {
    }

    @Override
    public void mouseExited(MouseEvent evt) {
    }

    @Override
    public void pkgEditorGotFocus() {
    }

    @Override
    public void pkgEditorLostFocus() {
    }

    private PkgMgrFrame() {
        super(new BorderLayout(5, 5));
//        this.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        makeFrame();
        listeners = new HashSet<>();
    }

    private void makeFrame() {
        setFont(pkgMgrFont);

        // create the main panel holding the diagram and toolbar on the left
        setBackground(Color.lightGray);
        setOpaque(true);

        classScroller = new JScrollPane();
        classScroller.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        classScroller.setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        classScroller.setFocusable(false);
        classScroller.getVerticalScrollBar().setUnitIncrement(10);
        classScroller.getHorizontalScrollBar().setUnitIncrement(20);
        classScroller.setOpaque(false);
        classScroller.getViewport().setOpaque(false);
        add(classScroller, BorderLayout.CENTER);
    }

    /**
     * Save this package. Don't ask questions - just do it.
     */
    public void doSave() {
        Properties p = new Properties();
        Dimension d = classScroller.getSize();

        p.put(EDITOR_WIDTH, Integer.toString(d.width));
        p.put(EDITOR_HEIGHT, Integer.toString(d.height));

        Point point = getLocation();

        p.put(EDITOR_X, Integer.toString(point.x));
        p.put(EDITOR_Y, Integer.toString(point.y));

        p.put(OBJECTBENCH_WIDTH, Integer.toString(d.width));
        p.put(OBJECTBENCH_HEIGHT, Integer.toString(d.height));

        p.put(SHOW_USES, Boolean.toString(true));
        p.put(SHOW_EXTENDS, Boolean.toString(true));

        p.put(PROJECT_CHARSET, "UTF-8");
        pkg.save(p);
        notifyListeners(pkg.getPath().getAbsolutePath());
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
        editor.addListener(listener);
    }

    @Override
    public void removeListener(FileListener listener) {
        listeners.remove(listener);
        editor.removeListener(listener);
    }
}
