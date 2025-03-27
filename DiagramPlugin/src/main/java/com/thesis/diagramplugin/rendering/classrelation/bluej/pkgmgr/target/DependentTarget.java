/*
 This file is part of the BlueJ program.
 Copyright (C) 1999-2009,2012  Michael Kolling and John Rosenberg

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

 This file is subject to the Classpath exception as provided in the
 LICENSE.txt file that accompanied this code.
 */
package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.target;

import com.thesis.diagramplugin.rendering.classrelation.bluej.MultiIterator;
import com.thesis.diagramplugin.rendering.classrelation.bluej.graph.Moveable;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.Package;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.Dependency;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.ExtendsDependency;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.ImplementsDependency;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.UsesDependency;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.List;
import java.util.*;


/**
 * A target that has relationships to other targets
 *
 * @author   Michael Cahill
 * @author   Michael Kolling
 */
public abstract class DependentTarget extends Target
{
    private List<UsesDependency> inUses;
    private List<UsesDependency> outUses;
    private List<Dependency> parents;
    private List<Dependency> children;

    @Getter
    @Setter
    private DependentTarget containerTarget;

    protected DependentTarget assoc;

    /**
     * Create a new target belonging to the specified package.
     */
    public DependentTarget(Package pkg, String identifierName)
    {
        super(pkg, identifierName);

        inUses = new ArrayList<UsesDependency>();
        outUses = new ArrayList<UsesDependency>();
        parents = new ArrayList<Dependency>();
        children = new ArrayList<Dependency>();

        assoc = null;
    }

    @Override
    public void setPos(int x, int y)
    {
        super.setPos(x,y);
        recalcDependentPositions();
    }

    @Override
    public void setSize(int width, int height)
    {
        super.setSize(width, height);
        recalcDependentPositions();
    }

    /**
     * Save association information about this class target
     * @param props the properties object to save to
     * @param prefix an internal name used for this target to identify
     */
    @Override
    public void save(Properties props, String prefix)
    {
        super.save(props, prefix);

        if (getAssociation() != null) {
            String assocName = getAssociation().getIdentifierName();
            props.put(prefix + ".association", assocName);
        }
    }

    public void setAssociation(DependentTarget t)
    {
        assoc = t;
        //assoiated classes are not allowed to move on their own
        if (assoc instanceof Moveable){
            ((Moveable)assoc).setIsMoveable(false);
        }
    }

    public DependentTarget getAssociation()
    {
        return assoc;
    }

    public void addDependencyOut(Dependency d, boolean recalc)
    {
        if(d instanceof UsesDependency) {
            outUses.add((UsesDependency) d);
            if(recalc)
                recalcOutUses();
        }
        else if((d instanceof ExtendsDependency)
                || (d instanceof ImplementsDependency)) {
            parents.add(d);
        }
    }

    public void addDependencyIn(Dependency d, boolean recalc)
    {
        if(d instanceof UsesDependency) {
            inUses.add((UsesDependency) d);
            if(recalc)
                recalcInUses();
        }
        else if((d instanceof ExtendsDependency)
                || (d instanceof ImplementsDependency)) {
            children.add(d);
        }
    }

    public Iterator<? extends Dependency> dependencies()
    {
        List<Iterator<? extends Dependency>> v = new ArrayList<Iterator<? extends Dependency>>(2);
        v.add(parents.iterator());
        v.add(outUses.iterator());
        return new MultiIterator<Dependency>(v);
    }

    public Iterator<? extends Dependency> dependents()
    {
        List<Iterator<? extends Dependency>> v = new ArrayList<Iterator<? extends Dependency>>(2);
        v.add(children.iterator());
        v.add(inUses.iterator());
        return new MultiIterator<Dependency>(v);
    }

    /**
     * Get the dependencies between this target and its parent(s).
     * The returned list should not be modified and may be a view or a copy.
     */
    public List<Dependency> getParents()
    {
        return Collections.unmodifiableList(parents);
    }

    /**
     * Get the dependencies between this target and its children.
     *
     * @return
     */
    public List<Dependency> getChildren()
    {
        return Collections.unmodifiableList(children);
    }

    public List<Dependency> dependentsAsList()
    {
        List<Dependency> list = new LinkedList<Dependency>();
        list.addAll(inUses);
        list.addAll(outUses);
        list.addAll(children);
        list.addAll(parents);
        return list;
    }

    public Iterator<UsesDependency> usesDependencies()
    {
        return Collections.unmodifiableList(outUses).iterator();
    }

    public void recalcOutUses() {
        List<UsesDependency> visibleOutUses = getVisibleUsesDependencies(outUses);
        recalcUses(visibleOutUses, true);
    }
    public void recalcInUses() {
        List<UsesDependency> visibleInUses = getVisibleUsesDependencies(inUses);
        recalcUses(visibleInUses, false);
    }

    private void recalcUses(List<UsesDependency> visibleUses, boolean isOutgoing)
    {
        int offset = isOutgoing ? 0 : 4;
        Rectangle currentRect = new Rectangle(getX(), getY(), getWidth(), getHeight());
        Target target;

        // Group dependencies by the best side to connect.
        Map<ConnectionSide, List<UsesDependency>> groups = new EnumMap<>(ConnectionSide.class);
        // Initialize groups.
        for (ConnectionSide side : ConnectionSide.values()) {
            groups.put(side, new ArrayList<>());
        }

        for (UsesDependency d : visibleUses) {
            if(isOutgoing)
            {
                target = d.getTo();
                ConnectionSide[] best = determineBestConnection(currentRect, getSecondRectangle(target));
                ConnectionSide bestSourceSide = best[0];
                d.setStartConnectionSide(best[1]);
                groups.get(bestSourceSide).add(d);
            }
            else
            {
                target = d.getFrom();
                ConnectionSide bestSide = determineBestConnection(getSecondRectangle(target), currentRect)[1];
                d.setEndConnectionSide(bestSide);
                groups.get(bestSide).add(d);
            }

        }

        for (Map.Entry<ConnectionSide, List<UsesDependency>> entry : groups.entrySet()) {
            ConnectionSide side = entry.getKey();
            List<UsesDependency> group = entry.getValue();
            int count = group.size();
            if (count == 0) continue;

            switch (side) {
                case TOP -> {
                    int startX = getX() + (getWidth() - (count - 1) * ARR_HORIZ_DIST) / 2;
                    for (UsesDependency d : group) {
                        d.setCoord(startX, getY() - offset, ConnectionSide.TOP, isOutgoing);
                        startX += ARR_HORIZ_DIST;
                    }
                }
                case BOTTOM -> {
                    int startX = getX() + (getWidth() - (count - 1) * ARR_HORIZ_DIST) / 2;
                    for (UsesDependency d : group) {
                        d.setCoord(startX, getY() + getHeight() + offset, ConnectionSide.BOTTOM, isOutgoing);
                        startX += ARR_HORIZ_DIST;
                    }
                }
                case LEFT -> {
                    int startY = getY() + (getHeight() - (count - 1) * ARR_VERT_DIST) / 2;
                    for (UsesDependency d : group) {
                        d.setCoord(getX() - offset, startY, ConnectionSide.LEFT, isOutgoing);
                        startY += ARR_VERT_DIST;
                    }
                }
                case RIGHT -> {
                    int startY = getY() + (getHeight() - (count - 1) * ARR_VERT_DIST) / 2;
                    for (UsesDependency d : group) {
                        d.setCoord(getX() + getWidth() + offset, startY, ConnectionSide.RIGHT, isOutgoing);
                        startY += ARR_VERT_DIST;
                    }
                }
            }
        }
    }
    public Point getConnectionPoint(Rectangle rect, ConnectionSide side) {
        return switch (side) {
            case TOP -> new Point(rect.x + rect.width / 2, rect.y);
            case BOTTOM -> new Point(rect.x + rect.width / 2, rect.y + rect.height);
            case LEFT -> new Point(rect.x, rect.y + rect.height / 2);
            case RIGHT -> new Point(rect.x + rect.width, rect.y + rect.height / 2);
        };
    }
    public ConnectionSide[] determineBestConnection(Rectangle source, Rectangle target) {
        ConnectionSide bestSourceSide = null;
        ConnectionSide bestTargetSide = null;
        double bestDistance = Double.MAX_VALUE;
        if(source.getX() == target.getX() && source.getY() == target.getY())
        {
            return new ConnectionSide[]{ConnectionSide.TOP, ConnectionSide.RIGHT};
        }
        for (ConnectionSide srcSide : ConnectionSide.values()) {
            for (ConnectionSide tgtSide : ConnectionSide.values()) {
                Point pSrc = getConnectionPoint(source, srcSide);
                Point pTgt = getConnectionPoint(target, tgtSide);
                double distance = pSrc.distance(pTgt);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestSourceSide = srcSide;
                    bestTargetSide = tgtSide;
                }
            }
        }

        return new ConnectionSide[]{bestSourceSide, bestTargetSide};
    }
    private static Rectangle getSecondRectangle(Target target) {
        return new Rectangle(target.getX(), target.getY(), target.getWidth(), target.getHeight());
    }

    /**
     * Returns from the specified {@link List} all uses dependencies which are
     * currently visible.
     *
     * @param usesDependencies
     *            A {@link List} of uses dependencies.
     * @return A {@link List} containing all visible uses dependencies from the
     *         input list.
     */
    private List<UsesDependency> getVisibleUsesDependencies(List<UsesDependency> usesDependencies)
    {
        //~+ SIMJ08
        List<UsesDependency> result = new ArrayList<>();
        //~- SIMJ08

        for (UsesDependency incomingUsesDependency : usesDependencies) {
            if (incomingUsesDependency.isVisible()) {
                result.add(incomingUsesDependency);
            }
        }

        return result;
    }

    /**
     *  Clear the flag in a outgoing uses dependencies
     */
    protected void unflagAllOutDependencies()
    {
        for(int i = 0; i < outUses.size(); i++)
        {
        //~+ SIMJ08
            outUses.get(i).setFlag(false);
        //~- SIMJ08
        }
    }

    public Point getAttachment(double angle)
    {
        double radius;
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        double tan = sin / cos;
        double m = (double) getHeight() / getWidth();

        if(Math.abs(tan) < m)   // side
            radius = 0.5 * getWidth() / Math.abs(cos);
        else    // top
            radius = 0.5 * getHeight() / Math.abs(sin);

        Point p = new Point(getX() + getWidth() / 2 + (int)(radius * cos),
                            getY() + getHeight() / 2 - (int)(radius * sin));

        // Correct for shadow
        if((-m < tan) && (tan < m) && (cos > 0))    // right side
            p.x += SHAD_SIZE;
        if((Math.abs(tan) > m) && (sin < 0) && (p.x > getX() + SHAD_SIZE))  // bottom
            p.y += SHAD_SIZE;

        return p;
    }


    /**
     * The user may have moved or resized the target. If so, recalculate the
     * dependency arrows associated with this target.
     * @param
     */
    public void recalcDependentPositions()
    {
        // Recalculate arrows
        recalcInUses();
        recalcOutUses();

        // Recalculate neighbours' arrows
        for(Iterator<UsesDependency> it = inUses.iterator(); it.hasNext(); ) {
            Dependency d = it.next();
            d.getFrom().recalcOutUses();
        }
        for(Iterator<UsesDependency> it = outUses.iterator(); it.hasNext(); ) {
            Dependency d = it.next();
            d.getTo().recalcInUses();
        }

        updateAssociatePosition();
    }

    protected void updateAssociatePosition()
    {
        DependentTarget t = getAssociation();

        if (t != null) {
            //TODO magic numbers. Should also take grid size in to account.
            t.setPos(getX() + 30, getY() - 30);
            t. recalcDependentPositions();
        }
    }
}
