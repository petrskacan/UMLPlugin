/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency;

import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.Package;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.DependentTarget;

/**
 *
 * @author vojta
 */
public enum DependencyType {
        /** Represents a uses-dependency */
        USES{
                @Override
                public Dependency create(Package pkg, DependentTarget from, DependentTarget to) {
                        return new UsesDependency(pkg, from, to);
                }
        },

        /** Represents an extends-dependency */
        EXTENDS{
                @Override
                public Dependency create(Package pkg, DependentTarget from, DependentTarget to) {
                        return new ExtendsDependency(pkg, from, to);
                }
        },

        /** Represents an implements-dependency */
        IMPLEMENTS{
                @Override
                public Dependency create(Package pkg, DependentTarget from, DependentTarget to) {
                        return new ImplementsDependency(pkg, from, to);
                }
        },
        
        /** Represents an containment-dependency */
        CONTAINMENT{
                @Override
                public Dependency create(Package pkg, DependentTarget from, DependentTarget to) {
                        return new ContainmentDependency(pkg, from, to);
                }
        },
        
        /** Represents an association-dependency */ 
        ASSOCIATION{
                @Override
                public Dependency create(Package pkg, DependentTarget from, DependentTarget to) {
                        return new AssociationDependency(pkg, from, to);
                }
        },
        AGGREGATION{
                @Override
                public Dependency create(Package pkg, DependentTarget from, DependentTarget to) {
                        return new AggregationDependency(pkg, from, to);
                }
        },
        COMPOSITION{
                @Override
                public Dependency create(Package pkg, DependentTarget from, DependentTarget to) {
                        return new CompositionDependency(pkg, from, to);
                }
        };

        public abstract Dependency create(Package pkg, DependentTarget from, DependentTarget to);
}
