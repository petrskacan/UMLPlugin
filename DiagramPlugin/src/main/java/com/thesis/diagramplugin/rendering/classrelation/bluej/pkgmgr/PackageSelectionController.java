package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr;

import com.thesis.diagramplugin.rendering.classrelation.bluej.graph.*;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.BendPoint;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.UsesDependency;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.graphPainter.LineStyle;
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

                if(GraphEditor.getLineStyle() == LineStyle.ORTHOGONAL)
                {
                    List<Point> allPoints = new ArrayList<>(dep.getBendPoints());

                    Point click = evt.getPoint();
                    for (int i = 0; i < allPoints.size() - 1; i++) {
                        Point a = allPoints.get(i);
                        Point b = allPoints.get(i + 1);
                        double dist = Line2D.ptSegDist(a.x, a.y, b.x, b.y, click.x, click.y);

                        if (dist < 6) { // tolerance kliknutí na segment
                            selectedDependency = dep;
                            selectedSegmentStart = a;
                            selectedSegmentEnd = b;
                            draggingSegment = true;
                            dep.setAutoLayout(false);
                            break;
                        }
                    }
                }
                else {
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
    }


    /**
     * The mouse was released.
     */
    @Override
    public void mouseReleased(MouseEvent evt)
    {
        if(selectedDependency != null) {
            selectedDependency.removeRedundantPoints();
            selectedDependency.repaint();
        }
        rubberBand = null;
        draggingSegment = false;
        selectedSegmentStart = null;
        selectedSegmentEnd = null;
        SelectionSet newSelection = marquee.stop();     // may or may not have had a marquee...
        if(newSelection != null) {
            selection.addAll(newSelection);
            graphEditor.repaint();
        }

        if(moving || resizing) {
            System.out.println("DONE MOVING FIRST TIME");
            endMove();
            ((Package)super.graph).recalcArrows();
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
            if (selectedBendPoint != null || selectedDependency != null || selectedSegmentStart != null ) {
                if (selectedSegmentStart != null && selectedDependency != null && selectedSegmentEnd != null) {
                    if(selectedDependency.from == selectedDependency.to)
                    {
                        return;
                    }
//                    GhostPainter ghost = new GhostPainter(selectedDependency);
                    // Calculate the move delta.
                    int dx = evt.getX() - selectedSegmentStart.x;
                    int dy = evt.getY() - selectedSegmentStart.y;
                    int indexStart = selectedDependency.getBendPoints().indexOf(selectedSegmentStart);
                    int indexEnd = selectedDependency.getBendPoints().indexOf(selectedSegmentEnd);
                    if(indexEnd == selectedDependency.getBendPoints().size() - 1 || indexEnd == 0 || indexStart == selectedDependency.getBendPoints().size() - 1 || indexStart == 0)
                    {
                        return;
                    }

                    // Adjust only if the segment is perfectly horizontal or vertical.
                    if (selectedSegmentStart.y == selectedSegmentEnd.y) {
                        selectedSegmentStart.translate(0,dy);
                        selectedSegmentEnd.translate(0,dy);
                    } else if (selectedSegmentStart.x == selectedSegmentEnd.x) {
                        selectedSegmentStart.translate(dx,0);
                        selectedSegmentEnd.translate(dx, 0);
                    }
//                    if(indexStart == 0 || indexStart == selectedDependency.getBendPoints().size() - 1)
//                    {
//                        selectedDependency.getBendPoints().add(indexStart, new BendPoint(selectedDependency.getRecalcStart()));
//                    } else if (indexEnd == selectedDependency.getBendPoints().size() - 1 || indexEnd == 0 ) {
//                        selectedDependency.getBendPoints().add(indexEnd, new BendPoint(selectedDependency.getRecalcStart()));
//                    }

//                    // After manual adjustment, extract the manually updated bend points.
//                    // (Depending on your design, you might store these in a separate list.)
//                    // For this example, assume the whole bend point list reflects manual adjustments.
//                    List<BendPoint> manualBends = selectedDependency.getBendPoints();
//
//                    // Get adjusted start and end points used in routing (these may have been
//                    // calculated with the offsets already).
//                    Point adjustedStart = selectedDependency.getRecalcStart();
//                    Point adjustedEnd = selectedDependency.getRecalcEnd();
//
//                    // Merge the manual adjustments into the routing.
//                    updateRoutingWithManualAdjustments(selectedDependency, (BendPoint) adjustedStart, (BendPoint) adjustedEnd, manualBends);

                    // Repaint the updated diagram.
                    ((Package)super.graph).recalcArrows();
                    graphEditor.repaint();
                    return;
                }
                if (selectedBendPoint == null) {
                    insertPoint(evt.getX(), evt.getY());
                }
                selectedBendPoint.setLocation(evt.getX(), evt.getY());
                ((Package)super.graph).recalcArrows();
                graphEditor.repaint();
            }

        }
    }

    /**
     * Check if three points are collinear (all horizontal or vertical).
     */
    private boolean isCollinear(Point a, Point b, Point c) {
        return (a.x == b.x && b.x == c.x) || (a.y == b.y && b.y == c.y);
    }

    /**
     * Removes redundant bend points from a list.
     */
    private void removeRedundantBendPoints(List<BendPoint> bendPoints) {
        for (int i = 1; i < bendPoints.size() - 1; ) {
            BendPoint prev = bendPoints.get(i - 1);
            BendPoint current = bendPoints.get(i);
            BendPoint next = bendPoints.get(i + 1);
            if (isCollinear(prev, current, next)) {
                bendPoints.remove(i);
            } else {
                i++;
            }
        }
    }

    /**
     * Updates the routing of a dependency with manual adjustments.
     *
     * @param dependency The dependency whose route is being updated.
     * @param startPoint The adjusted start point (with offset).
     * @param endPoint The adjusted end point (with offset).
     * @param manualBends A possibly empty list of manually adjusted bend points.
     *                    (For example, if the user moved a segment, those points are included here.)
     */
    private void updateRoutingWithManualAdjustments(UsesDependency dependency,
                                                    BendPoint startPoint,
                                                    BendPoint endPoint,
                                                    List<BendPoint> manualBends) {
        // Compute the base route using Manhattan routing.
        List<BendPoint> autoBends = computeOrthogonalBendPoints(startPoint, endPoint);

        // Merge manual adjustments:
        // For demonstration, assume if a manual bend exists at the same index as one auto bend,
        // then it replaces the auto version.
        List<BendPoint> mergedBends = new ArrayList<>(autoBends);

        if (manualBends != null && !manualBends.isEmpty()) {
            // Here we assume the number of autoBends and manualBends are the same,
            // but you may add more sophisticated merging depending on your needs.
            for (int i = 0; i < Math.min(mergedBends.size(), manualBends.size()); i++) {
                // For example, if the manual bend was "locked in" (or simply exists), use it:
                mergedBends.set(i, manualBends.get(i));
            }
        }

        // Clean up any redundant bend points.
        removeRedundantBendPoints(mergedBends);

        // Update the dependency’s bend points with the merged route.
        dependency.getBendPoints().addAll(mergedBends);
    }

    /**
     * Computes two orthogonal bend points given start and end points.
     */
    private List<BendPoint> computeOrthogonalBendPoints(BendPoint start, BendPoint end) {
        List<BendPoint> bends = new ArrayList<>();
        int dx = end.x - start.x;
        int dy = end.y - start.y;

        if (Math.abs(dx) > Math.abs(dy)) {
            // For horizontal-first routing:
            int midX = start.x + dx / 2;
            bends.add(new BendPoint(midX, start.y));
            bends.add(new BendPoint(midX, end.y));
        } else {
            // For vertical-first routing:
            int midY = start.y + dy / 2;
            bends.add(new BendPoint(start.x, midY));
            bends.add(new BendPoint(end.x, midY));
        }
        return bends;
    }





    private boolean isBetween(Point bend, Point a, Point b) {
        return (bend.equals(a) || bend.equals(b) ||
                (Math.min(a.x, b.x) <= bend.x && bend.x <= Math.max(a.x, b.x) &&
                        Math.min(a.y, b.y) <= bend.y && bend.y <= Math.max(a.y, b.y)));
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
