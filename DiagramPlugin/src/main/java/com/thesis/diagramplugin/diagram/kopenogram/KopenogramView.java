package com.thesis.diagramplugin.diagram.kopenogram;

import com.thesis.diagramplugin.diagram.DiagramView;
import lombok.Getter;

import javax.swing.*;

public class KopenogramView implements DiagramView {

    private final JComponent frame;
    @Getter
    private final String name;

    @Getter
    private final KopenogramXmlViewBuilder builder;

    public KopenogramView(String diagramXml) {
        builder = new KopenogramXmlViewBuilder(diagramXml);
        this.frame = builder.getView();
        this.name = builder.getName();
    }

    @Override
    public JComponent getView() {
        return this.frame;
    }


}
