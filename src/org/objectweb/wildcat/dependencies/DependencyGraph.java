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

/**
 * This interface defines a simple, generic depdendency graph data structure.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public interface DependencyGraph<T> {
	/**
	 * Registers a dependency from <code>dependant</code> to
	 * <code>dependency</code>, i.e. "<i>dependant</i> depends on
	 * <i>dependency</i>".
	 * 
	 * @throws IllegalArgumentException
	 *             if this dependency would create a cycle in the graph.
	 */
	void addDependency(T dependant, T dependency)
			throws IllegalArgumentException;

	/**
	 * Registers that <code>dependant</code> does not depend on
	 * <code>dependency</code> anymore.
	 */
	void removeDependency(T dependant, T dependency);

	/**
	 * Returns all the elements which depend directly on <code>dependency</code>,
	 * i.e. all the elements which must be updated whenever
	 * <code>dependency</code> changes.
	 */
	Collection<T> getAllDependingOn(T dependency);
}
