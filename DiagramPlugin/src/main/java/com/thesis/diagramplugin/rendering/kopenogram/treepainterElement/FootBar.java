package com.thesis.diagramplugin.rendering.kopenogram.treepainterElement;


import com.thesis.diagramplugin.rendering.kopenogram.treepainter.PainterConfig;

import java.awt.*;

/**
 * enlarged bar for return and break
 */
public class FootBar extends ExtendedBar {

    private static final long serialVersionUID = -7355408018838414473L;

    public FootBar(String text, Color color, Color borderColor, Color lineColor) {
        super(text, color, borderColor, lineColor, "");
    }

    @Override
    protected Dimension computeDimension(PainterConfig config, Point pos) {
        Dimension dim = super.computeDimension(config, pos);
        dim.width -= config.leftGap;
        return dim;
    }

    @Override
    protected void paintGraphics(Graphics g, PainterConfig config, Point pos, Dimension dim) {
        dim.width += (config.leftGap);
        pos.x -= config.leftGap;
        super.paintGraphics(g, config, pos, dim);
    }

    @Override
    public String toString() {
        return "FootBar: " + text;
    }
}
