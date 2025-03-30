package com.thesis.diagramplugin.diagram.classdiagram;

import com.thesis.diagramplugin.diagram.classdiagram.model.*;
import com.thesis.diagramplugin.rendering.classrelation.bluej.SortedProperties;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.Package;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.PkgMgrFrame;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.*;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.ClassTarget;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.DependentTarget;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.PackageTarget;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import static com.thesis.diagramplugin.utils.DiagramConstants.UNKNOWN_TAG;

public class ClassDiagramViewBuilder {

    private final HashSet<String> identifiers = new HashSet<>();
    private final LinkedHashMap<AClassDiagramModelClassLikeEntity, DependentTarget> classes = new LinkedHashMap<>();
    private final LinkedHashMap<ClassDiagramModelPackage, PackageTarget> packages = new LinkedHashMap<>();

    private final LinkedHashMap<AClassDiagramModelClassLikeEntity, AClassDiagramModelClassLikeEntity> ownershipMap = new LinkedHashMap<>();
    private final HashSet<ClassDiagramModelClass> testClasses = new HashSet<>();
    private final SortedProperties props = new SortedProperties();

    private final ArrayList<String> dependencies = new ArrayList<>();

    private final ClassDiagramModelPackage model;
    @Getter
    private Package pkg = null;

    private class Relation {
        private final AClassDiagramModelElement from;
        private final AClassDiagramModelElement to;

        public Relation(AClassDiagramModelElement from, AClassDiagramModelElement to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Relation rel) {
                return this.from == rel.from && this.to == rel.to;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return (from.getName() + to.getName()).hashCode();
        }
    }
    private final HashSet<Relation> relations = new HashSet<>();


    public ClassDiagramViewBuilder(ClassDiagramModelPackage model) {
        this.model = model;
    }

    public PkgMgrFrame buildView() {

        try {

            pkg = new Package();
            pkg.setBaseName(model.getPackageName());

            PackageTarget packageTarget = new PackageTarget(pkg, model.getUniqueId());
            packageTarget.setDisplayName(model.getPackageName());
            packageTarget.setVisible(model.isVisible());
            if (model.hasSizeAndPosSet()) {
                packageTarget.setSize(model.getWidth(), model.getHeight());
                packageTarget.setPos(model.getPosX(), model.getPosY());
                packageTarget.disableLayoutGeneration();
            }
            packages.put(model, packageTarget);
            pkg.setPkgTarget(packageTarget);

            preparePackageElements(model, pkg);
            resolveElementDetails(model, pkg);

            addElements(model, pkg);

            pkg.generateLayout();

        } catch (IOException | NumberFormatException ex) {
            System.err.println(ex);
        }


        return PkgMgrFrame.createFrame(pkg);
    }

    private void preparePackageElements(AClassDiagramModelClassLikeEntityCollection model, Package pkg) {
        for (AClassDiagramModelClassLikeEntity el : model.getContainingClassLikeEntities()) {
            ownershipMap.put(el, model);
            if (el.getModelType() == ClassDiagramModelType.PACKAGE) {
                PackageTarget packageTarget = new PackageTarget(pkg, el.getUniqueId());
                packageTarget.setDisplayName(el.getName());
                packageTarget.setVisible(el.isVisible());
                if (el.hasSizeAndPosSet()) {
                    packageTarget.setPos(el.getPosX(), el.getPosY());
                    packageTarget.setSize(el.getWidth(), el.getHeight());
                    packageTarget.disableLayoutGeneration();
                }
                this.packages.put((ClassDiagramModelPackage) el, packageTarget);
                preparePackageElements((ClassDiagramModelPackage) el, pkg);
                continue;
            }
            if (el.getModelType() == ClassDiagramModelType.TEST_CLASS) {
                this.testClasses.add((ClassDiagramModelClass) el);
            }

            ClassTarget ct = new ClassTarget(pkg, el.getUniqueId(), el.getModelType().type);
            if (el.hasSizeAndPosSet()) {
                ct.setSize(el.getWidth(), el.getHeight());
                ct.setPos(el.getPosX(), el.getPosY());
                ct.disableLayoutGeneration();
            }
            ct.setVisible(el.isVisible());
            ct.setDisplayName(el.getName());
            this.classes.put(el, ct);

            if (el.getModelType() == ClassDiagramModelType.MODULE) {
                preparePackageElements((ClassDiagramModelModule) el, pkg);
            }
        }
    }


    private void resolveElementDetails(ClassDiagramModelPackage model, Package pkg) {

        for (AClassDiagramModelClassLikeEntity el : classes.keySet()) {
            resolveContainment(el, model, pkg);
            resolveFields(el, pkg);
            resolveMethods(el, pkg);
            resolveImplementations(el, pkg);
            resolveExtensions(el, pkg);
            resolveTests(el, pkg);
            resolveUses(el, pkg);
            resolveModules(el, pkg);
        }
    }

    private void resolveTests(AClassDiagramModelClassLikeEntity el, Package pkg) {
        if (el instanceof ClassDiagramModelClass cls && cls.isTest()) {
            for (AClassDiagramModelClassLikeEntity testedClass : cls.getTestedClasses().values()) {
                pkg.addDependency(new AssociationDependency(pkg, classes.get(testedClass), classes.get(cls)), true);
            }
        }
    }

    private void resolveContainment(AClassDiagramModelClassLikeEntity el, ClassDiagramModelPackage model, Package pkg) {
        if (el.getOwner() != null) {
            AClassDiagramModelClassLikeEntity owner = model.findEntity(el.getOwner());
            if (owner != null) {
                pkg.addDependency(new ContainmentDependency(pkg, classes.get(el), classes.get(model.findEntity(el.getOwner()))), true);
                dependencies.add(el.getName() + " =======> " + model.findEntity(el.getOwner()).getName() + " DEPENDENCY CONTAINMENT");
                System.out.println(el.getName() + " =======> "+ model.findEntity(el.getOwner()).getName() +  " DEPENDENCY USES");
                System.out.println("FIND ME CONTAINMENT");
            }
        }
    }

    private void resolveModules(AClassDiagramModelClassLikeEntity entity, Package pkg) {
        if (entity instanceof ClassDiagramModelModule module) {
            for (AClassDiagramModelClassLikeEntity en : module.getContainingClassLikeEntities()) {
                pkg.addDependency(new ContainmentDependency(pkg, classes.get(en), classes.get(entity)), true);
                dependencies.add(entity.getName() + " =======> " + en.getName() + " DEPENDENCY CONTAINMENT");
                System.out.println(entity.getName() + " =======> "+ en.getName() +  " DEPENDENCY USES");
                System.out.println("FIND ME CONTAINMENT MODULES");
                if (en instanceof ClassDiagramModelModule) {
                    resolveModules(en, pkg);
                }
            }
        }
    }

    private void resolveFields(AClassDiagramModelClassLikeEntity el, Package pkg) {
        LinkedHashSet<String> fields = new LinkedHashSet<>();

        for (ClassDiagramModelField field : el.getFields()) {
            for (AClassDiagramModelClassLikeEntity to : field.getRelations().values()) {
                if (relations.add(new Relation(el, to))) {
                    to.getUniqueId();
                    Dependency uses = el.getDependencyType().create(pkg, classes.get(el), classes.get(to));
                    pkg.addDependency(uses, true);
                    dependencies.add(el.getName() + " =======> " + to.getName() + " DEPENDENCY ASSOCIATION");
                    System.out.println(el.getName() + " =======> "+ to.getName()+  " DEPENDENCY USES");
                    System.out.println("FIND ME FIELDS ASSOCIATION");
                }
            }
            if (classes.get(el) instanceof ClassTarget) {
                String fieldExp = getVisibilityModifierSign(field) + field.getName();
                if (field.getType() != null && !UNKNOWN_TAG.equals(field.getType())) {
                    fieldExp = fieldExp + " : " + field.getType();
                }
                fields.add(fieldExp);
            }
        }

        ((ClassTarget) classes.get(el)).putFields(fields);
    }

    private String getVisibilityModifierSign(AClassDiagramModelElement el) {
        String visibilitySign = "";
        if (el.isPrivate()) {
            visibilitySign = "-";
        } else if (el.isPublic()) {
            visibilitySign = "+";
        } else if (el.isProtected()) {
            visibilitySign = "#";
        }
        return visibilitySign + " ";
    }

    private void resolveMethods(AClassDiagramModelClassLikeEntity el, Package pkg) {
        LinkedHashSet<String> methods = new LinkedHashSet<>();
        for (ClassDiagramModelMethod method : el.getMethods()) {
            for (AClassDiagramModelClassLikeEntity to : method.getRelations().values()) {
                if (relations.add(new Relation(el, to))) {
                    Dependency uses = new AssociationDependency(pkg, classes.get(el), classes.get(to));
                    pkg.addDependency(uses, true);
                    dependencies.add(el.getName() + " =======> " + to.getName() + " DEPENDENCY USES");
                    System.out.println(el.getName() + " =======> " + to.getName() + " DEPENDENCY USES");
                    System.out.println("FIND ME METHODS");
                }
            }
            if (classes.get(el) instanceof ClassTarget) {
                String methodExp = getVisibilityModifierSign(method) + method.getName() + method.getParameterString();
                if (method.isConstructor()) {
                    methods.add(methodExp);
                    continue;
                }
                if (method.getReturnType() != null && !"".equals(method.getReturnType())) {
                    methodExp += " : " + method.getReturnType();
                }
                methods.add(methodExp);
            }
        }

        ((ClassTarget) classes.get(el)).putMethods(methods);
    }

    private void resolveImplementations(AClassDiagramModelClassLikeEntity el, Package pkg) {
        if (el instanceof ClassDiagramModelClass cls) {
            for (AClassDiagramModelElement to : cls.getImplementations()) {
                pkg.addDependency(new ImplementsDependency(pkg, classes.get(el), classes.get(to)), true);
                relations.add(new Relation(el, to));
                dependencies.add(el.getName() + " =======> " + to.getName() + " DEPENDENCY (impl)");
            }
        }
    }

    private void resolveUses(AClassDiagramModelClassLikeEntity el, Package pkg) {
        for (AClassDiagramModelClassLikeEntity rel : el.getRelations().values()) {
            if (this.relations.add(new Relation(el, rel))) {
                pkg.addDependency(new UsesDependency(pkg, classes.get(el), classes.get(rel)), true);
                dependencies.add(el.getName() + " =======> " + rel.getName() + " DEPENDENCY (uses)");
                System.out.println(el.getName() + " =======> " + rel.getName() + " DEPENDENCY USES");
                System.out.println("FIND ME USES");
            }
        }
    }

    private void resolveExtensions(AClassDiagramModelClassLikeEntity el, Package pkg) {
        if (el instanceof ClassDiagramModelClass cls) {
            for (AClassDiagramModelElement to : cls.getExtensions()) {
                pkg.addDependency(new ExtendsDependency(pkg, classes.get(el), classes.get(to)), true);
                relations.add(new Relation(el, to));
                dependencies.add(el.getName() + " =======> " + to.getName() + " DEPENDENCY (ext)");
            }
        }
        if (el instanceof ClassDiagramModelInterface cls) {
            for (AClassDiagramModelElement to : cls.getExtensions()) {
                pkg.addDependency(new ExtendsDependency(pkg, classes.get(el), classes.get(to)), true);
                relations.add(new Relation(el, to));
                dependencies.add(el.getName() + " =======> " + to.getName() + " DEPENDENCY (ext)");
            }
        }
    }

    private void addElements(AClassDiagramModelClassLikeEntityCollection model, Package pkg) {
        for (AClassDiagramModelClassLikeEntity el : model.getContainingClassLikeEntities()) {
            if (model.getModelType() == ClassDiagramModelType.MODULE) {
                if (ownershipMap.containsKey(model)) {
                    AClassDiagramModelElement packageElement = ownershipMap.get(model);
                    if (packageElement.getModelType() == ClassDiagramModelType.PACKAGE && packages.containsKey((ClassDiagramModelPackage) packageElement)) {
                        PackageTarget parent = packages.get((ClassDiagramModelPackage) packageElement);
                        pkg.addTarget(parent, classes.get(el));
                    }
                }
            } else if (model instanceof ClassDiagramModelPackage modelPackage && packages.containsKey(modelPackage)){
                if (el.getModelType() != ClassDiagramModelType.PACKAGE) {
                    pkg.addTarget(packages.get(modelPackage), classes.get(el));
                }
            }
            if (el.getModelType() == ClassDiagramModelType.MODULE || el.getModelType() == ClassDiagramModelType.PACKAGE) {
                addElements((AClassDiagramModelClassLikeEntityCollection)el, pkg);
            }
        }
    }
}
