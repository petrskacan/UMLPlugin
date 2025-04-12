package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr;

import com.thesis.diagramplugin.rendering.classrelation.bluej.graph.*;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.BendPoint;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.UsesDependency;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.Target;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

public class PackageSelectionController extends SelectionController {
    public PackageSelectionController(GraphEditor editor) {
        super(editor);
    }

    @Override
    public void mousePressed(MouseEvent evt)
    {
        selectedBendPoint = null;
        selectedDependency = null;
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
            if (clickedElement instanceof UsesDependency dep) {
                selectedDependency = dep;
                dep.setAutoLayout(false);
                for (BendPoint bend : dep.getBendPoints()) {
                    if (bend.distance(evt.getPoint()) < 6) {
                        selectedBendPoint = bend;
                        selectedBendPoint.setSelected(true);
                        break;
                    }
                }
            }
        }
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

        if(selectedBendPoint != null)
        selectedBendPoint.setSelected(false);
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
            if (selectedBendPoint != null || selectedDependency != null) {
                if (selectedBendPoint == null) {
                    insertPoint(evt.getX(), evt.getY());
                }
                selectedBendPoint.setLocation(evt.getX(), evt.getY());
                ((Package)super.graph).recalcArrows();
                graphEditor.repaint();
            }

        }
    }

    private void insertPoint(int x, int y)
    {
        // 1. Zjisti celkovou trasu: start → bends → end
        List<BendPoint> bends = selectedDependency.getBendPoints();
        List<Point> allPoints = new ArrayList<>();
        allPoints.add(new Point(selectedDependency.getSourceX(), selectedDependency.getSourceY()));
        allPoints.addAll(bends);
        allPoints.add(new Point(selectedDependency.getDestX(), selectedDependency.getDestY()));

        BendPoint newBendPoint = new BendPoint(x,y);
        double closestDist = Double.MAX_VALUE;
        int insertIndex = bends.size();

        for (int i = 0; i < allPoints.size() - 1; i++) {
            Point a = allPoints.get(i);
            Point b = allPoints.get(i + 1);
            double dist = Line2D.ptSegDist(a.x, a.y, b.x, b.y, newBendPoint.x, newBendPoint.y);
            if (dist < closestDist) {
                closestDist = dist;
                insertIndex = i;
            }
        }

        selectedDependency.getBendPoints().add(insertIndex, newBendPoint);
        selectedBendPoint = newBendPoint;

    }

}
