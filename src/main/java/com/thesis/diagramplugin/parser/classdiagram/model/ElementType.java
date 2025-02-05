package com.thesis.diagramplugin.parser.classdiagram.model;


import com.sun.source.tree.Tree;

import static com.thesis.diagramplugin.utils.DiagramConstants.*;

public enum ElementType {
    CLASS (CLASS_TAG), ENUM (ENUM_TAG), RECORD (RECORD_TAG), INTERFACE (INTERFACE_TAG), ANNOTATION (ANNOTATION_TAG),
    MODULE(MODULE_TAG), METHOD (METHOD_TAG), VARIABLE (VARIABLE_TAG), UNKNOWN (UNKNOWN_TAG), PACKAGE (PACKAGE_TAG);

    private String type;
    ElementType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.type;
    }

    public static ElementType fromTreeKind(Tree.Kind treeKind) {
        switch (treeKind) {
            case INTERFACE : return INTERFACE;
            case ENUM : return ENUM;
            case CLASS : return CLASS;
            case RECORD : return RECORD;
            case ANNOTATION_TYPE : return ANNOTATION;
            case METHOD : return METHOD;
            case VARIABLE : return VARIABLE;
        }
        return UNKNOWN;
    }

}
