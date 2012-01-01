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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Default implementation of {@link Path}.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
class SimplePath extends AbstractPath {
    private static Path ROOT = new SimplePath(true, true, new ArrayList<String>(), null);

    public static Path getRoot() {
        return ROOT;
    }

    /**
     * Syntax for valid names of resources and attributes.
     */
    //private static final String IDENTIFIER_REGEX = "(?:\\p{Alpha}|_)[\\p{Alnum}_-]*";
    private static final String IDENTIFIER_REGEX = "[^/\\*]+";

    /**
     * Syntax for a valid resource name or pattern.
     */
    private static final String RESOURCE_REGEX = "(?:" + IDENTIFIER_REGEX
            + "|\\*|\\*\\*|\\.\\.)";

    /**
     * Syntax for a valid attribute name or pattern.
     */
    private static final String ATTRIBUTE_REGEX = "(?:" + IDENTIFIER_REGEX + "|\\*)";

    /*
     * Compiled versions of the regexps above.
     */
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile(IDENTIFIER_REGEX);

    private static final Pattern RESOURCE_PATTERN = Pattern.compile(RESOURCE_REGEX);

    private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile(ATTRIBUTE_REGEX);

    private static final Pattern SEPARATOR_PATTERN = Pattern.compile("/");

    /**
     * Indicates whether this Path is absolute (<code>true</code>) ore relative (<code>false</code>).
     */
    private boolean absolute;

    /**
     * Indicates whether this path is definite (<code>true</code>) or represents
     * pattern which might match a set of locations (<code>false</code>).
     */
    private boolean definite;

    /**
     * All the resources in the path. Empty (not <code>null</code>) for the root path
     * <code>/</code>.
     */
    private List<String> resources;

    /**
     * The name of the attribute part of the path, or <code>null</code> if the path
     * denotes a (set of) resources.
     */
    private String attribute;

    /**
     * Creates a new Path by parsing a String representation. The syntax is URI-like:
     * <code>/steps/to/the/resource#optional_attribute</code>.
     * 
     * @param path
     *            the textual representation of the path to create
     * @throws IllegalArgumentException
     *             if <code>pathExpr</code> is not a syntaxically correct path.
     */
    public SimplePath(String path) throws MalformedPathException {
        if (path.length() == 0) {
            syntaxError("empty path not allowed");
        } else if (path.indexOf("//") != -1 || path.indexOf("##") != -1) {
            // Pattern#split() does not handle this like we want.
            syntaxError("multiple consecutive separators not allowed");
        }
        // Split the resource steps
        if (path.charAt(0) == '/') {
            absolute = true;
            if (path.length() == 1) {
                // Special case for root ("/"), as Pattern#split() once again does not do
                // what we want.
                resources = Collections.emptyList();
            } else {
                resources = Arrays.asList(SEPARATOR_PATTERN.split(path.substring(1)));
            }
        } else {
            absolute = false;
            resources = Arrays.asList(SEPARATOR_PATTERN.split(path));
        }
        // Split the last step (if any) if it has an attribute part
        if (resources.size() > 0) {
            String last = resources.get(resources.size() - 1);
            int i = last.indexOf('#');
            if (i == 0 && resources.size() > 1) {
                // Corresponds to "/foo/#bar"
                syntaxError("missing resource name or pattern before attribute.");
            } else if (i != -1) {
                attribute = last.substring(i + 1);
                // Fix (or remove) the last resource step.
                last = last.substring(0, i);
                if (last.length() > 0) {
                    resources.set(resources.size() - 1, last);
                } else {
                    // Must make a copy as the list returned by Arrays.asList() are
                    // immutable.
                    resources = new ArrayList<String>(resources);
                    resources.remove(resources.size() - 1);
                }
            }
        }
        // Check the syntax of each step and determine if it is definite or pattern
        definite = true;
        for (String res : resources) {
            if (!matches(res, RESOURCE_PATTERN)) {
                syntaxError("invalid resource identifier (" + res + ")");
            }
            if (definite && !matches(res, IDENTIFIER_PATTERN)) {
                definite = false;
            }
        }
        if (attribute != null) {
            if (!matches(attribute, ATTRIBUTE_PATTERN)) {
                syntaxError("invalid attribute (" + attribute + ")");
            }
            if (definite && !matches(attribute, IDENTIFIER_PATTERN)) {
                definite = false;
            }
        }
        // Freeze the list of steps so that they can be returned directly,
        // shared, and referenced safely.
        resources = Collections.unmodifiableList(resources);
    }

    private boolean matches(String s, Pattern p) {
        return p.matcher(s).matches();
    }

    private void syntaxError(String message) throws MalformedPathException {
        throw new MalformedPathException("Syntax error: " + message + ".");
    }

    /**
     * Private constructor used when buildings new path relative to existing ones.
     */
    private SimplePath(boolean absolute, boolean definite, List<String> resources,
            String attribute) throws MalformedPathException {
        if (absolute && resources.size() == 0 && attribute != null) {
            throw new MalformedPathException("Root path can not have attributes.");
        }
        this.absolute = absolute;
        this.definite = definite;
        this.resources = resources;
        this.attribute = attribute;
    }

    @Override
    protected Path newFromString(String path) throws MalformedPathException {
        return new SimplePath(path);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.Path#isAbsolute()
     */
    public boolean isAbsolute() {
        return absolute;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.Path#isDefinite()
     */
    public boolean isDefinite() {
        return definite;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.Path#isAttribute()
     */
    public boolean isAttribute() {
        return attribute != null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.Path#getAttributeName()
     */
    public String getAttributePart() {
        return attribute;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.Path#getResourcesPart()
     */
    public List<String> getResourcesPart() {
        return resources;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.Path#append(org.objectweb.wildcat.Path)
     */
    public Path append(Path suffix) throws MalformedPathException {
        if (suffix.isAbsolute()) {
            throw new MalformedPathException("Suffix path must be relative.");
        } else if (this.isAttribute()) {
            throw new MalformedPathException("Attribute paths can not be appended to.");
        } else if (suffix.getResourcesPart().size() == 0) {
            return new SimplePath(this.absolute, this.definite && suffix.isDefinite(),
                    this.resources, suffix.getAttributePart());
        } else {
            List<String> steps = new ArrayList<String>(this.resources);
            steps.addAll(suffix.getResourcesPart());
            return new SimplePath(this.absolute, this.definite && suffix.isDefinite(),
                    steps, suffix.getAttributePart());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.Path#endsWith(org.objectweb.wildcat.Path)
     */
    public boolean endsWith(Path suffix) {
        if (suffix.isAbsolute() && this.isRelative()) {
            return false;
        } else if (this.isAttribute() != suffix.isAttribute()) {
            return false;
        } else if (suffix.isAttribute()) {
            if (!this.getAttributePart().equals(suffix.getAttributePart())) {
                return false;
            }
        }
        List<String> mySteps = this.getResourcesPart();
        List<String> hisSteps = suffix.getResourcesPart();
        if (hisSteps.size() > mySteps.size()) {
            return false;
        }
        for (int i = hisSteps.size() - 1; i >= 0; i--) {
            if (!mySteps.get(i).equals(hisSteps.get(i))) {
                return false;
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.Path#getParent()
     */
    public Path getParent() throws MalformedPathException {
        if (attribute != null) {
            return new SimplePath(this.absolute, this.definite, this.resources, null);
        } else if (this.resources.size() > 1) {
            return new SimplePath(this.absolute, this.definite, this.resources.subList(0,
                    this.resources.size() - 1), null);
        } else if (this.absolute && this.resources.size() == 1) {
            return new SimplePath(true, true, new ArrayList<String>(), null);
        } else {
            throw new MalformedPathException("");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.Path#matches(org.objectweb.wildcat.Path)
     */
    public boolean matches(Path pattern) {
        return this.toString().matches(patternRegexp(pattern));
    }

    /**
     * @param pattern
     * @return
     */
    private String patternRegexp(Path pattern) {
        String str = pattern.toString();
        return str.replaceAll("\\*", IDENTIFIER_REGEX.replace("\\", "\\\\"));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.Path#size()
     */
    public int size() {
        return resources.size() + ((attribute != null) ? 1 : 0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.Path#startWith(org.objectweb.wildcat.Path)
     */
    public boolean startsWith(Path prefix) {
        if (this.isAbsolute() != prefix.isAbsolute()) {
            return false;
        }
        List<String> mySteps = this.getResourcesPart();
        List<String> hisSteps = prefix.getResourcesPart();
        if (hisSteps.size() > mySteps.size()) {
            return false;
        }
        int i;
        for (i = 0; i < hisSteps.size(); i++) {
            if (!mySteps.get(i).equals(hisSteps.get(i))) {
                return false;
            }
        }
        if (prefix.isAttribute()) {
            return i == mySteps.size()
                    && prefix.getAttributePart().equals(this.getAttributePart());
        } else {
            return true;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.Path#subPath(int, int)
     */
    public Path subPath(int start, int finish) throws MalformedPathException {
        int maxAllowedIndex = size() + 1;
        if (start < 0 || start >= finish || finish > maxAllowedIndex) {
            throw new MalformedPathException("Invalid range for sub-path.");
        }
        boolean itsAbsolute = this.absolute && start == 0;
        int listStart = Math.max(start - 1, 0);
        if (finish - 1 == listStart && !itsAbsolute) {
            // Empty relative paths don't make sense.
            return null;
        }
        int listFinish = Math.min(finish - 1, resources.size());
        List<String> itsResources = resources.subList(listStart, listFinish);
        boolean itsDefinite = true;
        if (this.isPattern()) {
            for (String step : itsResources) {
                if (!matches(step, IDENTIFIER_PATTERN)) {
                    itsDefinite = false;
                    break;
                }
            }
        }
        String itsAttribute = (finish == maxAllowedIndex) ? this.attribute : null;
        if (itsAttribute != null && itsDefinite
                && !matches(itsAttribute, IDENTIFIER_PATTERN)) {
            itsDefinite = false;
        }
        return new SimplePath(itsAbsolute, itsDefinite, itsResources, itsAttribute);
    }
    
    /* (non-Javadoc)
     * @see org.objectweb.wildcat.Path#appendResource(java.lang.String)
     */
    public Path appendResource(String name) throws MalformedPathException {
        if (this.isAttribute()) {
            throw new MalformedPathException(
                    "Can't append a resource to an attribute path.");
        } else if (!matches(name, RESOURCE_PATTERN)) {
            throw new MalformedPathException("Invalid resource name: " + name + ".");
        } else {
            List<String> steps = new ArrayList<String>(this.resources);
            steps.add(name);
            return new SimplePath(this.absolute, this.definite
                    && matches(name, IDENTIFIER_PATTERN), steps, null);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.Path#appendAttribute(java.lang.String)
     */
    public Path appendAttribute(String name) throws MalformedPathException {
        if (this.isAttribute()) {
            throw new MalformedPathException(
                    "Can't append an attribute to an attribute path.");
        } else if (!matches(name, ATTRIBUTE_PATTERN)) {
            throw new MalformedPathException("Invalid attribute name: " + name + ".");
        } else {
            return new SimplePath(this.absolute, this.definite
                    && matches(name, IDENTIFIER_PATTERN), this.resources, name);
        }
    }
}
