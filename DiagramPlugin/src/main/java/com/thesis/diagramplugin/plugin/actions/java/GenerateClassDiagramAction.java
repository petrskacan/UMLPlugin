package com.thesis.diagramplugin.plugin.actions.java;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.*;
import com.thesis.diagramplugin.plugin.editors.ClassDiagramEditor;
import com.thesis.diagramplugin.parser.JavaDiagramParser;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

public class GenerateClassDiagramAction extends ADiagramAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (project == null) return;

        PsiElement element = anActionEvent.getData(PlatformDataKeys.PSI_ELEMENT);
        if (element == null) {
            return;
        }

        String dir = this.getDirectory(element).getVirtualFile().getPath();
        JavaDiagramParser parser = new JavaDiagramParser();

        File xmlFile = parser.parseDirectory(dir);
        if (xmlFile == null) {
            System.out.println("Error: no xml file provided");
            return;
        }

        VfsUtil.markDirtyAndRefresh(false, true, true, project.getBaseDir());
        VfsUtil.markDirtyAndRefresh(false, true, true, this.getDirectory(element).getVirtualFile());
        VirtualFileManager.getInstance().syncRefresh();

        VirtualFile vXMLFile = LocalFileSystem.getInstance().findFileByIoFile(xmlFile);
        if (vXMLFile == null) {
            System.out.println("Error");
            return;
        }
        openDiagramWindow(ClassDiagramEditor.class, project, vXMLFile, true);
    }

    @Override
    public void update(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        VirtualFile virtualFile = anActionEvent.getDataContext().getData(CommonDataKeys.VIRTUAL_FILE);

        if (virtualFile != null && project != null) {
            PsiDirectory psiDirectory = Optional.of(PsiManager.getInstance(project)).map(fm ->
                    fm.findDirectory(virtualFile)).orElse(null);

            if (psiDirectory != null) {
                boolean isJavaProject = Arrays.stream(psiDirectory.getFiles()).anyMatch(file -> file.getName().endsWith(".java"));
                anActionEvent.getPresentation().setEnabledAndVisible(isJavaProject);
                return;
            }
        }
        anActionEvent.getPresentation().setEnabledAndVisible(false);
    }

}
