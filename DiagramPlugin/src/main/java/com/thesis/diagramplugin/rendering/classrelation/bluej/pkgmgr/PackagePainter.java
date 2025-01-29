package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr;

import com.thesis.diagramplugin.rendering.classrelation.bluej.graph.*;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.*;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.graphPainter.*;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.ClassTarget;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.DependentTarget;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.PackageTarget;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.ProtoContainer;
import lombok.Getter;

import java.awt.*;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

public class PackagePainter implements GraphPainter {

    static final int TEXT_HEIGHT = 12;
    static final int TEXT_BORDER = 4;
    static final float alpha = (float) 0.5;
    static AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
    private final ClassTargetPainter classTargetPainter = new ClassTargetPainter();
    private final PackageTargetPainter packageTargetPainter = new PackageTargetPainter();

    private final PainterManager painter = PainterManager.getInstance();

    // Singleton
    @Getter
    private static final PackagePainter instance = new PackagePainter();

    private GraphEditor editor;

    private PackagePainter() {
        // singleton
    }


    /**
     * Paint 'graph' on 'g'
     */
    public void paint(Graphics2D g, GraphEditor graphEditor) {
        this.editor = graphEditor;
        Package pkg = null;
        if (graphEditor instanceof PackageEditor packageEditor && packageEditor.getGraph() instanceof Package) {
            pkg = (Package) packageEditor.getGraph();
        }
        if (pkg.getPackages() == null || pkg.getPackages().isEmpty()) {
            return;
        }

        // first paint packages
        for (PackageTarget packageTarget : pkg.getPackages().keySet()) {
            if (packageTarget.isVisible()) {
                paintPackage(g, packageTarget);
                if (packageTarget.isMoveable() && packageTarget.isDragging()) {
                    paintGhostVertex(g, packageTarget);
                }
            }
        }

        paintEdges(g, pkg);

        for (Map.Entry<PackageTarget, List<DependentTarget>> entry : pkg.getPackages().entrySet()) {
            PackageTarget pkgTarget = entry.getKey();
            List<DependentTarget> pkgContents = entry.getValue();

            for (DependentTarget content : pkgContents) {
                if (content.isVisible()) {
                    paintVertex(g, content);
                    if (content instanceof Moveable movableContent && movableContent.isDragging()) {
                        paintGhostVertex(g, movableContent);
                    }
                }
            }
        }
    }

    private void paintPackage(Graphics2D g, PackageTarget packageTarget)
    {
        packageTargetPainter.paint(g, packageTarget, isPermanentFocusOwner());
        List<DependentTarget> contents = packageTarget.getPackage().getPackages().get(packageTarget);
        if (contents != null) {
            contents.stream().filter(content -> content instanceof PackageTarget).forEach(pkgTarget -> paintPackage(g, (PackageTarget) pkgTarget));
        }
    }

    private void paintVertex(Graphics2D g, Vertex vertex)
    {
        if (vertex instanceof PackageTarget) {
//            System.out.println("PAINT VERTEX AS PACKAGE!!!");
            packageTargetPainter.paint(g, (PackageTarget) vertex, isPermanentFocusOwner());
        } else if (vertex instanceof ClassTarget) {
            classTargetPainter.paint(g, (ClassTarget) vertex, isPermanentFocusOwner());
        }
    }


    /**
     * Paint the edges in 'graph' on 'g'. Edges that are declared as
     * "not visible" will not be painted.
     *
     * @param g
     * @param graph
     */
    private void paintEdges(Graphics2D g, Graph graph)
    {
        Edge edge;
        //Paint the edges
        for (Iterator<? extends Edge> it = graph.getEdges(); it.hasNext();) {
            edge = it.next();
            if (edge.isVisible()) {
                paintEdge(g, edge);
            }
        }
    }

    /**
     * Paint the vertices in 'graph' on 'g'. If one of the targets to be painted
     * is in the process of drawing a dependency to another class, assign that
     * class to 'dependency'. Vertices that are declared as "not visible" will
     * not be painted.
     *
     * @param g
     * @param graph
     * @param dependentTarget
     * @return the class from which a dependency is being drawn. Null if none.
     */
    private void paintVertices(Graphics2D g, Graph graph)
    {
        for (Iterator<? extends Vertex> it = graph.getVertices(); it.hasNext();) {
            Vertex vertex = it.next();
            if (vertex.isVisible()) {
                paintVertex(g, vertex);
            }
        }
    }

    /**
     * Paint the ghosts (transparent versions) of the vertices in 'graph' that
     * are being dragged in the diagram.
     *
     * @param g
     * @param graph
     */
    private void paintGhosts(Graphics2D g, Graph graph)
    {
        for (Iterator<? extends Vertex> it = graph.getVertices(); it.hasNext();) {
            Vertex vertex = it.next();
            if (vertex instanceof Moveable) {
                Moveable moveable = (Moveable) vertex;
                if (moveable.isDragging()) {
                    paintGhostVertex(g, moveable);
                }
            }
        }
    }

    /**
     * Paint 'edge' on 'g'
     */
    private void paintEdge(Graphics2D g, Edge edge)
    {
        if (!(edge instanceof Dependency dependency)) {
            throw new IllegalArgumentException("Not a dependency");
        }
        PainterManager.paint(g, dependency, isPermanentFocusOwner());
    }


    /**
     * Paint a ghostet (transparent) version of 'vertex' on 'g'
     *
     * @param g
     * @param vertex
     */
    private void paintGhostVertex(Graphics2D g, Moveable vertex)
    {
        if (vertex instanceof PackageTarget) {
            packageTargetPainter.paintGhost(g, (PackageTarget) vertex, isPermanentFocusOwner());
        } else if (vertex instanceof ClassTarget) {
            classTargetPainter.paintGhost(g, (ClassTarget) vertex, isPermanentFocusOwner());
        }
//        else if (vertex instanceof PackageTarget) {
//            packageTargetPainter.paintGhost(g, (PackageTarget) vertex, isPermanentFocusOwner());
//        }
//        else {
//            //asserts false;
//        }
    }

    /**
     * Tell whether the graph editor has the permanent key focus -
     * this is NOT the temporary which hasFocus() and isFocusOwner() uses.
     */
    private boolean isPermanentFocusOwner()
    {
        return editor.hasPermFocus();
    }

}
