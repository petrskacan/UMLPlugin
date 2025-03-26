package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.graphPainter;

import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.CompositionDependency;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.Dependency;

import java.awt.*;

public class CompositionDependencyPainter extends UsesDependencyPainter implements DependencyPainter {

    private static final float strokeWidth = 1.0f;
    private static final BasicStroke stroke = new BasicStroke(strokeWidth);
    private static final Color lineColor = Color.BLACK;

    public CompositionDependencyPainter() {
        super.usesDiamond = true;
    }

    @Override
    public void paint(Graphics2D g, Dependency dependency, boolean hasFocus) {
        if (!(dependency instanceof CompositionDependency)) {
            throw new IllegalArgumentException("Not a CompositionDependency");
        }

        CompositionDependency d = (CompositionDependency) dependency;

        Stroke oldStroke = g.getStroke();
        g.setStroke(stroke);
        g.setColor(lineColor);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int src_x = d.getSourceX();
        int src_y = d.getSourceY();
        int dst_x = d.getDestX();
        int dst_y = d.getDestY();

        g.drawLine(src_x, src_y, dst_x, dst_y);

        // Draw filled diamond at source
        drawDiamond(g, src_x, src_y, true, d.getStartConnectionSide());

        g.setStroke(oldStroke);
    }
}
