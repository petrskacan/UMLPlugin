package com.thesis.diagramplugin.rendering.kopenogram.treepainter;
import com.thesis.diagramplugin.rendering.kopenogram.PaintedNode;

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

public class RenderedElements {

    // Store elements by their name with position and dimension
    private static final Map<String, RenderedElementInfo> elements = new HashMap<>();

    // Nested class to hold the rendering info
    public static class RenderedElementInfo {
        public final PainterElement element;
        public final Point position;
        public final Dimension dimension;

        public RenderedElementInfo(PainterElement element, Point position, Dimension dimension) {
            this.element = element;
            this.position = position;
            this.dimension = dimension;
        }

        @Override
        public String toString() {
            return "RenderedElementInfo{" +
                    "element=" + element +
                    ", position=" + position +
                    ", dimension=" + dimension +
                    '}';
        }
    }

    // Method to add an element to the registry
    public static void addElement(String path, PainterElement element, Point pos, Dimension dim) {
        elements.put(path, new RenderedElementInfo(element, pos, dim));
        System.out.println("Added element to registry: " + path);
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

