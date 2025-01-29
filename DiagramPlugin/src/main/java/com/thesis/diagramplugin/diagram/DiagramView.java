package com.thesis.diagramplugin.diagram;


import javax.swing.*;

public interface DiagramView {

    public JComponent getView();

    default void save() {};

    public String getName();

}
