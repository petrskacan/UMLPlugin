package com.thesis.diagramplugin.diagram.classdiagram.model;

import lombok.Getter;
import lombok.Setter;
import org.dom4j.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.thesis.diagramplugin.utils.DiagramConstants.*;
import static com.thesis.diagramplugin.utils.DiagramConstants.PATH_ATTRIBUTE;

@Getter
public class ClassDiagramModelPackage extends AClassDiagramModelClassLikeEntityCollection {

    private String packageName;
    @Setter
    private String packagePath;
    private final Map<String, AClassDiagramModelClassLikeEntity> classLikeEntities = new HashMap<>();

//    public ClassDiagramModelPackage(String diagramXML) {
//        try {
//            Document document = DocumentHelper.parseText(diagramXML);
//            Element rootElement = document.getRootElement();
//
//            if (rootElement.getName() == PACKAGE_TAG) {
//                if (rootElement.attribute(PATH_ATTRIBUTE) != null) {
//                    this.packagePath = rootElement.attribute(PATH_ATTRIBUTE).getValue();
//                    this.packageName = rootElement.attribute(NAME_ATTRIBUTE).getValue();
//
//                    this.createClassLikeElements(rootElement);
//                    this.resolveExtendsImplements();
//                    this.resolveTests();
//                    this.resolveRelationships();
//                }
//            }
//        } catch (DocumentException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public ClassDiagramModelPackage(Element element) {
        super(element);
        this.modelType = ClassDiagramModelType.PACKAGE;
        this.packagePath = element.attribute(PATH_ATTRIBUTE).getValue();
        this.packageName = element.attribute(NAME_ATTRIBUTE).getValue();
        this.createClassLikeElements(element);
        this.resolveExtendsImplements();
        this.resolveTests();
        this.resolveRelationships();
    }

    private void createClassLikeElements(Element rootElement) {

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
                case MODULE_TAG -> {
                    entity = new ClassDiagramModelModule(element);
                    for (AClassDiagramModelClassLikeEntity child : ((ClassDiagramModelModule)entity).getContainingClassLikeEntities()) {
                        this.addClassLikeEntity(child);
                    }
                }
                case PACKAGE_TAG -> {
                    entity = new ClassDiagramModelPackage(element);
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
            classLikeEntities.put(cls.getUniqueId(), cls);
        }
    }

    public AClassDiagramModelClassLikeEntity findEntity(String uniqueId) {
        return this.classLikeEntities.get(uniqueId);
    }

    public void resolveExtendsImplements() {
        for (AClassDiagramModelClassLikeEntity entity : this.classLikeEntities.values()) {
            if (entity instanceof ClassDiagramModelClass classEntity) {
                for (String ext : classEntity.getExtIds()) {
                    AClassDiagramModelClassLikeEntity extended = classLikeEntities.get(ext);
                    if (extended instanceof ClassDiagramModelClass extendedCls) {
                        classEntity.getExtensions().add(extendedCls);
                    }
                }

                for (String iface : classEntity.getImplIds()) {
                    AClassDiagramModelClassLikeEntity impl = classLikeEntities.get(iface);
                    if (impl instanceof ClassDiagramModelInterface implIface) {
                        classEntity.getImplementations().add(implIface);
                    }
                }
            }
        }
    }

    public void resolveTests() {
        for (AClassDiagramModelClassLikeEntity entity : classLikeEntities.values()) {
            if (entity instanceof ClassDiagramModelClass cls && cls.isTest()) {
                cls.resolveTests(this);
            }
        }
    }

    public void resolveRelationships() {
        for (AClassDiagramModelClassLikeEntity entity : classLikeEntities.values()) {
            entity.resolveRelations(this);
        }
    }

    public Collection<AClassDiagramModelClassLikeEntity> getContainingClassLikeEntities() {
        return this.classLikeEntities.values();
    }
}
