package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency;

import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.Package;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.DependentTarget;

import java.util.ArrayList;
import java.util.List;

public class CustomDependency {
    private final DependencyType type;
    private final DependentTarget from;
    private final DependentTarget to;

    private final Dependency dependency;
    private static final List<CustomDependency> customDependencies = new ArrayList<>();
    private static final List<CustomDependency> toRemoveDependecies = new ArrayList<>();

    public CustomDependency(Package pkg, DependentTarget from, DependentTarget to, DependencyType type) {
        this.type = type;
        this.from = from;
        this.to = to;
        customDependencies.add(this);
        dependency = type.create(pkg, from, to);
        pkg.addDependency(dependency, true);

    }

    public static List<CustomDependency> getCustomDependencies()
    {
        return customDependencies;
    }
    public static List<CustomDependency> getToRemoveDependecies()
    {
        return toRemoveDependecies;
    }
    public static void clearCustomDependencies() {
        customDependencies.clear();
        toRemoveDependecies.clear();
    }
    public static void removeCustomDependency(CustomDependency toRemove)
    {
        customDependencies.remove(toRemove);
        toRemoveDependecies.add(toRemove);
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
    public Dependency getDependency() {
        return dependency;
    }

    @Override
    public String toString() {
        return from.getDisplayName() + "->" + to.getDisplayName() + " as " + type;
    }
}
