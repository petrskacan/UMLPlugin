package com.thesis.diagramplugin.diagram.classdiagram.model;

import org.dom4j.Element;

public class ClassDiagramModelRecord extends AClassDiagramModelClassLikeEntity {

    public ClassDiagramModelRecord(Element element) {
        super(element);
        this.modelType = ClassDiagramModelType.RECORD;
    }

}
