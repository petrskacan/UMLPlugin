package com.thesis.diagramplugin.rendering.kopenogram.treepainterElement;


import com.thesis.diagramplugin.rendering.kopenogram.treepainter.PainterConfig;
import com.thesis.diagramplugin.rendering.kopenogram.treepainter.PainterElement;

import java.awt.*;

/**
 * Container takes few objects and erase gaps
 */
public class HorizontalContainer extends AbstractContainer {

    private static final long serialVersionUID = 8811281749516422579L;

    @Override
    protected Dimension computeDimension(PainterConfig config, Point pos) {
        int height = 0;
        int x = pos.x;
        for (PainterElement element : getChildren()) {
            Dimension dim2 = element.getDimension(config, new Point(x, pos.y));
            height = Math.max(height, dim2.height);
            x += dim2.width;
        }
        return new Dimension(getWidth(config), height);
    }

    @Override
    public int getWidth(PainterConfig config) {
        int width = 0;
        for (PainterElement element : getChildren()) {
            width += element.getWidth(config);
        }
        return width;
    }

    @Override
    protected void paintGraphics(Graphics g, PainterConfig config, Point pos, Dimension dim) {
        int x = pos.x;
        int y = pos.y;
        for (PainterElement element : getChildren()) {
            Dimension dim2 = element.getDimension(config, pos);
            dim2.height = dim.height;
            element.paint(g, config, new Point(x, y), dim2);
            x += dim2.width;
        }
    }

    @Override
    public String toString() {
        return "Horizontal: " + getChildren().size();
    }
}
