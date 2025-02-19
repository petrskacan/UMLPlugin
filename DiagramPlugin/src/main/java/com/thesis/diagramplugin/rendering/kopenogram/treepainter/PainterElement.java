package com.thesis.diagramplugin.rendering.kopenogram.treepainter;

import java.awt.*;
import java.io.Serializable;

/**
 * inteface for painter elements
 */
public interface PainterElement extends Serializable {

    /**
     * return size of object for painter
     */
    public Dimension getDimension(PainterConfig config, Point pos);

    /**
     * @return width
     */
    public int getWidth(PainterConfig config);

    /**
     * paint element on psoition with set size.
     */
    public void paint(Graphics g, PainterConfig config, Point pos, Dimension dim);

    /**
     * @return last painted position
     */
    public Point getLastPosition();
    public String getPath();
}
