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

/**
 * A <code>ContextProviderContainer</code> extends a plain {@link ContextProvider} with
 * support for mounting and unmounting "sub-providers" at specific points in its resource
 * tree.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public interface ContextProviderContainer extends ContextProvider {
    /**
     * Mounts a new provider at some location inside this container.
     * 
     * @param mountPoint
     * @param provider
     * @throws InvalidMountPointException
     */
    void mount(Path mountPoint, ContextProvider provider)
            throws InvalidMountPointException;

    /**
     * Unmounts a provider mounted inside this container.
     * 
     * @param mountPoint
     * @throws InvalidMountPointException
     */
    void unmount(Path mountPoint) throws InvalidMountPointException;
}
