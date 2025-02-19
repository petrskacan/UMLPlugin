package com.thesis.diagramplugin.rendering.kopenogram.treepainter;

import com.thesis.diagramplugin.rendering.kopenogram.PaintedNode;

import java.awt.*;

/**
 * Element painted over other elements. Method paint does not paint anything
 * (but must be called before painting over), paintOver paints the element.
 *
 */
public interface OverPainterElement extends PainterElement {

    /**
     * @return exact position where will be painted
     */
    public Point getRealPosition();

    /**
     * @return exact painted dimension
     */
    public Dimension getRealDimension(PainterConfig config);

    /**
     * Paints after everything was painted
     *
     * @param g
     * @param config
     */
    public void paintOver(Graphics g, PainterConfig config);
    void setElement(PainterElement element);
    String getPath();
}
