package com.thesis.diagramplugin.rendering.kopenogram.treepainter;

import com.thesis.diagramplugin.rendering.kopenogram.treepainterElement.Bar;
import com.thesis.diagramplugin.rendering.kopenogram.treepainterElement.BarWithBody;
import com.thesis.diagramplugin.rendering.kopenogram.treepainterElement.VerticalContainer;

import java.awt.*;

import static com.ibm.icu.impl.ValidIdentifiers.Datatype.x;

public abstract class AbstractPainterElement implements PainterElement {

    private static final long serialVersionUID = 2583468579390133385L;
    private transient Point lastPos;
    private transient Dimension lastDim;
    private transient PainterConfig lastConfig;
    private transient boolean cycled = false;

    @Override
    public Point getLastPosition() {
        return new Point(lastPos);
    }

    @Override
    public final Dimension getDimension(PainterConfig config, Point pos) {
        if (cycled) {
            throw new RuntimeException("Cycled");
        }
        if ((lastDim == null) || changed(config, pos)) {
            lastPos = new Point(pos);
            lastConfig = new PainterConfig(config);
            cycled = true;
            lastDim = computeDimension(config, pos);
            cycled = false;
        }
        return new Dimension(lastDim);
    }

    protected abstract Dimension computeDimension(PainterConfig config, Point pos);

    @Override
    public final void paint(Graphics g, PainterConfig config, Point pos, Dimension dim) {
        if (changed(config, pos)) {
            getDimension(config, pos);
        }
        lastPos = new Point(pos);
        lastConfig = new PainterConfig(config);
        RenderedElements.addElement(this.getPath(), this, pos, dim);
        paintGraphics(g, config, pos, dim);
    }

    protected abstract void paintGraphics(Graphics g, PainterConfig config, Point pos, Dimension dim);

    private boolean changed(PainterConfig config, Point pos) {
        return !config.equals(lastConfig) || !pos.equals(lastPos);
    }
}
