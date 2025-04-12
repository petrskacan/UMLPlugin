package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency;

import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class BendPoint extends Point {

    private final Color unselected = JBColor.BLUE;
    private final Color selected = JBColor.red;
    private Color fill = unselected;
    private boolean isSelected = false;

    /**
     * Constructs and initializes a point at the origin
     * (0,&nbsp;0) of the coordinate space.
     *
     * @since 1.1
     */
    public BendPoint() {
    }

    /**
     * Constructs and initializes a point with the same location as
     * the specified {@code Point} object.
     *
     * @param p a point
     * @since 1.1
     */
    public BendPoint(@NotNull Point p) {
        super(p);
    }

    /**
     * Constructs and initializes a point at the specified
     * {@code (x,y)} location in the coordinate space.
     *
     * @param x the X coordinate of the newly constructed {@code Point}
     * @param y the Y coordinate of the newly constructed {@code Point}
     * @since 1.0
     */
    public BendPoint(int x, int y) {
        super(x, y);
    }



    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        if(isSelected)
        {
            fill = this.selected;
        }
        else
        {
            fill = this.unselected;
        }
    }

    public Color getFill() {
        return fill;
    }
}
