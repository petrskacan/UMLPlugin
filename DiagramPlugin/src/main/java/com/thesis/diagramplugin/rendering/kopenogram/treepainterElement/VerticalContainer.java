package com.thesis.diagramplugin.rendering.kopenogram.treepainterElement;


import com.thesis.diagramplugin.rendering.kopenogram.treepainter.OverPainterElement;
import com.thesis.diagramplugin.rendering.kopenogram.treepainter.PainterConfig;
import com.thesis.diagramplugin.rendering.kopenogram.treepainter.PainterElement;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Container takes few object and erase gaps
 */
public class VerticalContainer extends AbstractContainer implements AvoidableContainer {

    private static final long serialVersionUID = 4201701983361439694L;
    private List<? extends OverPainterElement> avoidElements;
    private List<Point> cachedPositions;
    private final String path;

    @Override
    public void setAvoidElements(List<? extends OverPainterElement> avoidElements) {
        this.avoidElements = avoidElements;
    }
    public VerticalContainer(String path)
    {
        this.path = path;
    }

    public VerticalContainer()
    {
        this("");
    }
    @Override
    protected Dimension computeDimension(final PainterConfig config, Point pos) {
        Point currPos = new Point(pos);
        if (this.avoidElements != null) {
            this.avoidElements.sort(Comparator.comparingInt((OverPainterElement o) -> (o.getRealPosition().y + o.getRealDimension(config).height)));
        }
        cachedPositions = new ArrayList<Point>(getChildren().size());
        children:
        for (PainterElement element : getChildren()) {
            Dimension dim2 = element.getDimension(config, currPos);
            for (OverPainterElement avoid : getToAvoid()) {
                if ((element != avoid) && intersects(currPos, dim2, avoid, config)) {
                    currPos.y = avoid.getRealPosition().y + avoid.getRealDimension(config).height
                            + config.verticalGap;
                    if (element instanceof Separator) {
                        cachedPositions.add(null);
                        continue children;
                    }
                    dim2 = element.getDimension(config, currPos);
                }
            }
            cachedPositions.add(new Point(currPos));
            currPos.y += dim2.height;
        }
        int height = currPos.y - pos.y;
        return new Dimension(getWidth(config), height);
    }

    @Override
    public int getWidth(PainterConfig config) {
        int width = 0;
        for (PainterElement element : getChildren()) {
            width = Math.max(width, element.getWidth(config));
        }
        return width;
    }

    @Override
    protected void paintGraphics(Graphics g, PainterConfig config, Point pos, Dimension dim) {
        int i = 0;
        for (PainterElement element : getChildren()) {
            Point currPos = cachedPositions.get(i);
            if (currPos != null) {
                Dimension dim2 = element.getDimension(config, currPos);
                dim2.width = dim.width;
                element.paint(g, config, currPos, dim2);

            }
            i++;
        }
    }

    private boolean intersects(Point pos, Dimension dim, OverPainterElement element, PainterConfig config) {
        Point pos2 = element.getRealPosition();
        Dimension dim2 = element.getRealDimension(config);
        return (pos.x - config.leftGap < pos2.x + dim2.width)
                && (pos.x + dim.width + config.rightGap > pos2.x)
                && (pos.y - config.verticalGap < pos2.y + dim2.height)
                && (pos.y + dim.height + config.verticalGap > pos2.y);
    }

    private List<? extends OverPainterElement> getToAvoid() {
        if (avoidElements == null) {
            return Collections.emptyList();
        }
        return avoidElements;
    }

    @Override
    public String toString() {
        return "Vertical: " + getChildren().size();
    }
    public String getPath()
    {
        return path;
    }
}
