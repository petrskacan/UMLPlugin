package com.thesis.diagramplugin.rendering.kopenogram.treepainter;

import java.awt.*;
import java.io.Serializable;

/**
 * structure for paramets of painter
 */
public class PainterConfig implements Serializable {

    private static final long serialVersionUID = -7782344157560527853L;
    public Font font = new Font("Arial", Font.PLAIN, 10);
    public Color fontColor = Color.BLACK;
    public int fontHGap = 2;
    public int fontVGapUp = 1;
    public int fontVGapDown = 3;
    public int leftGap = 10;
    public int rightGap = 10;
    public int topGap = 10;
    public int bottomGap = 10;
    public int verticalGap = 15;
    public int horizontalGap = 15;
    public int textMaxChars = 500;

    public PainterConfig() {
    }

    public PainterConfig(PainterConfig config) {
        this.font = config.font;
        this.fontColor = config.fontColor;
        this.fontHGap = config.fontHGap;
        this.fontVGapUp = config.fontVGapUp;
        this.fontVGapDown = config.fontVGapDown;
        this.leftGap = config.leftGap;
        this.rightGap = config.rightGap;
        this.topGap = config.topGap;
        this.bottomGap = config.bottomGap;
        this.verticalGap = config.verticalGap;
        this.horizontalGap = config.horizontalGap;
        this.textMaxChars = config.textMaxChars;
    }
}
