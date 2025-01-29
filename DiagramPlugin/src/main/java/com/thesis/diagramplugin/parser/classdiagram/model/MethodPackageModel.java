package com.thesis.diagramplugin.parser.classdiagram.model;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.MethodTree;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
public class MethodPackageModel extends AElementPackageModel  implements OwnedPackageModel {

    private final Set<VariablePackageModel> parameters = new LinkedHashSet<>();
    private final String returnType;
    private final ClassTypePackageModel owner;
    private final boolean constructor;
    @Setter
    private boolean isTest;


    public MethodPackageModel(MethodTree method, ClassTypePackageModel owner) {
        this.owner = owner;
        this.returnType = method.getReturnType() != null ? method.getReturnType().toString() : "*NULL*";
        this.isTest = resolveTest(method);
//        if (this.owner != null) {
//            this.owner.setTest(this.isTest);
//        }
        this.setModifiers(method.getModifiers().getFlags());
        this.constructor = method.getName().contentEquals("<init>");
        if (this.isConstructor()) {
            this.setName(this.owner.getName());
        } else {
            this.setName(method.getName().toString());
        }
    }

    public void addParameter(VariablePackageModel parameter) {
        this.parameters.add(parameter);
    }

    @Override
    public ElementType getType() { return ElementType.METHOD; }


    private boolean resolveTest(MethodTree method) {
        if (!method.getName().contentEquals("<init>")) {
            for (AnnotationTree anotation : method.getModifiers().getAnnotations()) {
                if (anotation.getAnnotationType().toString().contains("Test")) {
                    return true;
                }
            }
        }
        return false;
    }
}
