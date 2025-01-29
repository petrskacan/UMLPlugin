/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thesis.diagramplugin.rendering.kopenogram.treeprinter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * JPanel for painting kopenograms
 */
public class Surface extends JPanel {

    private final BufferedImage img;

    public Surface(BufferedImage img, Dimension dim) {
        this.img = img;
        this.setPreferredSize(dim);
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(img, 0, 0, null);
    }
}
