package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency;

import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.Package;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.DependentTarget;

import java.util.ArrayList;
import java.util.List;

public class ClassDiagramCustomDependency{
    private final DependencyType type;
    private static final List<ClassDiagramCustomDependency> customDependencies = new ArrayList<>();

    public ClassDiagramCustomDependency(Package pkg, DependentTarget from, DependentTarget to, DependencyType type) {
        this.type = type;
        customDependencies.add(this);

        pkg.addDependency(type.create(pkg, from, to), true);

    }

    public static List<ClassDiagramCustomDependency> getCustomDependencies()
    {
        return customDependencies;
    }
    public static void clearCustomDependencies() {
        customDependencies.clear();
    }


    public DependencyType getType() {
        return type;
    }
}
