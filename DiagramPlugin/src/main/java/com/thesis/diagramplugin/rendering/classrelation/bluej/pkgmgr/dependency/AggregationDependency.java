package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency;

import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.Package;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.DependentTarget;

import java.util.Properties;

public class AggregationDependency extends UsesDependency
{

    public AggregationDependency(Package pkg, DependentTarget from, DependentTarget to)
    {
        super(pkg, from, to);
    }

    public AggregationDependency(Package pkg)
    {
        this(pkg, null, null);
    }


    @Override
    public void save(Properties props, String prefix)
    {
        super.save(props, prefix);

        // This may be overridden by decendents
        props.put(prefix + ".type", "AggregationDependency");
    }

    @Override
    public Type getType()
    {
        return Type.AGGREGATION;
    }
}
