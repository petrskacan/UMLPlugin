package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.graphPainter;

import com.thesis.diagramplugin.rendering.classrelation.bluej.Utility;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.ClassTarget;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.PackageTarget;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.Target;

import java.awt.*;

public class PackageTargetPainter {

    public enum Layer {
        BACKGROUND, FOREGROUND;
    }

    private static final int HANDLE_SIZE = 20;
    private static final Color textcolor = Color.BLACK;
    private static final Color borderColor = Color.BLACK;
    private static final Font targetFont = new Font("SansSerif-bold", Font.BOLD, 14);

    private static final int TEXT_HEIGHT = 20;
    private static final int TEXT_BORDER = 4;
    private static final AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 0.5);

    public PackageTargetPainter() {
    }

    public void paint(Graphics2D g, PackageTarget packageTarget, boolean hasFocus) {
        g.translate(packageTarget.getX(), packageTarget.getY());
//        packageTarget.getComponent().paintComponents(g);
        drawShadow((Graphics2D) g, packageTarget.getWidth(), packageTarget.getHeight());
        drawSkeleton((Graphics2D) g, packageTarget, packageTarget.getWidth(), packageTarget.getHeight());
        drawUMLStyle((Graphics2D) g, packageTarget, packageTarget.getWidth(), packageTarget.getHeight(), false);
        g.translate(-packageTarget.getX(), -packageTarget.getY());
    }

    public void paintGhost(Graphics2D g, PackageTarget packageTarget, boolean hasFocus) {
        Composite oldComposite = g.getComposite();
        g.translate(packageTarget.getGhostX(), packageTarget.getGhostY());

        g.setComposite(alphaComposite);
        packageTarget.getPackage().getEditor().moveToFront(packageTarget.getGhostComponent());

        drawSkeleton(g, packageTarget, packageTarget.getGhostWidth(), packageTarget.getGhostHeight());
        drawUMLStyle(g, packageTarget, packageTarget.getGhostWidth(), packageTarget.getGhostHeight(), true);
        drawShadow(g, packageTarget.getGhostWidth(), packageTarget.getGhostHeight());

        g.setComposite(oldComposite);
        g.translate(-packageTarget.getGhostX(), -packageTarget.getGhostY());
    }

    /**
     * Draw the Coloured rectangle and the borders.
     *
     */
    public static void drawSkeleton(Graphics2D g, ClassTarget packageTarget, int width, int height) {
        g.setPaint(packageTarget.getBackgroundPaint(width, height));

        g.fillRect(0, 0, ((PackageTarget)packageTarget).getHeaderWidth(), ((PackageTarget)packageTarget).getHeaderHeight());
        g.fillRect(0, ((PackageTarget)packageTarget).getHeaderHeight(), width, height-((PackageTarget)packageTarget).getHeaderHeight());
    }

    /**
     * Draw the stereotype, identifier name and the line beneath the identifier
     * name.
     */
    public static void drawUMLStyle(Graphics2D g, PackageTarget packageTarget, int width, int height, boolean isGhost) {
        g.setColor(textcolor);
        g.setFont(targetFont);
        Utility.drawCentredText(g, packageTarget.getDisplayName(), 0,0, packageTarget.getHeaderWidth(), packageTarget.getHeaderHeight());
        g.setColor(borderColor);
        drawBorder(g, packageTarget.isSelected(), packageTarget, width, height);
    }

    /**
     * Draw the borders of this target.
     */
    private static void drawBorder(Graphics2D g, boolean selected, PackageTarget packageTarget, int width, int height) {
        int thickness = 1; // default thickness

        if (selected) {
            thickness = 2; // thickness of borders when class is selected
            // Draw lines showing resize tag
            g.drawLine(width - HANDLE_SIZE - 2, height, width, height - HANDLE_SIZE - 2);
            g.drawLine(width - HANDLE_SIZE + 2, height, width, height - HANDLE_SIZE + 2);
        }
        Utility.drawThickRect(g, 0, 0, packageTarget.getHeaderWidth(), packageTarget.getHeaderHeight()+thickness, thickness);
        Utility.drawThickRect(g, 0, packageTarget.getHeaderHeight(), width, height-packageTarget.getHeaderHeight(), thickness);
    }

    /**
     * Draw a 'shadow' appearance under and to the right of the target.
     */
    public static void drawShadow(Graphics2D g, int width, int height)
    {
//        // A uniform tail-off would have equal values for each,
//        // as they all get drawn on top of each other:
//        final int shadowAlphas[] = {20, 15, 10, 5, 5};
//        for (int i = 0; i < 5; i++) {
//            g.setColor(new Color(0, 0, 0, shadowAlphas[i]));
//            g.fillRoundRect(2 - i, 4 - i, width + (2 * i) - 1, height + (2 * i) - 1, 8, 8);
//        }
    }
}
