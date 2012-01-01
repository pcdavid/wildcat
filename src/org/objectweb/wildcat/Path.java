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

import java.util.List;

/**
 * Denotes location or a set of locations in a WildCAT context. The locations denoted
 * might or might not exist at a particular point in time. WildCAT paths are similar to
 * Unix paths, except that attributes (akin to file) are syntacticaly distinguished from
 * resources (akin to directories). WildCAT also supports a (currently) limited form of
 * "glob matching" as found in Unix shells.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public interface Path {
    /**
     * Tests whether this path is absolute. This is the opposite of
     * {@linkplain #isRelative()}.
     * 
     * @return <code>true</code> iff this path is absolute.
     * @see #isRelative()
     */
    boolean isAbsolute();

    /**
     * Tests whether this path is relative. This is the opposite of
     * {@linkplain #isAbsolute()}.
     * 
     * @return <code>true</code> iff this path is relative.
     * @see #isAbsolute()
     */
    boolean isRelative();

    /**
     * Tests whether this path denotes a definite location (whether it exists or not), as
     * opposed to a pattern which might match a set of locations. This is the opposite of
     * {@linkplain #isPattern()}.
     * 
     * @return <code>true</code> iff this path denotes a definite location.
     * @see #isPattern()
     */
    boolean isDefinite();

    /**
     * Tests whether this path denotes a pattern of locations (whether they exist or not),
     * as opposed to a definite, unique location. This is the opposite of
     * {@linkplain #isDefinite()}.
     * 
     * @return <code>true</code> iff this path denotes a pattern of locations.
     * @see #isDefinite()
     */
    boolean isPattern();

    /**
     * Tests whether this path denotes an attribute (in the case of a definite path) or a
     * potential set of attributes (in the case of a pattern path). This is the opposite
     * of {@linkplain #isResource()}.
     * 
     * @return <code>true</code> iff this path denotes an attribute or a potential set
     *         of attributes.
     * @see #isResource()
     */
    boolean isAttribute();

    /**
     * Tests whether this path denotes a resource (in the case of a definite path) or a
     * potential set of resources (in the case of a pattern path). This is the opposite of
     * {@linkplain #isAttribute()}.
     * 
     * @return <code>true</code> iff this path denotes a resource or a potential set of
     *         resource.
     * @see #isAttribute()
     */
    boolean isResource();

    /**
     * Returns the name of the attribute designated by this path, which can be either an
     * identifier (e.g. <code>model</code>), a pattern (e.g. <code>*</code>), or
     * <code>null</code> if this path does not denote a (set of) attribute(s).
     * 
     * @return the attribute part of this path.
     */
    String getAttributePart();

    /**
     * Returns the sequence of resources names/patterns in this path.
     * 
     * @return the sequence of resources names/patterns in this path.
     */
    List<String> getResourcesPart();

    /**
     * Tests whether this path, which must be {@link #isDefinite() definite}, matches the
     * pattern of <code>p</code>. If <code>p</code> is also a definite path, this is
     * equivalent to {@link #equals(Object)}. If <code>p</code> is a
     * {@linkplain #isPattern() pattern} path, returns <code>true</code> iff TODO
     * 
     * @param p
     * @return
     */
    boolean matches(Path p);

    /**
     * Tests whether this path starts with the given prefix.
     * 
     * @param prefix
     * @return
     */
    boolean startsWith(Path prefix);

    /**
     * Tests whether this path starts with the given prefix.
     * 
     * @param prefix
     * @return
     */
    boolean startsWith(String prefix) throws MalformedPathException;

    /**
     * Tests whether this path ends with the given suffix.
     * 
     * @param suffix
     * @return
     */
    boolean endsWith(Path suffix);

    /**
     * Tests whether this path ends with the given suffix.
     * 
     * @param suffix
     * @return
     */
    boolean endsWith(String suffix) throws MalformedPathException;

    /**
     * Returns a textual representation of this path suitable to be used by the WildCAT
     * APIs. The representation is always canonical, i.e. a path created from the string
     * <code>"/path/to/resource/../.."</code> will be returned as the string
     * <code>"/path"</code>.
     * 
     * @return the canonical string representation of this path.
     */
    String toString();

    /**
     * Returns a new path denoting the parent(s) of this path.
     * 
     * @return
     */
    Path getParent() throws MalformedPathException;

    /**
     * Creates an return a new path by appending a suffix to this path.
     * 
     * @param suffix
     * @return
     * @throws MalformedPathException
     */
    Path append(Path suffix) throws MalformedPathException;

    /**
     * Creates an return a new path by appending a suffix to this path.
     * 
     * @param suffix
     * @return
     * @throws MalformedPathException
     */
    Path append(String suffix) throws MalformedPathException;

    Path appendResource(String name) throws MalformedPathException;

    Path appendAttribute(String name) throws MalformedPathException;

    Path subPath(int start) throws MalformedPathException;

    Path subPath(int start, int finish) throws MalformedPathException;

    int size();

    Path relativeTo(Path path) throws MalformedPathException;
}
