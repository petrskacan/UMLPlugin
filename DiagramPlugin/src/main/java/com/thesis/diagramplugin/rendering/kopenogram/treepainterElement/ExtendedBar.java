package com.thesis.diagramplugin.rendering.kopenogram.treepainterElement;


import com.thesis.diagramplugin.rendering.kopenogram.treepainter.PainterConfig;

import java.awt.*;

/**
 * enlarged bar for return and break
 */
public class ExtendedBar extends Bar {

    private static final long serialVersionUID = -1376414823350500886L;

    public ExtendedBar(String text, Color color, Color borderColor, Color lineColor) {
        super(text, color, borderColor, lineColor);
    }

    @Override
    protected Dimension computeDimension(PainterConfig config, Point pos) {
        Dimension dim = super.computeDimension(config, pos);
        dim.width -= config.rightGap;
        return dim;
    }

    @Override
    public int getWidth(PainterConfig config) {
        return super.getWidth(config) - config.rightGap;
    }

    @Override
    protected void paintGraphics(Graphics g, PainterConfig config, Point pos, Dimension dim) {
        dim.width += config.rightGap;
        super.paintGraphics(g, config, pos, dim);
    }

    @Override
    public String toString() {
        return "ExtendedBar: " + text;
    }
}
