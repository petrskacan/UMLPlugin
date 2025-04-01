package com.thesis.diagramplugin.diagram.classdiagram.model;

public enum ClassDiagramModelType {
    CLASS(null),
    ENUM("enum"),
    RECORD("record"),
    INTERFACE("interface"),
    TEST_CLASS("unittest"),
    ABSTRACT_CLASS("abstract"),
    MODULE("module"),
    PACKAGE("package"),
    CUSTOMDEPENDECY("customDependency");

    public final String type;

    private ClassDiagramModelType(String type) {
        this.type = type;
    }
}
