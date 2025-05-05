package com.thesis.diagramplugin.parser;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyFunction;
import com.thesis.diagramplugin.parser.classdiagram.python.PyClassDiagramParser;
import com.thesis.diagramplugin.parser.kopenogram.python.PyFunctionParser;

import java.io.File;

public class PythonDiagramParser {


    public File parseDirectory(Project project, PsiDirectory psiDir) {

        PyClassDiagramParser parser = new PyClassDiagramParser(project);

        return parser.parse(psiDir);
    }

    public File parseMethod(PsiElement function) {
        if (!(function instanceof PyFunction pyFunc)) {
            return null;
        }

        return PyFunctionParser.parse(pyFunc);
    }
}
