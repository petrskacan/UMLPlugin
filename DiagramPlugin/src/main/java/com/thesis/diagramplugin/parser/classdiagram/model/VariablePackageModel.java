package com.thesis.diagramplugin.parser.classdiagram.model;

import com.sun.source.tree.VariableTree;
import lombok.Getter;

@Getter
public class VariablePackageModel extends AElementPackageModel implements OwnedPackageModel{

    private final String variableType;
    private final AElementPackageModel owner;

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
