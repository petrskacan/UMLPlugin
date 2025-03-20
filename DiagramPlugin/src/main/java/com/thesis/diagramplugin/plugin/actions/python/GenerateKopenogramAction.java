package com.thesis.diagramplugin.plugin.actions.python;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.LightVirtualFile;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.sdk.PythonSdkType;
import com.thesis.diagramplugin.parser.PythonDiagramParser;
import com.thesis.diagramplugin.plugin.actions.java.ADiagramAction;
import com.thesis.diagramplugin.plugin.editors.KopenogramEditor;

import java.io.File;

public class GenerateKopenogramAction extends ADiagramAction {

    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();

        if (!(e.getData(CommonDataKeys.PSI_ELEMENT) instanceof PyFunction pyfunction) || project == null) return;

        VirtualFile virtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        LightVirtualFile lightFile = new LightVirtualFile();
        lightFile.setOriginalFile(virtualFile);

        PythonDiagramParser parser = new PythonDiagramParser();

        File xmlFile = parser.parseMethod(pyfunction);

        if (xmlFile == null) {
            System.out.println("Error: no xml file provided");
            return;
        }

        VirtualFileManager.getInstance().syncRefresh();

        VirtualFile vXMLFile = LocalFileSystem.getInstance().findFileByIoFile(xmlFile);
        openDiagramWindow(KopenogramEditor.class, project, vXMLFile, true);
    }

    @Override
    public void update(AnActionEvent anActionEvent) {
        PsiElement element = anActionEvent.getData(CommonDataKeys.PSI_ELEMENT);
        Project project = anActionEvent.getProject();

        if (project == null) {
            anActionEvent.getPresentation().setEnabledAndVisible(false);
            return;
        }

        PythonSdkType pythonSdkType = PythonSdkType.getInstance();
        Sdk projectSdk = ProjectRootManager.getInstance(project).getProjectSdk();

        // Check if the SDK is a Python SDK
        if (projectSdk == null || !pythonSdkType.equals(projectSdk.getSdkType())) {
            // Python interpreter is not set up for the project
            anActionEvent.getPresentation().setEnabled(false);
            anActionEvent.getPresentation().setVisible(element instanceof PyFunction);
            return;
        }

        anActionEvent.getPresentation().setEnabledAndVisible(element instanceof PyFunction);
    }
}
