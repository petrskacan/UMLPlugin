package com.thesis.diagramplugin.rendering.kopenogram.treepainter;

import com.thesis.diagramplugin.rendering.kopenogram.PaintedNode;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Paints its root element and then over elements
 */
public class RootPainter implements PainterElement {

    private static final long serialVersionUID = 2801865657764404588L;
    private PainterElement root;
    private List<OverPainterElement> overElements;

    public RootPainter() {
        overElements = new ArrayList<OverPainterElement>();
    }

    public void setRoot(PainterElement root) {
        this.root = root;
    }

    public void addOverElement(OverPainterElement element) {
        overElements.add(element);
    }

    @Override
    public Dimension getDimension(PainterConfig config, Point pos) {
        return root.getDimension(config, pos);
    }

    @Override
    public int getWidth(PainterConfig config) {
        return root.getWidth(config);
    }

    @Override
    public Point getLastPosition() {
        return root.getLastPosition();
    }

    @Override
    public String getPath() {
        return "/";
    }

    @Override
    public void paint(Graphics g, PainterConfig config, Point pos, Dimension dim) {
        root.paint(g, config, pos, dim);
        for (OverPainterElement element : overElements) {
            element.getRealDimension(config);
            element.paintOver(g, config);
        }
    }

    public List<OverPainterElement> getOverElements() {
        return overElements;
    }

    @Override
    public String toString() {
        return "Root: " + root + "\n  Over elements: " + overElements;
    }
}
