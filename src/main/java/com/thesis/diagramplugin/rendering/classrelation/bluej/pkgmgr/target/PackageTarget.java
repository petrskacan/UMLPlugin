package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target;

import com.thesis.diagramplugin.rendering.classrelation.bluej.graph.GraphEditor;
import com.thesis.diagramplugin.rendering.classrelation.bluej.graph.IExpandable;
import com.thesis.diagramplugin.rendering.classrelation.bluej.graph.Moveable;
import com.thesis.diagramplugin.rendering.classrelation.bluej.graph.Vertex;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.Package;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.graphPainter.ClassTargetPainter;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.graphPainter.PackageTargetPainter;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.role.*;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.Properties;

/**
 * A module in a package
 */
public class PackageTarget extends ClassTarget {

    static final int DEF_WIDTH = 80;
    static final int DEF_HEIGHT = 50;
    static final int ARR_HORIZ_DIST = 5;
    static final int ARR_VERT_DIST = 10;
    static final int HANDLE_SIZE = 20;
    static final int TEXT_HEIGHT = 16;
    static final int TEXT_BORDER = 8;
    static final int SHAD_SIZE = 4;

    @Getter
    protected int headerWidth;
    @Getter
    protected int headerHeight;
    @Getter
    protected int bodyWidth;
    @Getter
    protected int bodyHeight;
    protected int MIN_HEIGHT;
    protected int MIN_WIDTH;

    public PackageTarget(Package pkg, String identifierName) {
        super(pkg, identifierName);
        role = new PackageRole();
        role.setClassTarget(this);

        ghostComponent = new PackageTarget.GhostComponent();
        ghostComponent.setFocusable(false);
        ghostComponent.setBounds(getX(), getY(), getGhostHeight(), getGhostHeight());
        ghostComponent.setVisible(false);
        setGhostPosition(0, 0);
        setGhostSize(0, 0);

        component = new PackageTarget.PackageComponent();
        component.setFocusable(true);
        component.setBounds(getX(), getY(), getHeight(), getHeight());
        component.setVisible(true);

        headerWidth = (int) new Font("SansSerif-bold", Font.BOLD, 14).getStringBounds(this.getDisplayName(), FRC).getWidth() + 4*TEXT_BORDER;
        headerHeight = (int) new Font("SansSerif-bold", Font.BOLD, 14).getStringBounds(this.getDisplayName(), FRC).getHeight() + 2*TEXT_BORDER;

        MIN_WIDTH = headerWidth + 30;
        MIN_HEIGHT = headerHeight + 30;

        setSize(MIN_WIDTH, MIN_HEIGHT);
    }

    @Override
    public void setSize(int width, int height) {
        bodyHeight = Math.min(MIN_HEIGHT, height - headerHeight);
        bodyWidth = Math.min(MIN_WIDTH, width);
        super.setSize(width, height);
    }

    @Override
    public void setDisplayName(String name) {
        super.setDisplayName(name);
        this.resetHeaderWidth(name);
    }

    private void resetHeaderWidth(String name) {
        headerWidth = (int) new Font("SansSerif-bold", Font.BOLD, 14).getStringBounds(name, FRC).getWidth() + 4*TEXT_BORDER;
    }

    private class GhostComponent extends JComponent {

//        @Override
//        public void paintComponent(Graphics g) {
//            PackageTargetPainter.drawSkeleton((Graphics2D) g, PackageTarget.this, PackageTarget.this.getGhostWidth(), PackageTarget.this.getGhostHeight());
//            PackageTargetPainter.drawUMLStyle((Graphics2D) g, PackageTarget.this, PackageTarget.this.getGhostWidth(), PackageTarget.this.getGhostHeight(), true);
//            PackageTargetPainter.drawShadow((Graphics2D) g, PackageTarget.this.getGhostWidth(), PackageTarget.this.getGhostHeight());
//        }

    }

    @Override
    public void doubleClick(MouseEvent evt) {
    }

    private class PackageComponent extends VertexJComponent {

//        @Override
//        public void paintComponent(Graphics g) {
//            PackageTargetPainter.drawSkeleton((Graphics2D) g, PackageTarget.this, PackageTarget.this.getWidth(), PackageTarget.this.getHeight());
//            PackageTargetPainter.drawUMLStyle((Graphics2D) g, PackageTarget.this, PackageTarget.this.getWidth(), PackageTarget.this.getHeight(), false);
//            PackageTargetPainter.drawShadow((Graphics2D) g, PackageTarget.this.getWidth(), PackageTarget.this.getHeight());
//        }
    }

}
