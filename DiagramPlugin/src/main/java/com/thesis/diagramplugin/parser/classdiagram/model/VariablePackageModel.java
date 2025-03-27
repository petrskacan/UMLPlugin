package com.thesis.diagramplugin.parser.classdiagram.model;

import com.sun.source.tree.VariableTree;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.DependencyType;
import lombok.Getter;

@Getter
public class VariablePackageModel extends AElementPackageModel implements OwnedPackageModel{

    private final String variableType;
    private final AElementPackageModel owner;
    private DependencyType dependencyType;

    public DependencyType getDependencyType() {
        return dependencyType;
    }

    public void setDependencyType(DependencyType dependencyType) {
        this.dependencyType = dependencyType;
    }


    public VariablePackageModel(VariableTree var, AElementPackageModel owner) {
        this.setModifiers(var.getModifiers().getFlags());
        this.setName(var.getName().toString());
        this.variableType = var.getType().toString();
        this.owner = owner;
    }

    @Override
    public ElementType getType() {
        return ElementType.VARIABLE;
    }
}
