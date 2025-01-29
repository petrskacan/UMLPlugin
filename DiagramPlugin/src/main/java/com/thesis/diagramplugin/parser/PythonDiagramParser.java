package com.thesis.diagramplugin.parser;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFunction;
import com.thesis.diagramplugin.parser.classdiagram.python.PyClassDiagramParser;
import com.thesis.diagramplugin.parser.kopenogram.python.PyFunctionParser;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

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
