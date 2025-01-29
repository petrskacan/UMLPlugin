package com.thesis.diagramplugin.parser.classdiagram.model;

import lombok.Getter;
import lombok.Setter;

import javax.lang.model.element.Modifier;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
public abstract class AElementPackageModel {

    @Setter
    private String name = "<name>";

    protected ElementType type;

    protected final Set<String> relations = new HashSet<>();

    protected final LinkedHashSet<String> modifiers = new LinkedHashSet<>();

    protected void setModifiers(Set<Modifier> flags) {
        flags.forEach(modifier -> this.modifiers.add(modifier.toString()));
    }

    public void addRelation(String id) {
        this.relations.add(id);
    }

    public void addRelations(Set<String> ids) {
        this.relations.addAll(ids);
    }

}
