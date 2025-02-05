/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency;

/**
 *
 * @author vojta
 */
public enum Type {
        /**
         * The type of the dependency could not be determined. This usually
         * happens if the represented dependency does not exists anymore.
         */
        UNKNOWN,

        /** Represents a uses-dependency */
        USES,

        /** Represents an extends-dependency */
        EXTENDS,

        /** Represents an implements-dependency */
        IMPLEMENTS,
        
        /** Represents an containment-dependency */
        CONTAINMENT,
        
        /** Represents an association-dependency */ 
        ASSOCIATION;
}
