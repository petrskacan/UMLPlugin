package com.thesis.diagramplugin.diagram.classdiagram.model;

import lombok.Getter;
import lombok.Setter;
import org.dom4j.Element;

import java.util.*;

import static com.thesis.diagramplugin.utils.DiagramConstants.*;

@Getter
public abstract class AClassDiagramModelClassLikeEntity extends AClassDiagramModelElement {

    protected final Set<ClassDiagramModelField> fields = new LinkedHashSet<ClassDiagramModelField>();
    protected final Set<ClassDiagramModelMethod> methods = new LinkedHashSet<ClassDiagramModelMethod>();
    @Setter
    protected String owner = null;

    public AClassDiagramModelClassLikeEntity(Element element) {
        super(element);
        Optional.ofNullable(element.attributeValue(OWNER_ATTRIBUTE)).ifPresent(this::setOwner);
        readFields(element);
        readMethods(element);
    }

    private void readFields(Element element) {
        element.elements().stream().filter(e -> VARIABLE_TAG.equals(e.getName())).forEach(var -> {
            this.fields.add(new ClassDiagramModelField(var));
        });
    }

    private void readMethods(Element element) {
        element.elements().stream().filter(e -> METHOD_TAG.equals(e.getName())).forEach(method -> {
            this.methods.add(new ClassDiagramModelMethod(method));
        });
    }

    @Override
    protected List<AClassDiagramModelElement> getChildren() {
        List<AClassDiagramModelElement> children = new LinkedList<>(this.fields);
        children.addAll(this.methods);
        return children;
    }
}
