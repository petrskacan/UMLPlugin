/*
 This file is part of the BlueJ program. 
 Copyright (C) 1999-2009,2010,2012,2013,2014  Michael Kolling and John Rosenberg 
 
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
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-12001, USA. 
 
 This file is subject to the Classpath exception as provided in the  
 LICENSE.txt file that accompanied this code.
 */
package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.graphPainter;

import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.ClassTarget;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.Target;
import com.thesis.diagramplugin.rendering.classrelation.bluej.Utility;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.Iterator;

/**
 * Paints a ClassTarget
 *
 * @author fisker
 */
public class ClassTargetPainter {

    /**
     * The constants in this enumeration are used to define which method of the
     * {@link ExtensionClassTargetPainter} shall be called.
     *
     * @author Simon Gerlach
     */
    public enum Layer {
        BACKGROUND, FOREGROUND;
    }

    private static final int HANDLE_SIZE = 20;
    private static final String STEREOTYPE_OPEN = "<<";
    private static final String STEREOTYPE_CLOSE = ">>";
    private static final Color textcolor = Color.BLACK;
    private static final Color borderColor = Color.BLACK;
    // private static final Image brokenImage = Config.getFixedImageAsIcon("broken-symbol.png").getImage();
    private static final Font targetFont = new Font("SansSerif-bold", Font.BOLD, 14);
    private static final Font textFont = new Font("SansSerif-bold", Font.PLAIN, 12);

    private static final int TEXT_HEIGHT = 16;
    private static final int TEXT_BORDER = 4;
    private static final int TEXT_MARGIN = 2;
    public static FontRenderContext FRC = new FontRenderContext(new AffineTransform(), false, false);
    private static final int HEADER_LINE_HEIGHT = (int) targetFont.getStringBounds("Test", FRC).getHeight() + TEXT_BORDER/2;
    private static final int TEXT_LINE_HEIGHT = (int) textFont.getStringBounds("Test", FRC).getHeight() + TEXT_BORDER/2;
    private static final AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 0.5);



    /**
     * Construct the ClassTargetPainter
     *
     */
    public ClassTargetPainter() {
    }

    public void paint(Graphics2D g, ClassTarget classTarget, boolean hasFocus) {
        g.translate(classTarget.getX(), classTarget.getY());
        ClassTargetPainter.drawShadow(g, classTarget.getWidth(), classTarget.getHeight());
        ClassTargetPainter.drawSkeleton(g, classTarget, classTarget.getWidth(), classTarget.getHeight());
        ClassTargetPainter.drawUMLStyle(g, classTarget, classTarget.getWidth(), classTarget.getHeight(), false);
        g.translate(-classTarget.getX(), -classTarget.getY());
    }

    public void paintGhost(Graphics2D g, ClassTarget classTarget, boolean hasFocus) {
        Composite oldComposite = g.getComposite();
        g.translate(classTarget.getGhostX(), classTarget.getGhostY());

        g.setComposite(alphaComposite);
        classTarget.getPackage().getEditor().moveToFront(classTarget.getGhostComponent());

        ClassTargetPainter.drawSkeleton(g, classTarget, classTarget.getGhostWidth(), classTarget.getGhostHeight());
        ClassTargetPainter.drawUMLStyle(g, classTarget, classTarget.getGhostWidth(), classTarget.getGhostHeight(), true);
        ClassTargetPainter.drawShadow(g, classTarget.getGhostWidth(), classTarget.getGhostHeight());

        g.setComposite(oldComposite);
        g.translate(-classTarget.getGhostX(), -classTarget.getGhostY());
    }

    /**
     * Draw the Coloured rectangle and the borders.
     *
     */
    public static void drawSkeleton(Graphics2D g, ClassTarget classTarget, int width, int height) {
        g.setPaint(classTarget.getBackgroundPaint(width, height));
        g.fillRect(0, 0, width, height);
    }

    /**
     * Draw the stereotype, identifier name and the line beneath the identifier
     * name.
     */
    public static void drawUMLStyle(Graphics2D g, ClassTarget classTarget, int width, int height, boolean isGhost) {
        // ExtensionsManager extensionsManager = ExtensionsManager.getInstance();

        // get the Stereotype
        String stereotype = classTarget.getRole().getStereotypeLabel();

        g.setColor(textcolor);
        int currentTextPosY = TEXT_MARGIN;

        // draw stereotype if applicable
        if (stereotype != null) {
            String stereotypeLabel = STEREOTYPE_OPEN + stereotype + STEREOTYPE_CLOSE;
            Font stereotypeFont = targetFont.deriveFont((float) (targetFont.getSize() - 2)).deriveFont(Font.PLAIN);
            g.setFont(stereotypeFont);
            Utility.drawCentredText(g, stereotypeLabel, TEXT_BORDER, currentTextPosY, width - 2
                    * TEXT_BORDER, HEADER_LINE_HEIGHT);
            currentTextPosY += HEADER_LINE_HEIGHT;
        }

        g.setFont(targetFont);

        // draw the identifiername of the class
        Utility.drawCentredText(g, classTarget.getDisplayName(), 0, currentTextPosY, width, HEADER_LINE_HEIGHT);
        currentTextPosY += HEADER_LINE_HEIGHT+TEXT_MARGIN*2;

        // draw line beneath the stereotype and indentifiername. The UML-style
        g.setColor(borderColor);
        g.drawLine(0, currentTextPosY, width-1, currentTextPosY);
        currentTextPosY += 2;
        
        classTarget.setHeaderHeight(currentTextPosY);

        // Ask extensions to draw their background of the class target
        int extensionGraphicsX = 1;
        int extensionGraphicsY = currentTextPosY + 1;
        int extensionGraphicsWidth = width - 1;
        int extensionGraphicsHeight = height - currentTextPosY - 1;

        // Last, draw the warnings if something is wrong with the class
        // drawWarnings(g, classTarget, width, height);
        g.setColor(borderColor);
        boolean drawSelected = classTarget.isSelected() && true;
        drawBorder(g, drawSelected, width, height);

        if (classTarget.isExpanded() && !isGhost) {
            g.setFont(textFont);
            Iterator<String> itFields = classTarget.getFields().iterator();
            while(itFields.hasNext()) {
                String a = itFields.next();
                Utility.drawLeftText(g, a, TEXT_BORDER, currentTextPosY, width
                    - 2 * TEXT_BORDER, TEXT_LINE_HEIGHT);
                currentTextPosY += TEXT_LINE_HEIGHT;
            };

            if(!classTarget.getFields().isEmpty()) {
                currentTextPosY += 4;
            } else {
                currentTextPosY += 10;
            }
            g.setColor(borderColor);
            g.drawLine(0, currentTextPosY, width-1, currentTextPosY);
            currentTextPosY += 2;
            
            Iterator<String> itMethods = classTarget.getMethods().iterator();
            while(itMethods.hasNext()) {
                String a = itMethods.next();
                Utility.drawLeftText(g, a, TEXT_BORDER, currentTextPosY, width
                    - 2 * TEXT_BORDER, TEXT_LINE_HEIGHT);
                currentTextPosY += TEXT_LINE_HEIGHT;
            };
            
            
        }
    }

    /**
     * Draw the borders of this target.
     */
    private static void drawBorder(Graphics2D g, boolean selected, int width, int height) {
        int thickness = 1; // default thickness

        if (selected) {
            thickness = 2; // thickness of borders when class is selected
            // Draw lines showing resize tag
            g.drawLine(width - HANDLE_SIZE - 2, height, width, height - HANDLE_SIZE - 2);
            g.drawLine(width - HANDLE_SIZE + 2, height, width, height - HANDLE_SIZE + 2);
        }
        Utility.drawThickRect(g, 0, 0, width, height, thickness);
    }

    /**
     * Draw a 'shadow' appearance under and to the right of the target.
     */
    public static void drawShadow(Graphics2D g, int width, int height)
    {
        // A uniform tail-off would have equal values for each,
        // as they all get drawn on top of each other:
        final int shadowAlphas[] = {20, 15, 10, 5, 5};
        for (int i = 0; i < 5; i++) {
            g.setColor(new Color(0, 0, 0, shadowAlphas[i]));
            g.fillRoundRect(2 - i, 4 - i, width + (2 * i) - 1, height + (2 * i) - 1, 8, 8);
        }
    }
}
