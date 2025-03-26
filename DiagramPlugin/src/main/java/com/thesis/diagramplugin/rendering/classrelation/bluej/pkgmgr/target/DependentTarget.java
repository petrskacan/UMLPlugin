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

import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.Package;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.ExtendsDependency;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.ImplementsDependency;
import com.thesis.diagramplugin.rendering.classrelation.bluej.MultiIterator;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.LayoutComparer;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.Dependency;
import com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr.dependency.UsesDependency;
import com.thesis.diagramplugin.rendering.classrelation.bluej.graph.Moveable;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.*;
import java.util.List;


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

//    public void recalcOutUses()
//    {
//        // Determine the visible outgoing uses dependencies
//        List<UsesDependency> visibleOutUses = getVisibleUsesDependencies(outUses);
//
//        // Order the arrows by quadrant and then appropriate coordinate
//        Collections.sort(visibleOutUses, new LayoutComparer(this, false));
//
//        // Count the number of arrows into each quadrant
//        int cy = getY() + getHeight() / 2;
//        int n_top = 0, n_bottom = 0;
//        for(int i = visibleOutUses.size() - 1; i >= 0; i--)
//        {
//            //~+ SIMJ08
//            Target to = visibleOutUses.get(i).getTo();
//            //~- SIMJ08
//            int to_cy = to.getY() + to.getHeight() / 2;
//            if(to_cy < cy)
//                ++n_top;
//            else
//                ++n_bottom;
//        }
//
//        // Assign source coordinates to each arrow
//        int top_left = getX() + (getWidth() - (n_top - 1) * ARR_HORIZ_DIST) / 2;
//        int bottom_left = getX() + (getWidth() - (n_bottom - 1) * ARR_HORIZ_DIST) / 2;
//        for(int i = 0; i < n_top + n_bottom; i++)
//        {
//            //~+ SIMJ08
//            UsesDependency d = visibleOutUses.get(i);
//            //~- SIMJ08
//            int to_cy = d.getTo().getY() + d.getTo().getHeight() / 2;
//            if(to_cy < cy) {
//                d.setSourceCoords(top_left, getY() - 4, true);
//                top_left += ARR_HORIZ_DIST;
//            }
//            else {
//                d.setSourceCoords(bottom_left, getY() + getHeight() + 4, false);
//                bottom_left += ARR_HORIZ_DIST;
//            }
//        }
//    }
    public void recalcOutUses() {
        // Získáme viditelné závislosti
        List<UsesDependency> visibleOutUses = getVisibleUsesDependencies(outUses);
        Rectangle sourceRect = new Rectangle(getX(), getY(), getWidth(), getHeight());

        // Vytvoříme mapu pro seskupení závislostí dle vybraného okraje zdroje
        Map<ConnectionSide, List<UsesDependency>> groups = new EnumMap<>(ConnectionSide.class);
        for (ConnectionSide side : ConnectionSide.values()) {
            groups.put(side, new ArrayList<>());
        }

        // Pro každou závislost určíme nejlepší okraj spojení
        for (UsesDependency d : visibleOutUses) {
            Target target = d.getTo();
            Rectangle targetRect = new Rectangle(target.getX(), target.getY(), target.getWidth(), target.getHeight());
            ConnectionSide[] best = determineBestConnection(sourceRect, targetRect);
            ConnectionSide bestSourceSide = best[0];
            // Můžete také uložit cílový okraj, pokud je to potřeba:
            // d.setTargetSide(best[1]);
            groups.get(bestSourceSide).add(d);
        }

        // Pro každou skupinu rozložíme spojovací body
        // Předpokládejme, že máme definované konstanty ARR_HORIZ_DIST a ARR_VERT_DIST
        for (Map.Entry<ConnectionSide, List<UsesDependency>> entry : groups.entrySet()) {
            ConnectionSide side = entry.getKey();
            List<UsesDependency> group = entry.getValue();
            int count = group.size();
            if (count == 0) continue;

            switch (side) {
                case TOP: {
                    // Rozložení horizontálně podél horní hrany
                    int startX = getX() + (getWidth() - (count - 1) * ARR_HORIZ_DIST) / 2;
                    for (UsesDependency d : group) {
                        d.setSourceCoords(startX, getY(), ConnectionSide.TOP); // boolean parametr může indikovat "horní" okraj
                        startX += ARR_HORIZ_DIST;
                    }
                    break;
                }
                case BOTTOM: {
                    int startX = getX() + (getWidth() - (count - 1) * ARR_HORIZ_DIST) / 2;
                    for (UsesDependency d : group) {
                        d.setSourceCoords(startX, getY() + getHeight(), ConnectionSide.BOTTOM);
                        startX += ARR_HORIZ_DIST;
                    }
                    break;
                }
                case LEFT: {
                    // Rozložení vertikálně podél levé hrany
                    int startY = getY() + getHeight()  / 2;
                    for (UsesDependency d : group) {
                        d.setSourceCoords(getX(), startY, ConnectionSide.LEFT);
                        startY += ARR_VERT_DIST;
                    }
                    break;
                }
                case RIGHT: {
                    int startY = getY() + (getHeight()) / 2;
                    for (UsesDependency d : group) {
                        d.setSourceCoords(getX() + getWidth(), startY, ConnectionSide.RIGHT);
                        startY += ARR_VERT_DIST;
                    }
                    break;
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

    // Vyhodnotí všechny kombinace a vrátí dvojici [nejlepší zdrojový okraj, nejvhodnější cílový okraj
    public ConnectionSide[] determineBestConnection(Rectangle source, Rectangle target) {
        ConnectionSide bestSourceSide = null;
        ConnectionSide bestTargetSide = null;
        double bestDistance = Double.MAX_VALUE;

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

    /**
     * Re-layout arrows into this target
     */
//    public void recalcInUses()
//    {
//        // Determine the visible incoming uses dependencies
//        List<UsesDependency> visibleInUses = getVisibleUsesDependencies(inUses);
//
//        // Order the arrows by quadrant and then appropriate coordinate
//        Collections.sort(visibleInUses, new LayoutComparer(this, true));
//
//        // Count the number of arrows into each quadrant
//        int cx = getX() + getWidth() / 2;
//        int n_left = 0, n_right = 0;
//        for(int i = visibleInUses.size() - 1; i >= 0; i--)
//        {
//            //~+ SIMJ08
//            Target from = visibleInUses.get(i).getFrom();
//            //~- SIMJ08
//            int from_cx = from.getX() + from.getWidth() / 2;
//            if(from_cx < cx)
//                ++n_left;
//            else
//                ++n_right;
//        }
//
//        // Assign source coordinates to each arrow
//        int left_top = getY() + (getHeight() - (n_left - 1) * ARR_VERT_DIST) / 2;
//        int right_top = getY() + (getHeight() - (n_right - 1) * ARR_VERT_DIST) / 2;
//        for(int i = 0; i < n_left + n_right; i++)
//        {
//            //~+ SIMJ08
//            UsesDependency d = visibleInUses.get(i);
//            //~- SIMJ08
//            int from_cx = d.getFrom().getX() + d.getFrom().getWidth() / 2;
//            if(from_cx < cx)
//            {
//                d.setDestCoords(getX() - 4, left_top, ConnectionSide.LEFT);
//                left_top += ARR_VERT_DIST;
//            }
//            else
//            {
//                d.setDestCoords(getX() + getWidth() + 4, right_top, ConnectionSide.RIGHT);
//                right_top += ARR_VERT_DIST;
//            }
//        }
//    }
    public void recalcInUses() {
        List<UsesDependency> visibleInUses = getVisibleUsesDependencies(inUses);
        Collections.sort(visibleInUses, new LayoutComparer(this, true));

        Rectangle currentRect = new Rectangle(getX(), getY(), getWidth(), getHeight());

        // Group dependencies by the best side to connect.
        Map<ConnectionSide, List<UsesDependency>> groups = new EnumMap<>(ConnectionSide.class);
        // Initialize groups.
        for (ConnectionSide side : ConnectionSide.values()) {
            groups.put(side, new ArrayList<>());
        }

        for (UsesDependency d : visibleInUses) {
            Target from = d.getFrom();
            Rectangle fromRect = new Rectangle(from.getX(), from.getY(), from.getWidth(), from.getHeight());
            ConnectionSide bestSide = determineBestSide(currentRect, fromRect);
            // Optionally store the chosen side in the dependency:
            d.setEndConnectionSide(bestSide);
            groups.get(bestSide).add(d);
        }

        // For each group, compute the starting coordinate along the edge and assign connection points.
        // LEFT group: incoming arrows will connect on the left edge.
        List<UsesDependency> leftGroup = groups.get(ConnectionSide.LEFT);
        if (!leftGroup.isEmpty()) {
            int count = leftGroup.size();
            int startY = getY() + (getHeight() - (count - 1) * ARR_VERT_DIST) / 2;
            for (UsesDependency d : leftGroup) {
                d.setDestCoords(getX() - 4, startY, ConnectionSide.LEFT);  // 'true' indicates left side
                startY += ARR_VERT_DIST;
            }
        }

        // RIGHT group: incoming arrows will connect on the right edge.
        List<UsesDependency> rightGroup = groups.get(ConnectionSide.RIGHT);
        if (!rightGroup.isEmpty()) {
            int count = rightGroup.size();
            int startY = getY() + (getHeight() - (count - 1) * ARR_VERT_DIST) / 2;
            for (UsesDependency d : rightGroup) {
                d.setDestCoords(getX() + getWidth() + 4, startY, ConnectionSide.RIGHT); // 'false' indicates right side
                startY += ARR_VERT_DIST;
            }
        }

        // TOP group: incoming arrows will connect on the top edge.
        List<UsesDependency> topGroup = groups.get(ConnectionSide.TOP);
        if (!topGroup.isEmpty()) {
            int count = topGroup.size();
            int startX = getX() + (getWidth() - (count - 1) * ARR_HORIZ_DIST) / 2;
            for (UsesDependency d : topGroup) {
                d.setDestCoords(startX, getY() - 4, ConnectionSide.TOP); // 'true' might indicate top side
                startX += ARR_HORIZ_DIST;
            }
        }

        // BOTTOM group: incoming arrows will connect on the bottom edge.
        List<UsesDependency> bottomGroup = groups.get(ConnectionSide.BOTTOM);
        if (!bottomGroup.isEmpty()) {
            int count = bottomGroup.size();
            int startX = getX() + (getWidth() - (count - 1) * ARR_HORIZ_DIST) / 2;
            for (UsesDependency d : bottomGroup) {
                d.setDestCoords(startX, getY() + getHeight() + 4, ConnectionSide.BOTTOM); // 'false' might indicate bottom side
                startX += ARR_HORIZ_DIST;
            }
        }
    }
    private ConnectionSide determineBestSide(Rectangle currentRect, Rectangle fromRect) {
        int cx = currentRect.x + currentRect.width / 2;
        int cy = currentRect.y + currentRect.height / 2;
        int fromCx = fromRect.x + fromRect.width / 2;
        int fromCy = fromRect.y + fromRect.height / 2;

        int dx = fromCx - cx;
        int dy = fromCy - cy;

        // Choose the side based on which difference is greater.
        if (Math.abs(dx) > Math.abs(dy)) {
            return (dx < 0) ? ConnectionSide.LEFT : ConnectionSide.RIGHT;
        } else {
            return (dy < 0) ? ConnectionSide.TOP : ConnectionSide.BOTTOM;
        }
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
