package com.thesis.diagramplugin.rendering.kopenogram;


import com.thesis.diagramplugin.rendering.kopenogram.treepainter.PainterElement;
import com.thesis.diagramplugin.rendering.kopenogram.treepainter.PainterUtils;

import java.io.Serializable;

/**
 * hold note from tree and his element
 */
public class PaintedNode implements Serializable {

    private static final long serialVersionUID = 2228844183764551432L;
    private transient final Object node;
    private PainterElement element;

    public PaintedNode(Object node) {
        this.node = node;
    }

    public void setElement(PainterElement element) {
        PainterUtils.validateNotNull(element, "Element is null");
        this.element = element;
    }

    public Object getNode() {
        return node;
    }

    public PainterElement getElement() {
        if (element == null) {
            throw new IllegalStateException("Element must be set first");
        }
        return element;
    }
}