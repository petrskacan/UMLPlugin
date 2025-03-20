package com.thesis.diagramplugin.plugin.actions.python;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.jetbrains.python.sdk.PythonSdkType;
import com.jetbrains.python.sdk.PythonSdkUtil;
import com.thesis.diagramplugin.parser.PythonDiagramParser;
import com.thesis.diagramplugin.plugin.actions.java.ADiagramAction;
import com.thesis.diagramplugin.plugin.editors.ClassDiagramEditor;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

public class GenerateClassDiagramAction extends ADiagramAction {

    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (project == null) return;

        PsiElement element = anActionEvent.getData(PlatformDataKeys.PSI_ELEMENT);
        if (element == null) {
            return;
        }

        if (! (element instanceof PsiDirectory psiDir)) {
            return;
        }
        PythonDiagramParser parser = new PythonDiagramParser();

        File xmlFile = parser.parseDirectory(project, psiDir);
        if (xmlFile == null) {
            System.out.println("Error: no xml file provided");
            return;
        }

        VirtualFileManager.getInstance().syncRefresh();
        VfsUtil.markDirtyAndRefresh(false, true, false, xmlFile);

        VirtualFile vXMLFile = LocalFileSystem.getInstance().findFileByIoFile(xmlFile);
        openDiagramWindow(ClassDiagramEditor.class, project, vXMLFile, true);
    }

    @Override
    public void update(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        VirtualFile virtualFile = anActionEvent.getDataContext().getData(CommonDataKeys.VIRTUAL_FILE);
        if (virtualFile == null || project == null) {
            anActionEvent.getPresentation().setEnabledAndVisible(false);
            return;
        }

        boolean hasPlugin = false;
        for (IdeaPluginDescriptor pluginDescr : PluginManager.getPlugins()) {
            if (pluginDescr.getPluginId().getIdString().equals("Python") ||
                pluginDescr.getPluginId().getIdString().equals("Pythonid") ||
                pluginDescr.getPluginId().getIdString().equals("PythonCore")) {
                hasPlugin = true;
                break;
            }
        }
        String productCode = ApplicationInfo.getInstance().getBuild().getProductCode();
        if (!hasPlugin && !"PY".equals(productCode) && !"PC".equals(productCode)) {
            anActionEvent.getPresentation().setEnabledAndVisible(false);
            return;
        }

        PsiDirectory psiDirectory = Optional.of(PsiManager.getInstance(project)).map(fm ->
                fm.findDirectory(virtualFile)).orElse(null);

        if (psiDirectory == null || Arrays.stream(psiDirectory.getFiles()).noneMatch(file -> file.getName().endsWith(".py"))) {
            anActionEvent.getPresentation().setEnabledAndVisible(false);
            return;
        }

        // Check if the SDK is a Python SDK
        PythonSdkType pythonSdkType = PythonSdkType.getInstance();
        Module module = ModuleManager.getInstance(project).getModules()[0];
        Sdk projectSdk = PythonSdkUtil.findPythonSdk(module);

        if (projectSdk == null || !pythonSdkType.equals(projectSdk.getSdkType())) {
            // Python interpreter is not set up for the project
            anActionEvent.getPresentation().setEnabled(false);
            anActionEvent.getPresentation().setVisible(true);
            return;
        }

        anActionEvent.getPresentation().setEnabledAndVisible(true);
    }
}