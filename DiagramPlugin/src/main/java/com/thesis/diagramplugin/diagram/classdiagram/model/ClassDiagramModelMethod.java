package com.thesis.diagramplugin.diagram.classdiagram.model;

import lombok.Getter;
import org.dom4j.Element;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.thesis.diagramplugin.utils.DiagramConstants.*;

@Getter
public class ClassDiagramModelMethod extends AClassDiagramModelElement {

    private boolean constructor;
    private final List<ClassDiagramModelField> parameters = new LinkedList<>();
    private String returnType;

    public String getParameterString() {
//        return parameters.stream().map(field -> field.getType() + " " + field.getName()).collect(Collectors.joining(", ", "(", ")"));
        return parameters.stream()
                .map(field -> (UNKNOWN_TAG.equals(field.getType()) ? "" : (field.getType() + " ")) + field.getName())
                .collect(Collectors.joining(", ", "(", ")"));
    }

    public ClassDiagramModelMethod(Element element) {
        super(element);
        readParameters(element);
        Optional.ofNullable(element.attributeValue(CONSTRUCTOR_ATTRIBUTE)).ifPresent(c -> this.constructor = Boolean.parseBoolean(c));
        Optional.ofNullable(element.attributeValue(RETURN_TYPE)).ifPresent(rt -> this.returnType = rt);
    }

    private void readParameters(Element element) {
        element.elements().stream().filter(e -> PARAMETER_TAG.equals(e.getName())).forEach(param -> {
            this.parameters.add(new ClassDiagramModelField(param));
        });
    }

    @Override
    protected List<AClassDiagramModelElement> getChildren() {
        return new LinkedList<>(this.parameters);
    }
}
