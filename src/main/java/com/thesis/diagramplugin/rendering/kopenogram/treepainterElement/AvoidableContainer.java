package com.thesis.diagramplugin.rendering.kopenogram.treepainterElement;


import com.thesis.diagramplugin.rendering.kopenogram.treepainter.OverPainterElement;

import java.util.List;

public interface AvoidableContainer extends Container {

    public void setAvoidElements(List<? extends OverPainterElement> avoid);
}
