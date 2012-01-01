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
 * Provides standard implementations for some {@link Path} methods, in particular
 * convenience methods which are defined in terms of others (e.g.
 * {@link Path#isAbsolute()} defined as the opposite of {@link Path#isRelative()}).
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public abstract class AbstractPath implements Path {
    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.Path#isRelative()
     */
    public boolean isRelative() {
        return !isAbsolute();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.Path#isPattern()
     */
    public boolean isPattern() {
        return !isDefinite();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.Path#isResource()
     */
    public boolean isResource() {
        return !isAttribute();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.Path#append(java.lang.String)
     */
    public Path append(String suffix) throws MalformedPathException {
        return append(newFromString(suffix));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.Path#prepend(java.lang.String)
     */
    public Path prepend(String prefix) throws MalformedPathException {
        return newFromString(prefix).append(this);
    }

    /**
     * Creates a new path from a string. This method must be implemented by concrete
     * subclasses to benefit from the shortcut methods provided by this class which take a
     * string instead of a fully-formed {@link Path} object.
     */
    protected abstract Path newFromString(String path) throws MalformedPathException;

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Path) {
            Path that = (Path) obj;
            return (this.isAbsolute() == that.isAbsolute())
                    && (this.getResourcesPart().equals(that.getResourcesPart()))
                    && equals(this.getAttributePart(), that.getAttributePart());
        } else {
            return false;
        }
    }

    /**
     * Tests two objects for equality, correctly handling the case when they are both
     * <code>null</code>.
     * 
     * @param o1
     *            an object
     * @param o2
     *            another object
     * @return <code>true</code> iff <code>o1</code> and <code>o2</code> are
     *         {@link Object#equals(Object)} or both <code>null</code>.
     */
    protected boolean equals(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        } else {
            return o1.equals(o2);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = isAbsolute() ? 5 : 7;
        for (String step : getResourcesPart()) {
            hash = hash * 13 + step.hashCode();
        }
        String attr = getAttributePart();
        if (attr != null) {
            hash = hash * attr.hashCode();
        }
        return hash;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.Path#startsWith(java.lang.String)
     */
    public boolean startsWith(String prefix) throws MalformedPathException {
        return startsWith(newFromString(prefix));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.Path#endsWith(java.lang.String)
     */
    public boolean endsWith(String suffix) throws MalformedPathException {
        return endsWith(newFromString(suffix));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.Path#subPath(int)
     */
    public Path subPath(int start) throws MalformedPathException {
        return subPath(start, size() + 1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.Path#relativeTo(org.objectweb.wildcat.Path)
     */
    public Path relativeTo(Path base) throws MalformedPathException {
        if (this.startsWith(base)) {
            return this.subPath(base.size() + 1);
        } else {
            throw new MalformedPathException(base + " is not a prefix of " + this + ".");
        }
    }


    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        boolean first = true;
        if (isAbsolute() && getResourcesPart().size() == 0) {
            return "/";
        }
        for (String step : getResourcesPart()) {
            if (!first || isAbsolute()) {
                str.append("/");
            }
            str.append(step);
            first = false;
        }
        String attr = getAttributePart();
        if (attr != null) {
            str.append("#").append(attr);
        }
        return str.toString();
    }
}
