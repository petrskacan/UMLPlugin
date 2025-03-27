package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency;

import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.Package;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.DependentTarget;

import java.util.Properties;

public class CompositionDependency extends UsesDependency
{

    public CompositionDependency(Package pkg, DependentTarget from, DependentTarget to)
    {
        super(pkg, from, to);
    }

    public CompositionDependency(Package pkg)
    {
        this(pkg, null, null);
    }


    @Override
    public void save(Properties props, String prefix)
    {
        super.save(props, prefix);

        // This may be overridden by decendents
        props.put(prefix + ".type", "CompositionDependency");
    }

    @Override
    public DependencyType getType()
    {
        return DependencyType.COMPOSITION;
    }
}