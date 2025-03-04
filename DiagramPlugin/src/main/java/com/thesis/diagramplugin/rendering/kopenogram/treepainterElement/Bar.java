package com.thesis.diagramplugin.rendering.kopenogram.treepainterElement;


import com.thesis.diagramplugin.rendering.kopenogram.treepainter.*;

import java.awt.*;

/**
 * one row with text
 */
public class Bar extends AbstractPainterElement {

    private static final long serialVersionUID = 7374892792492539066L;
    protected final String text;
    protected final Color color;
    protected final Color borderColor;
    protected final Color lineColor;
    protected final Color fontColor;
    protected final String path;

    public Bar(String text, Color color, String path) {
        this(text, color, Color.BLACK, Color.BLACK, path);
    }

    public Bar(String text, Color color, Color borderColor, Color lineColor, String path) {
        this(text, color, borderColor, lineColor, Color.BLACK, path);
    }
    public Bar(String text, Color color, Color borderColor, Color lineColor, Color fontColor, String path)
    {
        this.text = text;
        this.color = color;
        this.borderColor = borderColor;
        this.lineColor = lineColor;
        this.fontColor = fontColor;
        this.path = path;
    }
    public Bar()
    {
        this("",
                Settings.decodeColorProperty(Settings.Property.SWITCH_BODY_COLOR.getValue()),
                Settings.decodeColorProperty(Settings.Property.SWITCH_BODY_COLOR.getValue()),
                Settings.decodeColorProperty(Settings.Property.SWITCH_BODY_COLOR.getValue()),
                Settings.decodeColorProperty(Settings.Property.SWITCH_BODY_COLOR.getValue()),
                "");
    }


    @Override
    protected Dimension computeDimension(PainterConfig config, Point pos) {
        return new Dimension(getWidth(config), config.font.getSize() + config.fontVGapDown * 2);
    }

    @Override
    public int getWidth(PainterConfig config) {
        return PainterUtils.getTextWidth(getText(config), config.font) + config.fontHGap * 2;
    }

    /**
     * paint with set size
     */
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

    /**
     * paint text on set position with gap and return new postion for paint
     */
    protected int drawText(String text, Graphics g, PainterConfig config, Point pos) {
        g.setColor(this.fontColor);
        g.setFont(config.font);
        g.drawString(text, pos.x + config.fontHGap, pos.y + config.fontVGapUp + config.font.getSize());
        return pos.y + config.font.getSize() + config.fontVGapDown * 2;
    }

    public String getText(PainterConfig config) {
        if (text == null) {
            return null;
        }
        return text.substring(0, Math.min(text.length(), config.textMaxChars));
    }

    @Override
    public String toString() {
        return "Bar: " + text;
    }
    public String getPath()
    {
        return path;
    }
}
