package com.thesis.diagramplugin.plugin.editors;

import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.thesis.diagramplugin.diagram.classdiagram.ClassDiagramView;
import com.thesis.diagramplugin.diagram.classdiagram.model.ClassDiagramModelPackage;
import com.thesis.diagramplugin.rendering.classrelation.FileListener;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.CustomDependency;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.ClassTarget;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static com.thesis.diagramplugin.utils.DiagramConstants.*;

public class ClassDiagramEditor extends DiagramEditor implements FileListener {

    private ClassDiagramView diagramView;
    private ClassDiagramModelPackage diagramModel;

    private Document document;

    public ClassDiagramEditor(VirtualFile xmlFile) {
        super(xmlFile);
    }

    @Override
    public void init() throws IOException {
        try {
            document = DocumentHelper.parseText(new String(Files.readAllBytes(Paths.get(xmlFile.getPath()))));
            Element rootElement = document.getRootElement();
            diagramModel = new ClassDiagramModelPackage(rootElement);
            diagramView = new ClassDiagramView(diagramModel);
            name = diagramView.getName() + ": Class Diagram";
            xmlFile.putUserData(TITLE, name);
            diagramView.addListener(this);
        } catch (DocumentException e) {
            System.out.println("ERROR opening xml");
        }
    }

    private void save() {
        if (document == null) return;

        Map<String, Element> elementIdMap = new HashMap<>();
        buildElementIdMap(document.getRootElement(), elementIdMap);
        Set<ClassTarget> objects = diagramView.getObjectsToSave();
        System.out.println(objects);
        for (ClassTarget object : objects) {
            Element element = elementIdMap.get(object.getIdentifierName());
            if (element != null) {
                if (element.attributeValue(POS_X_ATTRIBUTE) != null) {
                    element.attribute(POS_X_ATTRIBUTE).setValue(String.valueOf(object.getX()));
                } else {
                    element.addAttribute(POS_X_ATTRIBUTE, String.valueOf(object.getX()));
                }
                if (element.attributeValue(POS_Y_ATTRIBUTE) != null) {
                    element.attribute(POS_Y_ATTRIBUTE).setValue(String.valueOf(object.getY()));
                } else {
                    element.addAttribute(POS_Y_ATTRIBUTE, String.valueOf(object.getY()));
                }
                if (element.attributeValue(SIZE_W_ATTRIBUTE) != null) {
                    element.attribute(SIZE_W_ATTRIBUTE).setValue(String.valueOf(object.getWidth()));
                } else {
                    element.addAttribute(SIZE_W_ATTRIBUTE, String.valueOf(object.getWidth()));
                }
                if (element.attributeValue(SIZE_H_ATTRIBUTE) != null) {
                    element.attribute(SIZE_H_ATTRIBUTE).setValue(String.valueOf(object.getHeight()));
                } else {
                    element.addAttribute(SIZE_H_ATTRIBUTE, String.valueOf(object.getHeight()));
                }
                if (element.attributeValue(VISIBLE_ATTRIBUTE) != null) {
                    element.attribute(VISIBLE_ATTRIBUTE).setValue(String.valueOf(object.isVisible()));
                } else {
                    element.addAttribute(VISIBLE_ATTRIBUTE, String.valueOf(object.isVisible()));
                }
            }
        }
        for (CustomDependency customDep : CustomDependency.getCustomDependencies())
        {
            String expectedName = customDep.toString();

            boolean alreadyExists = false;
            // Iterate over existing customDependecy elements
            for (Iterator<Element> it = document.getRootElement().elementIterator("customDependecy"); it.hasNext();) {
                Element existingElement = it.next();
                if (expectedName.equals(existingElement.attributeValue(NAME_ATTRIBUTE))) {
                    alreadyExists = true;
                    break;
                }
            }

            // Only add a new element if one doesn't already exist
            if (!alreadyExists) {
                Element customDependency = document.getRootElement().addElement("customDependecy");
                customDependency.addAttribute(NAME_ATTRIBUTE, expectedName);
                customDependency.addAttribute(UNIQUE_ID_ATTRIBUTE, expectedName);
                customDependency.addAttribute(FROM, customDep.getFrom().getIdentifierName());
                customDependency.addAttribute(TO, customDep.getTo().getIdentifierName());
                customDependency.addAttribute(DEPENDECY_TYPE, customDep.getType().toString());
            }
        }
        CustomDependency.clearCustomDependencies();

        try {
            FileOutputStream fileOS = new FileOutputStream(xmlFile.getPath());
            XMLWriter writer = new XMLWriter(fileOS, OutputFormat.createPrettyPrint());
            writer.write(document);
            writer.flush();
            fileOS.close();
        } catch (IOException e) {
            System.out.println("IO ERROR during creation of " + xmlFile.getName());
        }
    }

    private void buildElementIdMap(Element element, Map<String, Element> idMap) {
        if (CLASS_TAG.equals(element.getName()) || MODULE_TAG.equals(element.getName()) || PACKAGE_TAG.equals(element.getName()) ||
                ENUM_TAG.equals(element.getName()) || RECORD_TAG.equals(element.getName()) || INTERFACE_TAG.equals(element.getName())) {
            String id = element.attributeValue(UNIQUE_ID_ATTRIBUTE);
            if (id != null) {
                idMap.put(id, element);
            }
        }
        for (Element el : element.elements()) {
            buildElementIdMap(el, idMap);
        }
    }

    @Override
    public void dispose() {
        if (diagramView != null) {
            this.save();
            diagramView.removeListener(this);
        }
        Disposer.dispose(this);
    }

    @Override
    public void update(String newFile) {
        ArrayList<File> files = new ArrayList<>();
        files.add(new File(newFile));
        LocalFileSystem.getInstance().refreshIoFiles(files);
    }

    @Override
    public @NotNull JComponent getComponent() {
        return this.diagramView.getView();
    }


}
