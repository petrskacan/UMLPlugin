package com.thesis.diagramplugin.diagram.classdiagram.model;

import org.dom4j.Element;

import java.util.Collection;

public abstract class AClassDiagramModelClassLikeEntityCollection extends AClassDiagramModelClassLikeEntity {

    public AClassDiagramModelClassLikeEntityCollection(Element element) {
        super(element);
    }

    public abstract Collection<AClassDiagramModelClassLikeEntity> getContainingClassLikeEntities();

}
