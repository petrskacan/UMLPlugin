package com.thesis.diagramplugin.parser;

import com.intellij.psi.PsiElement;

import java.io.File;

public interface IDiagramParser {

    public File parseDirectory(String path);

    public File parseMethod(PsiElement method);
}
