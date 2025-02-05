package com.thesis.diagramplugin.diagram.classdiagram.model;

import lombok.Getter;
import lombok.Setter;
import org.dom4j.Element;

import java.util.*;

import static com.thesis.diagramplugin.utils.DiagramConstants.*;

public class ClassDiagramModelModule extends AClassDiagramModelClassLikeEntityCollection {
    private final Map<String, AClassDiagramModelClassLikeEntity> classLikeEntities = new HashMap<>();

    public Collection<AClassDiagramModelClassLikeEntity> getContainingClassLikeEntities() {
        return classLikeEntities.values();
    }

    public ClassDiagramModelModule(Element element) {
        super(element);
        readContainedClasses(element);
        this.modelType = ClassDiagramModelType.MODULE;
    }

    private void readContainedClasses(Element rootElement) {
        for (Element element : rootElement.elements()) {
            final AClassDiagramModelClassLikeEntity entity;
            switch (element.getName()) {
                case CLASS_TAG -> {
                    entity = new ClassDiagramModelClass(element);
                }
                case ENUM_TAG -> {
                    entity = new ClassDiagramModelEnum(element);
                }
                case RECORD_TAG -> {
                    entity = new ClassDiagramModelRecord(element);
                }
                case INTERFACE_TAG -> {
                    entity = new ClassDiagramModelInterface(element);
                }
                default -> {
                    continue;
                }
            }

            this.addClassLikeEntity(entity);
        }
    }

    public void addClassLikeEntity(AClassDiagramModelClassLikeEntity cls) {
        if (cls != null) {
            classLikeEntities.put(cls.getName(), cls);
        }
    }

    @Override
    protected List<AClassDiagramModelElement> getChildren() {
        List<AClassDiagramModelElement> children = new ArrayList<>(super.getChildren());
        children.addAll(classLikeEntities.values());
        return children;
    }
}
