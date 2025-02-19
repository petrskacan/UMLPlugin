package com.thesis.diagramplugin.rendering.kopenogram.treepainterElement;

import com.thesis.diagramplugin.rendering.kopenogram.treepainter.PainterElement;

import java.util.List;

public interface Container extends PainterElement {

    public Container addChild(PainterElement element);

    public List<PainterElement> getChildren();

}