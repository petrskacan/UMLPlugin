package com.thesis.diagramplugin.diagram.classdiagram.model;

import org.dom4j.Element;

public class ClassDiagramModelInterface extends AClassDiagramModelClassLikeEntity {

    public ClassDiagramModelInterface(Element element) {
        super(element);
        this.modelType = ClassDiagramModelType.INTERFACE;
    }


}
