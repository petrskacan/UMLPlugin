package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency;

import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.Package;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.DependentTarget;

import java.util.ArrayList;
import java.util.List;

public class CustomDependency {
    private final DependencyType type;
    private final DependentTarget from;
    private final DependentTarget to;
    private static final List<CustomDependency> customDependencies = new ArrayList<>();

    public CustomDependency(Package pkg, DependentTarget from, DependentTarget to, DependencyType type) {
        this.type = type;
        this.from = from;
        this.to = to;
        customDependencies.add(this);

        pkg.addDependency(type.create(pkg, from, to), true);

    }

    public static List<CustomDependency> getCustomDependencies()
    {
        return customDependencies;
    }
    public static void clearCustomDependencies() {
        customDependencies.clear();
    }


    public DependencyType getType() {
        return type;
    }

    public DependentTarget getFrom() {
        return from;
    }

    public DependentTarget getTo() {
        return to;
    }

    @Override
    public String toString() {
        return from.getDisplayName() + "->" + to.getDisplayName() + " as " + type;
    }
}
