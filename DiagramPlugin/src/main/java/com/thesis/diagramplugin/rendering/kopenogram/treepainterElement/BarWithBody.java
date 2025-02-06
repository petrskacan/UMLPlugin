package com.thesis.diagramplugin.rendering.kopenogram.treepainterElement;


import com.thesis.diagramplugin.rendering.kopenogram.treepainter.AbstractPainterElement;
import com.thesis.diagramplugin.rendering.kopenogram.treepainter.OverPainterElement;
import com.thesis.diagramplugin.rendering.kopenogram.treepainter.PainterConfig;
import com.thesis.diagramplugin.rendering.kopenogram.treepainter.PainterElement;

import java.awt.*;
import java.util.List;

/**
 * One row for header, and rectangle for body
 */
public class BarWithBody extends AbstractPainterElement implements AvoidableContainer {

    private static final long serialVersionUID = -8619173144236718802L;
    protected final Color bodyColor;
    protected final Color lineColor;
    protected final Color noBorderLineColorRight;
    protected final Bar head;
    protected final VerticalContainer container;

    /**
     * @param text if null, then there is no head
     * @param headColor
     * @param bodyColor
     * @param lineColor
     * @param noBorderLineColorRight
     */
    public BarWithBody(String text, Color headColor, Color bodyColor, Color lineColor,
            Color noBorderLineColorRight) {
        head = new Bar(text, headColor);
        container = new VerticalContainer();
        this.bodyColor = bodyColor;
        this.lineColor = lineColor;
        this.noBorderLineColorRight = noBorderLineColorRight;
    }

    @Override
    public void setAvoidElements(List<? extends OverPainterElement> avoidElements) {
        container.setAvoidElements(avoidElements);
    }

    @Override
    protected Dimension computeDimension(PainterConfig config, Point pos) {
        Dimension headDim = new Dimension(0, 0);
        if (head.getText(config) != null) {
            headDim = head.getDimension(config, pos);
        }
        int height = headDim.height;
        if (!container.getChildren().isEmpty()) {
            Dimension cDim = container.getDimension(config, new Point(pos.x + config.leftGap, pos.y
                    + config.topGap + headDim.height));
            height += cDim.height + config.topGap;
            if (!(container.getChildren().get(container.getChildren().size() - 1) instanceof FootBar)) {
                height += config.bottomGap;
            }
        }
        return new Dimension(getWidth(config), height);
    }

    @Override
    public int getWidth(PainterConfig config) {
        int width = 0;
        if (head.getText(config) != null) {
            width = head.getWidth(config);
        }
        for (PainterElement element : container.getChildren()) {
            width = Math.max(width, config.leftGap + config.rightGap + element.getWidth(config));
        }
        return width;
    }

    public String getName() {
        return head.text;
    }

    @Override
    protected void paintGraphics(Graphics g, PainterConfig config, Point pos, Dimension dim) {
        int height = 0;
        if (head.getText(config) != null) {
            height = head.getDimension(config, pos).height;
            head.paint(g, config, pos, new Dimension(dim.width, height));
        }
        int y = pos.y + height;

        g.setColor(bodyColor);
        g.fillRect(pos.x, y, dim.width, dim.height - height);

        g.setColor(head.borderColor);
        g.drawRect(pos.x, y, dim.width, dim.height - height);

        g.setColor(lineColor);
        g.drawLine((pos.x + 1), pos.y, (pos.x + dim.width - 1), pos.y);

        g.setColor(noBorderLineColorRight);
        g.drawLine(pos.x, (y + 1), pos.x, ((y + dim.height - height) - 1));

        if (!container.getChildren().isEmpty()) {
            Point currPos = new Point(pos.x + config.leftGap, pos.y + height + config.topGap);
            Dimension dim2 = container.getDimension(config, currPos);
            dim2.width = dim.width - config.leftGap - config.rightGap;
            container.paint(g, config, currPos, dim2);
        }
    }

    @Override
    public Container addChild(PainterElement element) {
        container.addChild(element);
        return this;
    }

    @Override
    public List<PainterElement> getChildren() {
        return container.getChildren();
    }

    @Override
    public String toString() {
        if (head.text != null) {
            return "BarWithBody: " + head.text + ", " + container.getChildren().size();
        }
        return "Body: " + container.getChildren().size();
    }
}
