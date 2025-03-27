package com.thesis.diagramplugin.parser.classdiagram.java;

import com.sun.source.tree.*;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.thesis.diagramplugin.parser.classdiagram.model.PackageModel;
import com.thesis.diagramplugin.parser.classdiagram.model.*;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.DependencyType;

import java.util.HashSet;

import static com.thesis.diagramplugin.parser.classdiagram.model.ElementType.ENUM;

public class ClassRelationsScanner extends TreePathScanner<TreePath, AElementPackageModel> {

    private final PackageModel model;

    public ClassRelationsScanner(PackageModel model) {
        this.model = model;
    }


    @Override
    public TreePath visitClass(ClassTree node, AElementPackageModel parent) {

        if (node.getSimpleName() == null || node.getSimpleName().contentEquals("")) {
            return null;
        }

        if (this.getCurrentPath().getCompilationUnit().getPackageName() != null) {
            model.setPackageName(this.getCurrentPath().getCompilationUnit().getPackageName().toString());
        }

        ClassTypePackageModel classModel = new ClassTypePackageModel(node);

        if (parent instanceof ClassTypePackageModel parentModel) {
            classModel.setOwner(parentModel);
        }

        model.addElement(classModel);
        model.addKnownIdentifier(classModel.getName());

        return super.visitClass(node, classModel);
    }

    private HashSet<Tree> scanCollections(Tree fieldType) {
        HashSet<Tree> pointers = new HashSet<>();
        if (fieldType == null) return pointers;

        if (fieldType.getKind().equals(Tree.Kind.PARAMETERIZED_TYPE)) {
            ParameterizedTypeTree parametrizedReturnType = (ParameterizedTypeTree) fieldType;
            for (Tree argument : parametrizedReturnType.getTypeArguments()) {
                pointers.add(argument);
                pointers.addAll(scanCollections(argument));
            }
            pointers.add(parametrizedReturnType.getType());
        } else if (fieldType.getKind().equals(Tree.Kind.ARRAY_TYPE)) {
            ArrayTypeTree arrayReturnType = (ArrayTypeTree) fieldType;
            pointers.add(arrayReturnType.getType());
        } else {
            pointers.add(fieldType);
        }

        return pointers;
    }

    private VariablePackageModel scanVariable(VariableTree variable, AElementPackageModel parent) {
        VariablePackageModel var = new VariablePackageModel(variable, parent);
        HashSet<String> pointers = new HashSet<>();
        boolean isComposed = variable.getInitializer() != null && variable.getInitializer().getKind() == Tree.Kind.NEW_CLASS;
        var.setDependencyType(isComposed ? DependencyType.COMPOSITION : DependencyType.AGGREGATION);
        for (Tree argument : scanCollections(variable.getType())) {
            if (argument.getKind().equals(Tree.Kind.IDENTIFIER)) {
                pointers.add(((IdentifierTree)argument).getName().toString());
            }
        }
        if (parent instanceof ClassTypePackageModel parentClass) {
            parentClass.addVariable(var);
            var.addRelations(pointers);
            model.addKnownIdentifier(var.getName());
        } else if (parent instanceof MethodPackageModel parentMethod) {
            parentMethod.addParameter(var);
            parentMethod.addRelations(pointers);
            model.addKnownIdentifier(parentMethod.getName());
        }
        return var;
    }

    @Override
    public TreePath visitMethod(MethodTree node, AElementPackageModel parent) {
        if (!(parent instanceof ClassTypePackageModel parentClass)) {
            return super.visitMethod(node, parent);
        }

        if (node.getName().contentEquals("<init>") && node.getBody().getStatements().toString().equals("super();")) {
            return null;
        }

        MethodPackageModel methodModel = new MethodPackageModel(node, parentClass);
        for (VariableTree var : node.getParameters()) {
            scanVariable(var, methodModel);
        }

        parentClass.addMethod(methodModel);
        model.addKnownIdentifier(methodModel.getName());

        return super.visitMethod(node, methodModel);
    }

    @Override
    public TreePath visitVariable(VariableTree node, AElementPackageModel parent) {
        // variable inside method
        if (parent instanceof MethodPackageModel) {
            return super.visitVariable(node, parent);
        }
        // variable inside class (field)
        if (parent instanceof ClassTypePackageModel parentClass) {
            if (ENUM.equals(parent.getType()) && node.getInitializer() != null && node.getInitializer().getKind() == Tree.Kind.NEW_CLASS &&
                    node.getType().toString().equals(parent.getName())) {
                VariablePackageModel field = new VariablePackageModel(node, parent);
                parentClass.addVariable(field);
                return null;
            }
            VariablePackageModel field = scanVariable(node, parent);
            return super.visitVariable(node, field);
        }
        return null;
    }

    @Override
    public TreePath visitIdentifier(IdentifierTree node, AElementPackageModel parent) {
        if (parent instanceof OwnedPackageModel owned) {
            parent = getTopOwner(owned);
            if (parent instanceof ClassTypePackageModel classModel) {
                classModel.addRelation(node.getName().toString());
            }
        }
        return null;
    }

    protected AElementPackageModel getTopOwner(OwnedPackageModel owned) {
        AElementPackageModel owner = owned.getOwner();
        if (owner instanceof OwnedPackageModel own) {
            return getTopOwner(own);
        }
        return owner;
    }


}
