package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.graphPainter;

import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.AggregationDependency;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.Dependency;

import java.awt.*;

public class AggregationDependencyPainter extends UsesDependencyPainter implements DependencyPainter {

    protected static final float strokeWidthDefault = 1.0f;
    protected static final float strokeWidthSelected = 2.0f;

    private static final Color normalColour = Color.BLACK;

    private static final BasicStroke normalSelected = new BasicStroke(strokeWidthSelected);
    private static final BasicStroke normalUnselected = new BasicStroke(strokeWidthDefault);

    public AggregationDependencyPainter() {
        super.usesDiamond = true;
    }

    @Override
    public void paint(Graphics2D g, Dependency dependency, boolean hasFocus) {
        if (!(dependency instanceof AggregationDependency aggregation)) {
            throw new IllegalArgumentException("Not a AggregationDependency");
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

        g.setColor(normalColour);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawDiamond(g, src_x, src_y, false, aggregation.getStartConnectionSide());
        paintLine(src_y, aggregation, g, src_x, dst_x, dst_y, oldStroke);

        drawArrow(g, dst_x, dst_y,aggregation.getEndConnectionSide());
    }
}
