package com.thesis.diagramplugin.diagram.classdiagram.model;

import lombok.Getter;
import org.dom4j.Element;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.thesis.diagramplugin.utils.DiagramConstants.FIELD_TYPE_ATTRIBUTE;
import static com.thesis.diagramplugin.utils.DiagramConstants.UNKNOWN_TAG;

@Getter
public class ClassDiagramModelField extends AClassDiagramModelElement {

    private String type;

    public ClassDiagramModelField(Element element) {
        super(element);
        type = Optional.ofNullable(element.attributeValue(FIELD_TYPE_ATTRIBUTE)).orElse(UNKNOWN_TAG);
    }

    @Override
    protected List<AClassDiagramModelElement> getChildren() {
        return Collections.emptyList();
    }
}
