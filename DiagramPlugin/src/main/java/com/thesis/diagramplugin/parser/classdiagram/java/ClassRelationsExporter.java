package com.thesis.diagramplugin.parser.classdiagram.java;

import com.thesis.diagramplugin.parser.classdiagram.model.*;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import static com.thesis.diagramplugin.utils.DiagramConstants.*;

public class ClassRelationsExporter {

    private final PackageModel pkgModel;

    private final String path;

    public ClassRelationsExporter(PackageModel model, String path) {
        this.pkgModel = model;
        this.path = path;
    }

    private class PosPlacement {
        public int x = 0;
        public int y = 0;
        public int w = 0;
        public int h = 0;
        public boolean visible = true;
    }

    private final Map<String, ClassRelationsExporter.PosPlacement> existingElements = new HashMap<>();
    private final Map<String, List<String>> knownIdentifiers = new HashMap<>();

    public File export() {
        return exportToXMLFile();
    }
    private File exportToXMLFile() {
        if (pkgModel == null) return null;

        String fileName = path + "/" + pkgModel.getPackageName() + XML_FILE_NAME_POSTFIX_CLASS_DIAGRAM;
        File file = new File(fileName);
        try {
            // maintain placement/size of existing objects if file already exists
            if (file.exists()) {
                file.delete();
            }
            String packageId = path + "/" + pkgModel.getPackageName();
//            String packageId = getClassId(pkgModel);

            Document document = DocumentHelper.createDocument();
            Element packageElement = document.addElement(PACKAGE_TAG);
            packageElement.addAttribute(NAME_ATTRIBUTE, pkgModel.getPackageName());
            packageElement.addAttribute(PATH_ATTRIBUTE, path);
            packageElement.addAttribute(UNIQUE_ID_ATTRIBUTE, packageId);

            //copyExistingSizePosAttributes(packageId, packageElement);

            createClassElements(packageElement);

            FileOutputStream fileOS = new FileOutputStream(file);
            XMLWriter writer = new XMLWriter(fileOS, OutputFormat.createPrettyPrint());
            writer.write(document);
            writer.flush();
            fileOS.close();
        } catch (IOException e ) {
            System.out.println("IO ERROR during creation of " + fileName);
        }

        return file;
    }

    private void copyExistingSizePosAttributes(String packageId, Element packageElement) {
        ClassRelationsExporter.PosPlacement posPlacement = existingElements.get(packageId);
        if (posPlacement != null) {
            packageElement.addAttribute(POS_X_ATTRIBUTE, Integer.toString(posPlacement.x));
            packageElement.addAttribute(POS_Y_ATTRIBUTE, Integer.toString(posPlacement.y));
            packageElement.addAttribute(SIZE_W_ATTRIBUTE, Integer.toString(posPlacement.w));
            packageElement.addAttribute(SIZE_H_ATTRIBUTE, Integer.toString(posPlacement.h));
            packageElement.addAttribute(VISIBLE_ATTRIBUTE, Boolean.toString(posPlacement.visible));
        }
    }

    private void buildSizePositionMap(Element el, Map<String, ClassRelationsExporter.PosPlacement> existingElements) {
        if (PACKAGE_TAG.equals(el.getName()) || MODULE_TAG.equals(el.getName()) || ENUM_TAG.equals(el.getName()) ||
                RECORD_TAG.equals(el.getName()) || CLASS_TAG.equals(el.getName()) || INTERFACE_TAG.equals(el.getName())) {

            ClassRelationsExporter.PosPlacement pos = new ClassRelationsExporter.PosPlacement();
            String id = Optional.ofNullable(el.attributeValue(UNIQUE_ID_ATTRIBUTE)).orElse("unknown");
            Optional.ofNullable(el.attributeValue(POS_X_ATTRIBUTE)).map(Integer::parseInt).ifPresent(x -> {
                pos.x = x;
            });
            Optional.ofNullable(el.attributeValue(POS_Y_ATTRIBUTE)).map(Integer::parseInt).ifPresent(y -> {
                pos.y = y;
            });
            Optional.ofNullable(el.attributeValue(SIZE_W_ATTRIBUTE)).map(Integer::parseInt).ifPresent(w -> {
                pos.w = w;
            });
            Optional.ofNullable(el.attributeValue(SIZE_H_ATTRIBUTE)).map(Integer::parseInt).ifPresent(h -> {
                pos.h = h;
            });
            Optional.ofNullable(el.attributeValue(VISIBLE_ATTRIBUTE)).map(Boolean::parseBoolean).ifPresent(v -> {
                pos.visible = v;
            });

            existingElements.put(id, pos);

            for (Element child : el.elements()) {
                buildSizePositionMap(child, existingElements);
            }
        }
    }

    private String getClassId(AElementPackageModel cls) {
        return path + "/" + pkgModel.getPackageName() + "/" + cls.getName();
    }

    private void createClassElements(Element packageElement) {
        for (AElementPackageModel elementEntry : pkgModel.getElements()) {
            Element element = packageElement.addElement(elementEntry.getType().toString().toLowerCase());
            element.addAttribute("name", elementEntry.getName());
            element.addAttribute(UNIQUE_ID_ATTRIBUTE, getClassId(elementEntry));

            //copyExistingSizePosAttributes(getClassId(elementEntry), element);

            List<String> ids = new ArrayList<>();
            ids.add(getClassId(elementEntry));
            this.knownIdentifiers.put(elementEntry.getName(), ids);

            if (elementEntry instanceof  ClassTypePackageModel classPackageModel) {
                element.addAttribute(ABSTRACT_ATTRIBUTE, classPackageModel.isAbstract() ? "true" : "false");
                createClassVariableElements(classPackageModel, element);
                createClassMethodElements(classPackageModel, element);
                Optional.ofNullable(classPackageModel.getOwner()).ifPresent(owner -> {
                    element.addAttribute(OWNER_ATTRIBUTE, getClassId(owner));
                });
                Optional.ofNullable(classPackageModel.getExtendedClass()).ifPresent(extended -> {
                    AElementPackageModel ext = pkgModel.getElement(extended);
                    if (ext != null) {
                        element.addElement(EXTENDS_TAG).setText(getClassId(ext));
                    }
                });
                for (String iface : classPackageModel.getImplementedInterfaces()) {
                    AElementPackageModel ifc = pkgModel.getElement(iface);
                    if (ifc != null) {
                        if(elementEntry.getType().toString().equals("interface"))
                            element.addElement(EXTENDS_TAG).setText(getClassId(ifc));
                        else
                            element.addElement(IMPLEMENTATION_TAG).setText(getClassId(ifc));
                    }
                }
                if (classPackageModel.isTest()) {
                    element.addAttribute(TEST_ATTRIBUTE, "true");
                    for (ClassTypePackageModel testedCls : classPackageModel.getTestedClasses()) {
                        Element testing = element.addElement(TESTING_TAG);
                        testing.addAttribute(NAME_ATTRIBUTE, getClassId(testedCls));
                    }
                }
                createRelations(classPackageModel, element);
            }
        }
    }

    private void createClassMethodElements(ClassTypePackageModel classPackageModel, Element classElement) {
        for (MethodPackageModel method : classPackageModel.getMethods()) {
            Element methodElement = classElement.addElement(METHOD_TAG);
            methodElement.addAttribute(NAME_ATTRIBUTE, method.getName());
            methodElement.addAttribute(RETURN_TYPE, method.getReturnType());
            methodElement.addAttribute(MODIFIERS_ATTRIBURE, String.join(";", method.getModifiers()));
            methodElement.addAttribute(CONSTRUCTOR_ATTRIBUTE, method.isConstructor() ? "true" : "false");
            createMethodParameters(method, methodElement);
            createRelations(method, methodElement);
        }
    }

    private void createRelations(AElementPackageModel model, Element element) {
        for (String relation : model.getRelations()) {
            if (pkgModel.getKnownIdentifiers().contains(relation)) {
                Element rel = element.addElement(RELATION_TAG);
                AElementPackageModel rModel = pkgModel.getElement(relation);
                if (rModel != null) {
                    rel.addAttribute(NAME_ATTRIBUTE, getClassId(rModel));
                }
                // this.fixReferences(element, path);
            }
        }
    }

    private void fixReferences(Element element, String pkgPath) {
        element.elements().stream().filter(el -> RELATION_TAG.equals(el.getName())).forEach(relElement -> {
            if (relElement.attributeValue(NAME_ATTRIBUTE) != null) {
                String relName = relElement.attributeValue(NAME_ATTRIBUTE);
                if (knownIdentifiers.containsKey(relName)) {
                    List<String> ids = knownIdentifiers.get(relName);
                    String relId = ids.stream().filter(id -> id.startsWith(pkgPath)).findFirst().orElse(ids.get(0));
                    relElement.addAttribute(UNIQUE_ID_ATTRIBUTE, relId);
                }
            }
        });
    }

    private void createMethodParameters(MethodPackageModel method, Element methodElement) {
        for (VariablePackageModel variable : method.getParameters()) {
            Element variableElement = methodElement.addElement(PARAMETER_TAG);
            variableElement.addAttribute(NAME_ATTRIBUTE, variable.getName());
            variableElement.addAttribute(FIELD_TYPE_ATTRIBUTE, variable.getVariableType());
        }
    }

    private void createClassVariableElements(ClassTypePackageModel model, Element classElement) {
        for (VariablePackageModel variable : model.getVariables()) {
            Element variableElement = classElement.addElement(variable.getType().toString().toLowerCase());
            variableElement
                    .addAttribute("name", variable.getName())
                    .addAttribute("type", variable.getVariableType())
                    .addAttribute(MODIFIERS_ATTRIBURE, String.join(";", variable.getModifiers()));
            createRelations(variable, variableElement);
        }
    }
}
