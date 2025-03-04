package com.thesis.diagramplugin.rendering.kopenogram.treepainter;
import com.thesis.diagramplugin.rendering.kopenogram.treepainterElement.AbstractContainer;
import com.thesis.diagramplugin.rendering.kopenogram.treepainterElement.Bar;
import lombok.Getter;

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

public class RenderedElements {

    // Store elements by their name with position and dimension
    private static final Map<String, RenderedElementInfo> elements = new HashMap<>();

    // Method to add an element to the registry
    public static void addElement(String path, PainterElement element, Point pos, Dimension dim) {
        if(element instanceof Bar)
        elements.put(path, new RenderedElementInfo(element, pos, dim));
        //FIXME THIS MIGHT BE WRONG
    }

    // Method to retrieve an element from the registry
    public static RenderedElementInfo getElement(String path) {
        return elements.get(path);
    }

    // Method to get all elements
    public static Map<String, RenderedElementInfo> getAllElements() {
        return elements;
    }
}

