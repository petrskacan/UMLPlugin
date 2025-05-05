package com.thesis.diagramplugin.plugin.actions.java;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;
import com.thesis.diagramplugin.parser.JavaDiagramParser;
import com.thesis.diagramplugin.plugin.editors.ClassDiagramEditor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

import static com.thesis.diagramplugin.utils.DiagramConstants.XML_FILE_NAME_POSTFIX_CLASS_DIAGRAM;

public class GenerateClassDiagramAction extends ADiagramAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

        FileDocumentManager.getInstance().saveAllDocuments();

        Project project = anActionEvent.getProject();
        if (project == null) return;
        PsiElement element = anActionEvent.getData(PlatformDataKeys.PSI_ELEMENT);
        if (element == null) {
            return;
        }

        String dir = this.getDirectory(element).getVirtualFile().getPath();

        XmlFile diagramXml = null;
        for (PsiElement child : element.getChildren()) {
            if (child instanceof XmlFile) {
                XmlFile xml = (XmlFile) child;
                String name = xml.getName();
                if (name.endsWith(XML_FILE_NAME_POSTFIX_CLASS_DIAGRAM)) {
                    diagramXml = xml;
                    break;
                }
            }
        }
        if (diagramXml != null) {
            VirtualFile vFile = diagramXml.getVirtualFile();
            FileEditorManager.getInstance(project).closeFile(vFile);
        }

        JavaDiagramParser parser = new JavaDiagramParser();
        File xmlFile = parser.parseDirectory(dir);
        if (xmlFile == null) {
            System.out.println("Error: no xml file provided");
            return;
        }

        VirtualFile vXMLFile = LocalFileSystem.getInstance().findFileByIoFile(xmlFile);
        VfsUtil.markDirtyAndRefresh(false, true, true, project.getBaseDir());
        VfsUtil.markDirtyAndRefresh(false, true, true, this.getDirectory(element).getVirtualFile());
        VirtualFileManager.getInstance().syncRefresh();

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
