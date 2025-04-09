package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr;

import com.thesis.diagramplugin.rendering.classrelation.bluej.graph.*;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.UsesDependency;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target.Target;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

public class PackageSelectionController extends SelectionController {
    private UsesDependency selectedDependency;
    private Point selectedBendPoint;

    public PackageSelectionController(GraphEditor editor) {
        super(editor);
    }

    @Override
    public void mousePressed(MouseEvent evt)
    {
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
                for (Point bend : dep.getBendPoints()) {
                    if (bend.distance(evt.getPoint()) < 6) {
                        selectedDependency = dep;
                        selectedBendPoint = bend;
                        dep.setAutoLayout(false); // switch to manual mode
                        break;
                    }
                }
            }
        }
    }

//    @Override
//    public void mouseClicked(MouseEvent evt) {
//        POKUS O NOVOU VĚC, EXPERIMENT, KTERÝ SE NEVYDARIL
//        It should create a new bend point when double clicking
//        int x = evt.getX();
//        int y = evt.getY();
//
//        if (evt.getClickCount() == 2) {
//            SelectableGraphElement clicked = graph.findGraphElement(x, y);
//
//            if (clicked instanceof UsesDependency dep) {
//                dep.setAutoLayout(false); // make sure custom bends are used
//                // Check if clicked near an existing bend point
//                Point toRemove = null;
//                for (Point p : dep.getBendPoints()) {
//                    if (p.distance(x, y) < 6) {
//                        toRemove = p;
//                        break;
//                    }
//                }
//
//                if (toRemove != null) {
//                    dep.getBendPoints().remove(toRemove); // remove nearby point
//                } else {
//                    List<Point> bends = dep.getBendPoints();
//                    List<Point> allPoints = new ArrayList<>();
//                    allPoints.add(new Point(dep.getSourceX(), dep.getSourceY()));
//                    allPoints.addAll(bends);
//                    allPoints.add(new Point(dep.getDestX(), dep.getDestY()));
//                    int insertAt = -1;
//                    for (int i = 0; i < allPoints.size() - 1; i++) {
//                        Point a = allPoints.get(i);
//                        Point b = allPoints.get(i + 1);
//
//                        // Check if click is near the segment
//                        double dist = Line2D.ptSegDist(a.x, a.y, b.x, b.y, x, y);
//                        System.out.println(dist);
//                        if (dist < 6) {
//                            insertAt = i;
//                            break;
//                        }
//                    }
//
//                    if (insertAt != -1) {
//                        Point insert = new Point(snapToGrid(x), snapToGrid(y));
//                        // insert into actual bend list (offset by +1 because it starts after source)
//                        dep.getBendPoints().add(insert);
//                    }
//                }
//
//                graphEditor.repaint();
//            } else {
//
//            }
//        }
//    }

    /**
     * The mouse was released.
     */
    @Override
    public void mouseReleased(MouseEvent evt)
    {
        rubberBand = null;
        selectedDependency = null;
        selectedBendPoint = null;
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
            if (selectedBendPoint != null && selectedDependency != null) {
                List<Point> bends = selectedDependency.getBendPoints();
                if (bends.size() == 2) {
                    Point p1 = bends.get(0);
                    Point p2 = bends.get(1);

                    if (selectedBendPoint == p1) {
                        selectedBendPoint.setLocation(evt.getX(), evt.getY()); // horizontal
                    } else if (selectedBendPoint == p2) {
                        selectedBendPoint.setLocation(evt.getX(), evt.getY()); // vertical
                    }

                    graphEditor.repaint();
                }
            }
        }
    }

}
