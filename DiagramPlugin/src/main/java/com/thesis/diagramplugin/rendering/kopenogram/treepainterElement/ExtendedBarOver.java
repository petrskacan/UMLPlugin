package com.thesis.diagramplugin.rendering.kopenogram.treepainterElement;


import com.thesis.diagramplugin.rendering.kopenogram.treepainter.*;

import java.awt.*;

/**
 * enlarged bar painter over other elements
 */
public class ExtendedBarOver extends Bar implements OverPainterElement {

    private static final long serialVersionUID = 253532453772157209L;
    private transient Dimension realDim;
    private final String pathParent;

    public ExtendedBarOver(String text, Color color, Color borderColor, Color lineColor, Color fontColor, String parentPath, String elementPath) {
        super(text, color, borderColor, lineColor, fontColor,elementPath);
        this.pathParent = parentPath;
    }

    @Override
    protected Dimension computeDimension(PainterConfig config, Point pos) {
        Dimension dim = super.computeDimension(config, pos);
        dim.width -= config.rightGap;
        return dim;
    }

    @Override
    protected void paintGraphics(Graphics g, PainterConfig config, Point pos, Dimension dim) {
        g.setColor(color);
        g.fillRect(pos.x, pos.y, dim.width, dim.height);
        g.setColor(borderColor);
        g.drawRect(pos.x, pos.y, dim.width, dim.height);
        g.setColor(lineColor);
        g.drawLine(pos.x, pos.y, (pos.x + dim.width), pos.y);
        drawText(getText(config), g, config, pos);
    }

    @Override
    public Point getRealPosition() {
        return getLastPosition();
    }

    @Override
    public Dimension getRealDimension(PainterConfig config) {
        return computeRealDim(config);
    }

    public Dimension computeRealDim(PainterConfig config) {
        RenderedElementInfo elem = RenderedElements.getElement(pathParent);
        Dimension dim = getDimension(config, getRealPosition());
        Point elemPos = elem.element().getLastPosition();
        dim.width = elem.dimension().width - (getRealPosition().x - elemPos.x);
        return dim;
    }

    @Override
    public void paintOver(Graphics g, PainterConfig config) {
        super.paint(g, config, getRealPosition(), getRealDimension(config));
    }

    @Override
    public String toString() {
        return "ExtendedBarOver: " + text + ", to: ";
    }

    @Override
    public String getPath() {
        return path;
    }
    @Override
    public String getParentPath()
    {
        return pathParent;
    }
}
