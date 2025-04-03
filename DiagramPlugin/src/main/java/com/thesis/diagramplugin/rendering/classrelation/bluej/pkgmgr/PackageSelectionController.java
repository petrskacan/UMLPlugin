package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr;

import com.thesis.diagramplugin.rendering.classrelation.bluej.graph.*;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.Target;

import java.awt.*;
import java.awt.event.MouseEvent;

public class PackageSelectionController extends SelectionController {

    public PackageSelectionController(GraphEditor editor) {
        super(editor);
    }

    @Override
    public void mousePressed(MouseEvent evt)
    {
        System.out.println("CLICK START");
        graphEditor.requestFocus();
        int clickX = evt.getX();
        int clickY = evt.getY();

        SelectableGraphElement clickedElement = graph.findGraphElement(clickX, clickY);
        // notifyPackage(clickedElement);

        if (clickedElement == null) {                           // nothing hit
            if (!isMultiselectionKeyDown(evt)) {
                selection.clear();
            }
            if (isButtonOne(evt))
                marquee.start(clickX, clickY);
        }
        else if (isButtonOne(evt)) {                            // clicked on something
            if (isMultiselectionKeyDown(evt)) {
                // a class was clicked, while multiselectionKey was down.
                if (clickedElement.isSelected()) {
                    selection.remove(clickedElement);
                }
                else {
                    selection.add(clickedElement);
                }
            }
            else {
                // a class was clicked without multiselection
                if (! clickedElement.isSelected()) {
                    selection.selectOnly(clickedElement);
                }
            }

            if(isDrawingDependency(evt)) {
                if (clickedElement instanceof Target)
                    rubberBand = new RubberBand(clickX, clickY, clickX, clickY);
            }
            else {
                dragStartX = clickX;
                dragStartY = clickY;

                if(clickedElement.isHandle(clickX, clickY)) {
                    resizing = true;
                }
                else {
                    moving = true;
                }
            }
        }
        System.out.println("CLICK END");
    }

    /**
     * The mouse was released.
     */
    @Override
    public void mouseReleased(MouseEvent evt)
    {
        rubberBand = null;

        SelectionSet newSelection = marquee.stop();     // may or may not have had a marquee...
        if(newSelection != null) {
            selection.addAll(newSelection);
            graphEditor.repaint();
        }

        if(moving || resizing) {
            endMove();
            graphEditor.revalidate();
            graphEditor.repaint();
        }
    }

    @Override
    public void mouseDragged(MouseEvent evt)
    {
        if (isButtonOne(evt)) {
            if (marquee.isActive()) {
                Rectangle oldRect = marquee.getRectangle();
                marquee.move(evt.getX(), evt.getY());
                Rectangle newRect = (Rectangle) marquee.getRectangle().clone();
                if(oldRect != null) {
                    newRect.add(oldRect);
                }
                newRect.width++;
                newRect.height++;
                graphEditor.repaint(newRect);
            }
            else if (rubberBand != null) {
                rubberBand.setEnd(evt.getX(), evt.getY());
                graphEditor.repaint();
            }
            else
            {
                if(! selection.isEmpty()) {
                    int deltaX = snapToGrid(evt.getX() - dragStartX);
                    int deltaY = snapToGrid(evt.getY() - dragStartY);

                    if(resizing) {
                        selection.resize(deltaX, deltaY);
                    }
                    else if (moving) {
                        selection.move(deltaX, deltaY);
                    }
                }
                graphEditor.repaint();
            }
        }
    }

}
