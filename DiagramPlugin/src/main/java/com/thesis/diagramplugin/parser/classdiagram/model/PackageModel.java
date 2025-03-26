package com.thesis.diagramplugin.parser.classdiagram.model;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class PackageModel extends AElementPackageModel {

    @Getter
    @Setter
    private String packageName = "default";
    private final Map<String, AElementPackageModel> elementMap = new HashMap<>();
    @Getter
    private final Set<String> knownIdentifiers = new HashSet<>();


    public PackageModel() {
        this.type = ElementType.PACKAGE;
    }

    public List<AElementPackageModel> getElements() {
        resolveTests();
        return new LinkedList<>(this.elementMap.values());
    }

    public AElementPackageModel getElement(String name) {
        return elementMap.get(name);
    }

    public void addElement(AElementPackageModel element) {
        this.elementMap.put(element.getName(), element);
    }

    private void resolveTests() {
        elementMap.values().forEach(tc -> {
            if (tc instanceof ClassTypePackageModel testClass && testClass.isTest()) {
                for (AElementPackageModel tf : elementMap.values()) {
                    if (Objects.equals(testClass.getName(), tf.getName())) continue;
                    String testClassNameUpper = testClass.getName().toUpperCase();
                    if (tf instanceof ClassTypePackageModel testFor && testClass.getName().contains(testFor.getName()) &&
                            (testClassNameUpper.startsWith("TEST") || testClassNameUpper.endsWith("TEST"))) {
                        testClass.getTestedClasses().add(testFor);
                    }
                }
            }
        });
    }

    public void addKnownIdentifier(String id) {
        this.knownIdentifiers.add(id);
    }
    public void clear() {
        this.elementMap.clear();
        this.knownIdentifiers.clear();
    }


}
