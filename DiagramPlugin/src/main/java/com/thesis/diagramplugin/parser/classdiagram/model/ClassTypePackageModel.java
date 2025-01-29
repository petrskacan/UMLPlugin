package com.thesis.diagramplugin.parser.classdiagram.model;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.Tree;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static javax.lang.model.element.Modifier.ABSTRACT;

@Getter
@Setter
public class ClassTypePackageModel extends AElementPackageModel {

    private ClassTypePackageModel owner;
    private LinkedList<VariablePackageModel> variables = new LinkedList<>();

    private LinkedList<MethodPackageModel> methods = new LinkedList<>();

    private boolean test = false;
    private boolean isAbstract = false;

    private HashSet<ClassTypePackageModel> testedClasses = new HashSet<>();
    private String extendedClass;
    private LinkedHashSet<String> implementedInterfaces = new LinkedHashSet<>();

    public ClassTypePackageModel(ClassTree classNode) {
        super.setName(classNode.getSimpleName().toString());

        this.setType(ElementType.fromTreeKind(classNode.getKind()));

        this.setModifiers(classNode.getModifiers().getFlags());
        this.isAbstract = this.getModifiers().contains(ABSTRACT.toString());
        resolveExtendedClass(classNode);
        resolveImplementedInterfaces(classNode);
    }

    public ClassTypePackageModel(ElementType type) {
        this.setType(type);
    }

    public void setType(ElementType type) {
        if (type == ElementType.CLASS || type == ElementType.ENUM || type == ElementType.INTERFACE || type == ElementType.RECORD || type == ElementType.ANNOTATION) {
            this.type = type;
        } else {
            throw new InstantiationError("Wrong type : " + type.toString() + " used for creating instance");
        }
    }

    public void addImplementedInterface(String name) {
        this.implementedInterfaces.add(name);
    }

    public void addVariable(VariablePackageModel parameter) {
        this.variables.add(parameter);
    }

    public void addMethod(MethodPackageModel method) {
        this.methods.add(method);
        if (method.isTest()) {
            this.test = true;
        }
    }

    private void resolveImplementedInterfaces(ClassTree node) {
        if (node.getImplementsClause() != null && !node.getImplementsClause().isEmpty()) {
            node.getImplementsClause().forEach(implementationClause -> {
                IdentifierTree implTree = null;
                if (implementationClause.getKind().equals(Tree.Kind.IDENTIFIER)) {
                    implTree = (IdentifierTree) implementationClause;
                } else if (implementationClause.getKind().equals(Tree.Kind.PARAMETERIZED_TYPE)) {
                    ParameterizedTypeTree paramTree = (ParameterizedTypeTree) implementationClause;
                    if (paramTree.getType().getKind().equals(Tree.Kind.IDENTIFIER)) {
                        implTree = (IdentifierTree) paramTree.getType();
                    }
                }
                Optional.ofNullable(implTree).map(IdentifierTree::getName).map(Objects::toString).ifPresent(this::addImplementedInterface);
            });
        }
    }
    private void resolveExtendedClass(ClassTree node) {
        if (node.getExtendsClause() != null) {
            Tree.Kind kind = node.getExtendsClause().getKind();
            IdentifierTree idTree = null;
            if (kind == Tree.Kind.IDENTIFIER) {
                idTree = (IdentifierTree) node.getExtendsClause();
            } else if (kind == Tree.Kind.PARAMETERIZED_TYPE) {
                ParameterizedTypeTree paramTree = (ParameterizedTypeTree) node.getExtendsClause();
                if (paramTree.getType().getKind().equals(Tree.Kind.IDENTIFIER)) {
                    idTree = (IdentifierTree) paramTree.getType();
                }
            }
            Optional.ofNullable(idTree).map(IdentifierTree::getName).map(Objects::toString).ifPresent(this::setExtendedClass);
        }
    }
}
