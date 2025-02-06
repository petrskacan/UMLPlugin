package com.thesis.diagramplugin.plugin.actions.java;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.testFramework.LightVirtualFile;
import com.thesis.diagramplugin.plugin.editors.KopenogramEditor;
import com.thesis.diagramplugin.parser.JavaDiagramParser;

import java.io.File;

public class GenerateKopenogramAction extends ADiagramAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiElement element = e.getData(CommonDataKeys.PSI_ELEMENT);
        Project project = e.getProject();

        if (!(element instanceof PsiMethod method) || project == null) return;

        VirtualFile virtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        LightVirtualFile lightFile = new LightVirtualFile();
        lightFile.setOriginalFile(virtualFile);

        JavaDiagramParser parser = new JavaDiagramParser();

        File xmlFile = parser.parseMethod(method);

        if (xmlFile == null) {
            System.out.println("Error: no xml file provided");
            return;
        }

        VirtualFileManager.getInstance().syncRefresh();

        VirtualFile vXMLFile = LocalFileSystem.getInstance().findFileByIoFile(xmlFile);
        openDiagramWindow(KopenogramEditor.class, project, vXMLFile, true);
    }

    @Override
    public void update(AnActionEvent e) {
        PsiElement element = e.getData(CommonDataKeys.PSI_ELEMENT);

        e.getPresentation().setEnabledAndVisible(element instanceof PsiMethod);
    }
}
