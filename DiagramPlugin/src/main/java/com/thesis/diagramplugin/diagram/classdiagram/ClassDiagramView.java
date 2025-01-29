package com.thesis.diagramplugin.diagram.classdiagram;

import com.thesis.diagramplugin.diagram.classdiagram.model.ClassDiagramModelPackage;
import com.thesis.diagramplugin.diagram.DiagramView;
import com.thesis.diagramplugin.plugin.editors.ClassDiagramEditor;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.Package;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.PkgMgrFrame;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.ClassTarget;

import javax.swing.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ClassDiagramView implements DiagramView {

    private final Package pkg;

    private PkgMgrFrame frame;

    private final ClassDiagramModelPackage model;

    public ClassDiagramView(ClassDiagramModelPackage model) throws IOException {
        this.model = model;

        ClassDiagramViewBuilder builder = new ClassDiagramViewBuilder(model);
        frame = builder.buildView();
        pkg = builder.getPkg();
    }

    @Override
    public JComponent getView() {
        return this.frame;
    }

    public void addListener(ClassDiagramEditor classDiagramEditor) {
        this.frame.addListener(classDiagramEditor);
    }

    public void removeListener(ClassDiagramEditor classDiagramEditor) {
        this.frame.removeListener(classDiagramEditor);
    }


    @Override
    public String getName() {
        return this.pkg.getBaseName();
    }

    public Set<ClassTarget> getObjectsToSave() {
        Set<ClassTarget> targets = new HashSet<>();
        targets.addAll(pkg.getPackages().keySet());
        targets.addAll(pkg.getModules().keySet());
        targets.addAll(pkg.getClassTargets());
        return targets;
    }
}
