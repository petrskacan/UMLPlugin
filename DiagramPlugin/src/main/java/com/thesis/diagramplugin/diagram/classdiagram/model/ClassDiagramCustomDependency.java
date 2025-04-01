package com.thesis.diagramplugin.diagram.classdiagram.model;

import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.DependencyType;
import org.dom4j.Element;

import static com.thesis.diagramplugin.utils.DiagramConstants.*;

public class ClassDiagramCustomDependency extends AClassDiagramModelClassLikeEntity{
    private final String from;
    private final String to;
    private final String type;
    public ClassDiagramCustomDependency(Element element) {
        super(element);
        this.modelType = ClassDiagramModelType.CUSTOMDEPENDECY;
        this.from = element.attributeValue(FROM);
        this.to = element.attributeValue(TO);
        this.type = element.attributeValue(DEPENDECY_TYPE);
        dependencyType = DependencyType.valueOf(type);
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getType() {
        return type;
    }
}
