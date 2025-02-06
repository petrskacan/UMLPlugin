package com.thesis.diagramplugin.parser.classdiagram.java;

import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import com.thesis.diagramplugin.parser.classdiagram.model.PackageModel;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class ClassRelationsProcessor extends AbstractProcessor {

    private Trees trees;
    private ClassRelationsScanner scanner;
    private PackageModel packageModel;

    @Override
    public void init(ProcessingEnvironment pEnv) {
        super.init(pEnv);
        this.trees = Trees.instance(pEnv);
        this.packageModel = new PackageModel();
        this.scanner = new ClassRelationsScanner(this.packageModel);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element e : roundEnv.getRootElements()) {
            TreePath path = trees.getPath(e);

            scanner.scan(path, null);
        }
        return false;
    }

    public PackageModel getPackageModel() {
        return this.packageModel;
    }

}
