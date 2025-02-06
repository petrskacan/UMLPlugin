package com.thesis.diagramplugin.diagram.classdiagram.model;

import org.dom4j.Element;

public class ClassDiagramModelEnum extends AClassDiagramModelClassLikeEntity {

    public ClassDiagramModelEnum(Element element) {
        super(element);
        this.modelType = ClassDiagramModelType.ENUM;
    }

}
