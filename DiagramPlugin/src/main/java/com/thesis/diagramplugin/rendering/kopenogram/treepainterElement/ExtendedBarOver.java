package com.thesis.diagramplugin.rendering.kopenogram.treepainterElement;


import com.thesis.diagramplugin.rendering.kopenogram.PaintedNode;
import com.thesis.diagramplugin.rendering.kopenogram.treepainter.OverPainterElement;
import com.thesis.diagramplugin.rendering.kopenogram.treepainter.PainterConfig;
import com.thesis.diagramplugin.rendering.kopenogram.treepainter.PainterUtils;

import java.awt.*;

/**
 * enlarged bar painter over other elements
 */
public class ExtendedBarOver extends Bar implements OverPainterElement {

    private static final long serialVersionUID = 253532453772157209L;
    private final PaintedNode elem;
    private transient Dimension realDim;

    public ExtendedBarOver(String text, Color color, Color borderColor, Color lineColor, PaintedNode elem) {
        super(text, color, borderColor, lineColor);
        PainterUtils.validateNotNull(elem, "elem must not be null, use ExtendedBar instead");
        this.elem = elem;
    }

    @Override
    protected Dimension computeDimension(PainterConfig config, Point pos) {
        Dimension dim = super.computeDimension(config, pos);
        dim.width -= config.rightGap;
        return dim;
    }

    @Override
    protected void paintGraphics(Graphics g, PainterConfig config, Point pos, Dimension dim) {
        // nothing, will be painted later
    }

    @Override
    public Point getRealPosition() {
        return getLastPosition();
    }

    @Override
    public Dimension getRealDimension(PainterConfig config) {
        if (realDim == null) {
            realDim = computeRealDim(config);
        }
        return new Dimension(realDim);
    }

    private Dimension computeRealDim(PainterConfig config) {
        Dimension dim = getDimension(config, getRealPosition());
        Point elemPos = elem.getElement().getLastPosition();
        dim.width = elem.getElement().getWidth(config) + elemPos.x - getLastPosition().x;
        return dim;
    }

    @Override
    public void paintOver(Graphics g, PainterConfig config) {
        super.paintGraphics(g, config, getRealPosition(), getRealDimension(config));
    }

    @Override
    public String toString() {
        return "ExtendedBarOver: " + text + ", to: " + elem.getElement();
    }
}
