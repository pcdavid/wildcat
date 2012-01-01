/*
 * Copyright (c) 2004-2005 Universite de Nantes (LINA)
 * Copyright (c) 2005-2006 France Telecom
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Contact: Pierre-Charles David <pcdavid@gmail.com>
 */
package org.objectweb.wildcat;

import java.util.Collection;

import org.objectweb.wildcat.dependencies.DependencyGraph;
import org.objectweb.wildcat.events.EventListener;
import org.objectweb.wildcat.events.PathEvent;

/**
 * A context provider is responsible for the management (lookup and event generation) of
 * some sub-tree in the context where it is mounted.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public interface ContextProvider {
    /**
     * Returns the absolute path of the top-level location managed by this provider, i.e.
     * the location at which it is mounted.
     * 
     * @return the location at which this provider is mounted, or <code>null</code> if
     *         it is not mounted.
     */
    Path getPath();

    /**
     * Callback sent to notify this <code>ContextProvider</code> that it is being mounted
     * at the specified location.
     * 
     * @param path
     *            the absolute location at which this <code>ContextProvider</code> is
     *            being mounted.
     */
    void mounted(Path path);

    /**
     * Callback used to notify this <code>ContextProvider</code> that it is begin
     * unmounted.
     */
    void unmounted();

    /**
     * Lookup this <code>ContextProvider</code> for the specified location(s).
     * 
     * @param query
     *            a relative path denoting the set of locations in this provider to look
     *            for.
     * @return a collection of all the absolute locations which currently exist inside
     *         this provider and which match the query.
     */
    Collection<Path> lookup(Path query);

    /**
     * Looks up the <em>value</em> of an attribute.
     * 
     * @param attribute
     *            the location of the attribute to look for, relative to this provider.
     * @return the value of the attribute, it it exists, or <code>null</code> otherwise.
     */
    Object lookupAttribute(Path attribute);

    /**
     * Sets the event listener to which this provider will report all the events occuring
     * inside it.
     * 
     * @param listener
     *            the event listener to notify.
     */
    void setEventListener(EventListener listener);

    /**
     * Returns the event listener to which this provider will report all the events
     * occuring inside it.
     * 
     * @return the event listener this provider notifies.
     */
    EventListener getEventListener();
    
    void setDependencyGraph(DependencyGraph<Path> dg);
    DependencyGraph<Path> getDependencyGraph();
    
    /**
     * Update the element located at <code>path</code> (typically an attribute) in
     * response to <code>cause</code>.
     * 
     * @param path
     * @param cause
     */
    void update(Path path, PathEvent cause);
}
