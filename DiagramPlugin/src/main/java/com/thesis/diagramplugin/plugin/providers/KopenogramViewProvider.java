package com.thesis.diagramplugin.plugin.providers;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.thesis.diagramplugin.plugin.editors.DiagramEditor;
import com.thesis.diagramplugin.plugin.editors.KopenogramEditor;
import com.thesis.diagramplugin.utils.DiagramConstants;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;

public class KopenogramViewProvider implements FileEditorProvider, DumbAware {

    private static final String EDITOR_TYPE_ID = "KopenogramView";

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        return file.getName().endsWith(DiagramConstants.XML_FILE_NAME_POSTFIX_KOPENOGRAM);
    }

    @Override
    public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        FileEditor[] openedEditors = FileEditorManager.getInstance(project).getEditors(file);

        KopenogramEditor editor = null;
        try {
            editor = Arrays.stream(openedEditors).filter(KopenogramEditor.class::isInstance).findFirst().map(KopenogramEditor.class::cast)
                    .orElse(new KopenogramEditor(file));
            file.refresh(false, true);
            editor.init();
            return editor;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return editor;
    }

    @Override
    public @NotNull @NonNls String getEditorTypeId() {
        return EDITOR_TYPE_ID;
    }

    @Override
    public @NotNull FileEditorPolicy getPolicy() {
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
    }
}
