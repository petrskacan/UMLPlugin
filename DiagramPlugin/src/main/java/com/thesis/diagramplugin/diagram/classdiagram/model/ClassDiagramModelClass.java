package com.thesis.diagramplugin.diagram.classdiagram.model;

import lombok.Getter;
import org.dom4j.Element;

import java.util.*;

import static com.thesis.diagramplugin.utils.DiagramConstants.*;

@Getter
public class ClassDiagramModelClass extends AClassDiagramModelClassLikeEntity {

    private final LinkedHashSet<ClassDiagramModelInterface> implementations = new LinkedHashSet<>();
    private final LinkedHashSet<String> implIds = new LinkedHashSet<>();
    private final LinkedHashSet<ClassDiagramModelClass> extensions = new LinkedHashSet<>();
    private final LinkedHashSet<String> extIds = new LinkedHashSet<>();
    private final HashMap<String, AClassDiagramModelClassLikeEntity> testedClasses = new HashMap<>();

    public ClassDiagramModelClass(Element element) {
        super(element);
        readExtendedClasses(element);
        readImplementedInterfaces(element);
        this.modelType = ClassDiagramModelType.CLASS;
        if ("true".equals(element.attributeValue(ABSTRACT_ATTRIBUTE))) {
            this.setAbstract();
        }
        readTests(element);
    }

    private void readImplementedInterfaces(Element element) {
        element.elements().stream().filter(el -> IMPLEMENTATION_TAG.equals(el.getName())).forEach(el -> {
            this.implIds.add(el.getText());
        });
    }

    private void readExtendedClasses(Element element) {
        element.elements().stream().filter(el -> EXTENDS_TAG.equals(el.getName())).forEach(el -> {
            this.extIds.add(el.getText());
        });
    }

    private void readTests(Element element) {
        if ("true".equals(element.attributeValue(TEST_ATTRIBUTE))) {
            this.modelType = ClassDiagramModelType.TEST_CLASS;
        }

        element.elements().stream().filter(el -> TESTING_TAG.equals(el.getName())).forEach(testingTag -> {
            Optional.ofNullable(testingTag.attributeValue(NAME_ATTRIBUTE)).ifPresent(testing -> {
                this.testedClasses.put(testing, null);
            });
        });
    }

    public void resolveTests(ClassDiagramModelPackage pkg) {
        for (Iterator<Map.Entry<String, AClassDiagramModelClassLikeEntity>> it = this.testedClasses.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, AClassDiagramModelClassLikeEntity> relationEntry = it.next();
            AClassDiagramModelClassLikeEntity testedCls = pkg.getClassLikeEntities().get(relationEntry.getKey());
            if (testedCls != null) {
                relationEntry.setValue(testedCls);
            } else {
                it.remove();
            }
        }
    }

    public boolean isAbstract() {
        return this.getModelType() == ClassDiagramModelType.ABSTRACT_CLASS;
    }

    public void setAbstract() {
        this.modelType = ClassDiagramModelType.ABSTRACT_CLASS;
    }

    public boolean isTest() {
        return ClassDiagramModelType.TEST_CLASS.equals(this.getModelType());
    }

}
