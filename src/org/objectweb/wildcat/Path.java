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
 * Unix paths, except that attributes (akin to files) are syntacticaly distinguished from
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
     * equivalent to {@link Object#equals(Object)}. If <code>p</code> is a
     * {@linkplain #isPattern() pattern} path, returns <code>true</code> iff this path
     * matches the pattern
     * 
     * @param p
     *            a pattern path
     * @return <code>true</code> iff this path patches the specified pattern.
     */
    boolean matches(Path p);

    /**
     * Tests whether this path starts with the given prefix.
     * 
     * @param prefix
     * @return <code>true</code> iff this path starts with the given suffix
     */
    boolean startsWith(Path prefix);

    /**
     * Tests whether this path starts with the given prefix.
     * 
     * @param prefix
     * @return <code>true</code> iff this path starts with the given suffix
     */
    boolean startsWith(String prefix) throws MalformedPathException;

    /**
     * Tests whether this path ends with the given suffix.
     * 
     * @param suffix
     * @return <code>true</code> iff this path ends with the given suffix
     */
    boolean endsWith(Path suffix);

    /**
     * Tests whether this path ends with the given suffix.
     * 
     * @param suffix
     * @return <code>true</code> iff this path ends with the given suffix
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
     * @return a new path denoting the parent(s) of this path.
     */
    Path getParent() throws MalformedPathException;

    /**
     * Creates an returns a new path by appending a suffix to this path.
     * 
     * @param suffix
     *            a relative path to append to this path.
     * @return a new path starting with this path and ending with the specified suffix.
     * @throws MalformedPathException
     *             if the resulting path would be malformed.
     */
    Path append(Path suffix) throws MalformedPathException;

    /**
     * Convenience shortcut to {@link #append(Path)} using a string instead of an already
     * parsed path.
     * 
     * @throws MalformedPathException
     *             if the resulting path would be malformed or if the specified suffix is
     *             not a valid path specification.
     */
    Path append(String suffix) throws MalformedPathException;

    /**
     * Creates a new path by extending the receiver with an additional resource step.
     * 
     * @param name
     *            the name of the resource step to append.
     * @return a new path
     * @throws MalformedPathException
     *             if the receiver is an attribute path (and hence can not be extended) or
     *             if the specified name if not a valid resource name or pattern.
     */
    Path appendResource(String name) throws MalformedPathException;

    /**
     * Creates a new path by extending the receiver with an attribute step.
     * 
     * @param name
     *            the name of the attribute to append.
     * @return a new path
     * @throws MalformedPathException
     *             if the receiver is an attribute path (and hence can not be extended) or
     *             if the specified name if not a valid attribute name or pattern.
     */
    Path appendAttribute(String name) throws MalformedPathException;

    /**
     * Returns the suffix of this path beginning at the given index.
     * 
     * @param start
     *            the index in this path where the subpath starts
     * @return a new path
     * @throws MalformedPathException
     *             if <code>start</code> is not a valid index
     */
    Path subPath(int start) throws MalformedPathException;

    /**
     * Returns a subpath of this path. The new path contains the parts of this path in the
     * given index interval. Valid indices range from <code>0</code> to
     * <code>#size() + 1</code>.
     * 
     * @param start
     * @param finish
     * @return a subath of this path
     * @throws MalformedPathException
     */
    Path subPath(int start, int finish) throws MalformedPathException;

    /**
     * Returns the size of this path, i.e. the number of steps it contains (including both
     * resources and the optional attribute part).
     * 
     * @return the size of this path.
     */
    int size();

    /**
     * "Relativizes" this path with regard to a given base path. If this path starts with
     * the base path, returns the suffix of this path excluding this base. Otherwise
     * throws an exception.
     * 
     * @param base
     *            the base path
     * @return the suffix of this path excluding <code>base</code>.
     * @throws MalformedPathException
     *             if base is not a proper prefix of this path.
     */
    Path relativeTo(Path base) throws MalformedPathException;
}
