/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thesis.diagramplugin.rendering.kopenogram.treeprinter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * JPanel for painting kopenograms
 */
public class Surface extends JPanel {

    private final BufferedImage img;
    private double zoomFactor = 1.0;
    private double offsetX = 0, offsetY = 0;
    private int lastMouseX, lastMouseY;


    public Surface(BufferedImage img, Dimension dim) {
        this.img = img;
        this.setPreferredSize(dim);
        //zoom
        addMouseWheelListener(e -> {
            if (e.isControlDown()) {  // Check if CTRL is held
                double zoomChange = (e.getPreciseWheelRotation() < 0) ? 1.1 : 0.9;

                // Calculate new zoom center
                double mouseX = e.getX();
                double mouseY = e.getY();

                offsetX = (offsetX - mouseX) * zoomChange + mouseX;
                offsetY = (offsetY - mouseY) * zoomChange + mouseY;

                zoomFactor *= zoomChange;
                repaint();
            }
        });
        //panning
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMouseX = e.getX();
                lastMouseY = e.getY();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                double scaledSpeed = 1.0 / zoomFactor;

                offsetX += (e.getX() - lastMouseX) * scaledSpeed;
                offsetY += (e.getY() - lastMouseY) * scaledSpeed;

                lastMouseX = e.getX();
                lastMouseY = e.getY();

                repaint();
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        // Get panel size and image size
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();
        // Calculate center position
        int x = (panelWidth - imgWidth) / 2;
        int y = (panelHeight - imgHeight) / 2;


        // Clear previous frame to prevent ghosting
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, panelWidth, panelHeight);
        g2d.setComposite(AlphaComposite.SrcOver); // Reset composite
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Apply zoom transformation
        AffineTransform transform = new AffineTransform();
        transform.translate(panelWidth / 2.0, panelHeight / 2.0);  // Center zoom
        transform.scale(zoomFactor, zoomFactor);
        transform.translate(-panelWidth / 2.0, -panelHeight / 2.0);
        transform.translate(offsetX, offsetY); //panning

        g2d.setTransform(transform);

        //observer this added
        g.drawImage(img, x, y, this);
    }
}
