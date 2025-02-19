package com.thesis.diagramplugin.rendering.kopenogram.treepainterElement;

import com.thesis.diagramplugin.rendering.kopenogram.treepainter.AbstractPainterElement;
import com.thesis.diagramplugin.rendering.kopenogram.treepainter.PainterConfig;
import com.thesis.diagramplugin.rendering.kopenogram.treepainter.PainterElement;

import java.awt.*;

/**
 * gap
 */
public class Separator extends AbstractPainterElement {

    private static final long serialVersionUID = 3974033871099803461L;
    /**
     * horizontal gap
     */
    public static final PainterElement HORIZONTAL = new Separator(-1, 0);
    /**
     * vertical gap
     */
    public static final PainterElement VERTICAL = new Separator(0, -1);
    /**
     * vertical and horizontal gap
     */
    public static final PainterElement BOTH = new Separator(-1, -1);
    private final int width;
    private final int height;

    /**
     * if tehre is 0 it takes value from config
     */
    public Separator(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    protected Dimension computeDimension(PainterConfig config, Point pos) {
        int h = height;
        if (h < 0) {
            h = config.verticalGap;
        }
        return new Dimension(getWidth(config), h);
    }

    @Override
    public int getWidth(PainterConfig config) {
        if (width < 0) {
            return config.horizontalGap;
        }
        return width;
    }

    @Override
    public String getPath() {
        return "";
    }

    @Override
    protected void paintGraphics(Graphics g, PainterConfig config, Point pos, Dimension dim) {
    }

    @Override
    public String toString() {
        return "Separator";
    }
}
