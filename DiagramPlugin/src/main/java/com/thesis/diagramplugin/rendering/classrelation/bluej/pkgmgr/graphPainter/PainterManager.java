package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.graphPainter;

import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.*;
import lombok.Getter;

import java.awt.*;
import java.util.Optional;

public class PainterManager {

    @Getter
    private static final PainterManager instance = new PainterManager();

    private static final ExtendsDependencyPainter extendsDependencyPainter = new ExtendsDependencyPainter();
    private static final ImplementsDependencyPainter implementsDependencyPainter = new ImplementsDependencyPainter();
    private static final UsesDependencyPainter usesDependencyPainter = new UsesDependencyPainter();
    private static final ContainmentDependencyPainter containmentDependencyPainter = new ContainmentDependencyPainter();
    private static final DependencyPainter associationDependencyPainter = new AssociationDependencyPainter();

    public static void paint(Graphics2D g, Dependency dependency, boolean hasFocus) {
        Optional.ofNullable(getPainter(dependency)).ifPresent(painter -> painter.paint(g, dependency, hasFocus));
    }

    public static DependencyPainter getPainter(Dependency dependency) {
        if (dependency instanceof ImplementsDependency) {
            return implementsDependencyPainter;
        }
        else if (dependency instanceof ExtendsDependency) {
            return extendsDependencyPainter;
        }
        else if (dependency instanceof ContainmentDependency) {
            return containmentDependencyPainter;
        }
        else if (dependency instanceof AssociationDependency) {
            return associationDependencyPainter;
        }
        else if (dependency instanceof UsesDependency) {
            return usesDependencyPainter;
        }
        else {
            return null;
        }
    }


    private PainterManager() {}

}
