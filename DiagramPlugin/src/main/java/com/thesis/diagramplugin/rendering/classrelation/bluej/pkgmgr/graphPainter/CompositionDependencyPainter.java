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
        if (!(dependency instanceof CompositionDependency composition)) {
            throw new IllegalArgumentException("Not a CompositionDependency");
        }
        Stroke oldStroke = g.getStroke();
        Stroke normalStroke;
        boolean isSelected = composition.isSelected() && hasFocus;
        if (isSelected) {
            normalStroke = normalSelected;
        }
        else {
            normalStroke = normalUnselected;
        }
        g.setStroke(normalStroke);
        int src_x = composition.getSourceX();
        int src_y = composition.getSourceY();
        int dst_x = composition.getDestX();
        int dst_y = composition.getDestY();

        g.setColor(normalColour);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawDiamond(g, src_x, src_y, true, composition.getStartConnectionSide());
        paintLine(src_y, composition, g, src_x, dst_x, dst_y, oldStroke);

        drawArrow(g, dst_x, dst_y,composition.getEndConnectionSide());
    }
}
