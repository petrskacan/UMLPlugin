package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.graphPainter;

import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.CompositionDependency;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.Dependency;

import java.awt.*;

public class CompositionDependencyPainter extends UsesDependencyPainter implements DependencyPainter {

    private static final float strokeWidth = 1.0f;
    private static final Color normalColour = Color.BLACK;

    private static final BasicStroke normalSelected = new BasicStroke(strokeWidthSelected);
    private static final BasicStroke normalUnselected = new BasicStroke(strokeWidthDefault);

    public CompositionDependencyPainter() {
        super.usesDiamond = true;
    }

    @Override
    public void paint(Graphics2D g, Dependency dependency, boolean hasFocus) {
        if (!(dependency instanceof CompositionDependency aggregation)) {
            throw new IllegalArgumentException("Not a CompositionDependency");
        }
        Stroke oldStroke = g.getStroke();
        Stroke normalStroke;
        boolean isSelected = aggregation.isSelected() && hasFocus;
        if (isSelected) {
            normalStroke = normalSelected;
        }
        else {
            normalStroke = normalUnselected;
        }
        g.setStroke(normalStroke);
        int src_x = aggregation.getSourceX();
        int src_y = aggregation.getSourceY();
        int dst_x = aggregation.getDestX();
        int dst_y = aggregation.getDestY();
        ;

        g.setColor(normalColour);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawDiamond(g, src_x, src_y, true, aggregation.getStartConnectionSide());
        paintLine(src_y, aggregation, g, src_x, dst_x, dst_y, oldStroke);
    }
}
