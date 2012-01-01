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
package org.objectweb.wildcat.dependencies;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Default implementation of a generic dependency graph data structure.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class DependencyGraphImpl<T> implements DependencyGraph<T> {
    /**
     * Direct dependency links: from a element (the dependency) to the list of its
     * dependants, i.e. the elements which depend on it. More precisely, "X depends on Y"
     * iff <code>dependantsOn.get(Y).contains(X)</code>.
     */
    private Map<T, Set<T>> dependantsOn;

    /**
     * Creates a new, empty, dependency graph.
     */
    public DependencyGraphImpl() {
        dependantsOn = Collections.synchronizedMap(new HashMap<T, Set<T>>());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.DependencyGraph#addDependency(java.lang.Object,
     *      java.lang.Object)
     */
    public void addDependency(T dependant, T dependency) throws IllegalArgumentException {
        synchronized (this) {
            if (reachableFrom(dependant, dependency, dependantsOn)) {
                throw new IllegalArgumentException(
                        "Invalid dependency would create a cycle: " + dependency
                                + " already depends on " + dependant + ".");
            }
            Set<T> deps = dependantsOn.get(dependency);
            if (deps == null) {
                deps = new HashSet<T>();
                dependantsOn.put(dependency, deps);
            }
            deps.add(dependant);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.DependencyGraph#removeDependency(java.lang.Object,
     *      java.lang.Object)
     */
    public void removeDependency(T dependant, T dependency) {
        synchronized (this) {
            Collection<T> deps = dependantsOn.get(dependency);
            if (deps != null) {
                deps.remove(dependant);
                if (deps.isEmpty()) {
                    dependantsOn.remove(dependency);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.DependencyGraph#getAllDependingOn(java.lang.Object)
     */
    public Collection<T> getAllDependingOn(T dependency) {
        synchronized (this) {
            Collection<T> deps = dependantsOn.get(dependency);
            if (deps == null) {
                return Collections.emptySet();
            } else {
                return Collections.unmodifiableCollection(deps);
            }
        }
    }

    /**
     * Tests whether <code>dest</code> is reachable from <code>src</code> in the graph
     * represented by <code>graph</code> (node -> successors of node).
     */
    private boolean reachableFrom(T src, T dest, Map<T, Set<T>> graph) {
        if (src.equals(dest)) {
            return true;
        }
        Queue<T> toVisit = new LinkedList<T>();
        Set<T> visited = new HashSet<T>();
        toVisit.add(src);
        while (!toVisit.isEmpty()) {
            T current = toVisit.remove();
            if (visited.contains(current)) {
                continue;
            }
            Set<T> successors = graph.get(current);
            if (successors != null && successors.contains(dest)) {
                return true;
            } else {
                visited.add(current);
                if (successors != null) {
                    toVisit.addAll(successors);
                }
            }
        }
        return false;
    }
}
