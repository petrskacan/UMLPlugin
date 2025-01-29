package com.thesis.diagramplugin.rendering.kopenogram.treepainterElement;

import com.thesis.diagramplugin.rendering.kopenogram.treepainter.AbstractPainterElement;
import com.thesis.diagramplugin.rendering.kopenogram.treepainter.PainterConfig;
import com.thesis.diagramplugin.rendering.kopenogram.treepainter.PainterElement;

import java.awt.*;

/**
 *
 */
public class Empty extends AbstractPainterElement {

    private static final long serialVersionUID = 4684627380429662912L;
    public static final PainterElement SPACE = new Empty();

    private Empty() {
    }

    @Override
    protected Dimension computeDimension(PainterConfig config, Point pos) {
        return new Dimension(getWidth(config), config.verticalGap);
    }

    @Override
    public int getWidth(PainterConfig config) {
        return config.horizontalGap;
    }

    @Override
    protected void paintGraphics(Graphics g, PainterConfig config, Point pos, Dimension dim) {
    }

    @Override
    public String toString() {
        return "Empty";
    }
}
