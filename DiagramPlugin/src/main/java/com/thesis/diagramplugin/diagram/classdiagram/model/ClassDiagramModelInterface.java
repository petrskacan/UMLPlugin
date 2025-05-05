package com.thesis.diagramplugin.diagram.classdiagram.model;

import lombok.Getter;
import org.dom4j.Element;

import java.util.LinkedHashSet;

import static com.thesis.diagramplugin.utils.DiagramConstants.EXTENDS_TAG;
@Getter
public class ClassDiagramModelInterface extends AClassDiagramModelClassLikeEntity {
    private final LinkedHashSet<String> extIds = new LinkedHashSet<>();
    private final LinkedHashSet<ClassDiagramModelInterface> extensions = new LinkedHashSet<>();

    public ClassDiagramModelInterface(Element element) {
        super(element);
        readExtendedInterfaces(element);
        this.modelType = ClassDiagramModelType.INTERFACE;
    }

    private void readExtendedInterfaces(Element element) {
        element.elements().stream().filter(el ->
        EXTENDS_TAG.equals(el.getName())).forEach(el -> {
            this.extIds.add(el.getText());
        });
    }

}
