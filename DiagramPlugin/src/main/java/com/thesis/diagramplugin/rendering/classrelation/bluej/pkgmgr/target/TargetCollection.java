/*
 This file is part of the BlueJ program.
 Copyright (C) 1999-2009,2013  Michael Kolling and John Rosenberg

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

import java.util.*;

/**
 * A collection of targets.
 *
 * @author Andrew Patterson
 */
public class TargetCollection
{
    /** all the targets in a package */
    protected HashMap<String,Target> targets = new HashMap<String,Target>();

    /**
     * Obtain an iterator that can be used to iterate through the targets in the
     * collection. The iterator becomes invalid if the collection is modified
     * (including if a target is renamed).
     */
    public Iterator<Target> iterator()
    {
        return targets.values().iterator();
    }

    public Iterator<Target> sortediterator()
    {
        return new TreeSet<Target>(targets.values()).iterator();
    }

    public Collection<Target> getAllTargets() { return this.targets.values(); }

    /**
     * Get an array of the targets currently in the collection.
     */
    public Target[] toArray()
    {
        return targets.values().toArray(new Target[targets.size()]);
    }

    public Target get(String identifierName)
    {
        //~+ SIMJ08
        return targets.get(identifierName);
        //~- SIMJ08
    }

    public Target remove(String identifierName)
    {
        //~+ SIMJ08
        return targets.remove(identifierName);
        //~- SIMJ08
    }

    public void add(String identifierName, Target target)
    {
        targets.put(identifierName, target);
    }

    public String toString()
    {
        return targets.toString();
    }
}
