package com.thesis.diagramplugin.parser;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.thesis.diagramplugin.parser.classdiagram.java.ClassRelationsProcessor;
import com.thesis.diagramplugin.parser.classdiagram.model.PackageModel;
import com.thesis.diagramplugin.parser.kopenogram.java.MethodParser;
import com.thesis.diagramplugin.parser.classdiagram.java.ClassRelationsExporter;
import org.jetbrains.jps.javac.JavacMain;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class  JavaDiagramParser implements IDiagramParser {

    public File parseMethod(PsiElement method) {
        if (!(method instanceof PsiMethod psiMethod)) {
            return null;
        }
        return MethodParser.parse(psiMethod);
    }

    public File parseDirectory(String path) {
        JavaCompiler compiler = null;
        ClassRelationsProcessor processor = new ClassRelationsProcessor();
        File xmlFile = null;
        try {
            compiler = this.getCompiler();
            File dir = new File(path);
            if (dir.isDirectory()) {
                File[] fileArray = dir.listFiles();
                List<File> files = Arrays.asList(fileArray).stream().filter(f -> f.getName().endsWith(".java")).collect(Collectors.toList());

                StandardJavaFileManager fileManager = compiler.getStandardFileManager(null,null,null);
                Path tmpDir = Files.createTempDirectory("compile-test-");
                fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singleton(tmpDir.toFile()));

                JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, fileManager.getJavaFileObjectsFromFiles(files));
                task.setProcessors(Collections.singleton(processor));
                task.call();

                PackageModel packageModel = processor.getPackageModel();
                ClassRelationsExporter exporter = new ClassRelationsExporter(packageModel, path);
                xmlFile = exporter.export();
            }

        } catch (ClassNotFoundException | IOException | InstantiationException | IllegalAccessException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return xmlFile;
    }

    private JavaCompiler getCompiler() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if(compiler == null) {
            compiler = (JavaCompiler) Class.forName("com.sun.tools.javac.api.JavacTool", true, JavacMain.class.getClassLoader()).newInstance();
        }
        return compiler;
    }

}
