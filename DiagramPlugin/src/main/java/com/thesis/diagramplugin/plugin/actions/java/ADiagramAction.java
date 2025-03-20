package com.thesis.diagramplugin.plugin.actions.java;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl;
import com.thesis.diagramplugin.plugin.editors.DiagramEditor;


public abstract class ADiagramAction extends AnAction {

    protected PsiDirectory getDirectory(PsiElement element) {
        PsiDirectory psiDirectory = null;
        if (element instanceof PsiJavaDirectoryImpl) {
            psiDirectory = (PsiJavaDirectoryImpl) element;
        } else if (element instanceof PsiPackage) {
            psiDirectory = ((PsiPackage)element).getDirectories()[0];
        } else if (element instanceof PsiMethod method) {
            psiDirectory = method.getContainingFile().getContainingDirectory();
        } else if (element instanceof PsiClass psiClass) {
            psiDirectory = psiClass.getContainingFile().getContainingDirectory();
        } else {
            return null;
        }
        return psiDirectory;
    }

    public FileEditor[] openDiagramWindow(Class<? extends DiagramEditor> editorCls, Project project, VirtualFile virtualFile, boolean b) {
        virtualFile.refresh(false, true);
        FileEditorManager.getInstance(project).closeFile(virtualFile);
        return FileEditorManager.getInstance(project).openFile(virtualFile, b);
    }
    @Override
    public ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
