package com.thesis.diagramplugin.rendering.kopenogram.treepainterElement;


import com.thesis.diagramplugin.rendering.kopenogram.treepainter.AbstractPainterElement;
import com.thesis.diagramplugin.rendering.kopenogram.treepainter.PainterElement;
import com.thesis.diagramplugin.rendering.kopenogram.treepainter.PainterUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract container contains other elements
 */
public abstract class AbstractContainer extends AbstractPainterElement implements Container {

    private static final long serialVersionUID = 1710297027352533372L;
    private final List<PainterElement> children = new ArrayList<PainterElement>();

    @Override
    public AbstractContainer addChild(PainterElement element) {
        if (children.isEmpty() && (element instanceof Separator)) {
            throw new RuntimeException("Separator in empty children");
        }
        if (!children.isEmpty() && (element instanceof Empty)) {
            throw new RuntimeException("Some children and empty");
        }
        if (!children.isEmpty() && (children.get(0) instanceof Empty)) {
            throw new RuntimeException("Already empty");
        }
        PainterUtils.validateNotNull(element, "Child must not be null");
        children.add(element);
        return this;
    }

    @Override
    public List<PainterElement> getChildren() {
        if (!children.isEmpty() && (children.get(0) instanceof Separator)) {
            throw new RuntimeException("First separator");
        }
        if (!children.isEmpty() && (children.get(children.size() - 1)) instanceof Separator) {
            throw new RuntimeException("Last separator");
        }
        return children;
    }
}
