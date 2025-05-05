package com.thesis.diagramplugin.plugin.editors;

import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.thesis.diagramplugin.diagram.kopenogram.KopenogramView;
import com.thesis.diagramplugin.diagram.kopenogram.KopenogramXmlViewBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class KopenogramEditor extends DiagramEditor {

    private KopenogramView diagramView;

    public KopenogramEditor(VirtualFile xmlFile) {
        super(xmlFile);
    }

    @Override
    public void init() throws IOException {
        diagramView = new KopenogramView(new String(Files.readAllBytes(Paths.get(xmlFile.getPath()))));
        name = diagramView.getName() + ": Kopenogram";
        xmlFile.putUserData(TITLE, name);
    }

    @Override
    public void dispose() {
        if (diagramView != null) {
            diagramView.save();
        }
        Disposer.dispose(this);
    }

    @Override
    public @NotNull JComponent getComponent() {
        return this.diagramView.getView();
    }

    public KopenogramXmlViewBuilder getBuilder() {
        return diagramView.getBuilder();
    }
}
