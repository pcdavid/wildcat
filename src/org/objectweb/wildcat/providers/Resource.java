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
package org.objectweb.wildcat.providers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.objectweb.wildcat.MalformedPathException;
import org.objectweb.wildcat.Path;
import org.objectweb.wildcat.events.EventListener;
import org.objectweb.wildcat.events.EventSource;
import org.objectweb.wildcat.events.PathAddedEvent;
import org.objectweb.wildcat.events.PathChangedEvent;
import org.objectweb.wildcat.events.PathRemovedEvent;

/**
 * Simple rescursive data-structure to represent resources and their attributes.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
class Resource extends EventSource {
    /**
     * This resource's absolute path.
     */
    protected Path path;

    /**
     * Local attributes on this resource.
     */
    protected Map<String, Object> attributes;

    /**
     * Direct sub-resources.
     */
    protected Map<String, Resource> children;

    /**
     * Creates a new resource.
     * 
     * @param path
     *            the absolute path of the new resource.
     * @param listener
     *            the listener which will be notified of local events on this resource,
     *            and inherited by sub-resources.
     */
    Resource(Path path, EventListener listener) {
        assert path != null && listener != null;
        assert path.isAbsolute() && path.isDefinite() && path.isResource();

        this.path = path;
        this.listener = listener;
    }

    /**
     * Returns this resource's path in the context.
     * 
     * @return this resource's absolute path
     */
    public Path getPath() {
        return path;
    }

    /**
     * Creates a new sub-resource with the given name. If the creation succeeds an event
     * is generated corresponding to the {@linkplain PathAddedEvent creation} of the new
     * resource's path.
     * 
     * @param name
     *            the name of the new resource to create
     * @return the newly created subresource
     * @throws IllegalArgumentException
     *             if a sub-resource with the given name already exists.
     * @throws MalformedPathException
     *             if the given name is not a valid resource name.
     */
    public synchronized Resource createChild(String name) {
        assert name != null;
        if (hasChild(name)) {
            throw new IllegalArgumentException("A sub-resource named " + name
                    + " already exists.");
        }
        Resource kid = new Resource(path.append(name), listener);
        if (children == null) {
            children = new HashMap<String, Resource>();
        }
        children.put(name, kid);
        assert hasChild(name);
        assert getChild(name) == kid;
        assert getChild(name).getPath().equals(path.append(name));
        notify(new PathAddedEvent(kid.getPath()));
        return kid;
    }

    /**
     * Deletes the sub-resource with the given name. If the deletion succeeds an event is
     * generated corresponding to the {@linkplain PathRemovedEvent removal} of the
     * resource's path.
     * 
     * @param name
     *            the name of the sub-resource to delete.
     * @throws IllegalArgumentException
     *             if no sbu-resource with that name exists.
     */
    public synchronized void deleteChild(String name) {
        if (!hasChild(name)) {
            throw new IllegalArgumentException(path + " has no child named " + name
                    + " to delete.");
        }
        Resource kid = getChild(name);
        Path kidPath = kid.getPath();
        kid.dispose();
        children.remove(name);
        assert !hasChild(name);
        notify(new PathRemovedEvent(kidPath));
    }

    /**
     * Tests whether a sub-resource with the given name exists.
     * 
     * @param name
     *            the name of the sub-resource to check.
     * @return <code>true</code> iff this resource has a sub-resource named
     *         <code>name</code>.
     */
    public boolean hasChild(String name) {
        return children != null && children.containsKey(name);
    }

    /**
     * Returns the direct sub-resource with the given name, if any.
     * 
     * @param name
     *            the name of the sub-resource to find.
     * @return the sub-resource with the given name, or <code>null</code> if there is
     *         none.
     */
    public synchronized Resource getChild(String name) {
        if (children == null) {
            return null;
        }
        Resource kid = children.get(name);
        if (kid == null) {
            return null;
        }
        Path kidPath = kid.getPath();
        assert kidPath.isAbsolute() && kidPath.isResource() && kidPath.isDefinite();
        assert kidPath.startsWith(this.path) && kidPath.size() == this.path.size() + 1;
        assert kidPath.getResourcesPart().get(kidPath.size() - 1).equals(name);
        return kid;
    }

    /**
     * Returns all the direct sub-resources of this resource.
     * 
     * @return an unmodifiable collection with all the direct sub-resources of this
     *         resource.
     */
    public Collection<Resource> getChildren() {
        if (children != null) {
            return Collections.unmodifiableCollection(children.values());
        } else {
            return Collections.emptySet();
        }
    }

    /**
     * Returns all the direct sub-resources of this resource whose name matches the given
     * pattern. The pattern can be either a valid name or <code>"*"</code> to select all
     * the sub-resources.
     * 
     * @param pattern
     *            the pattern to match agains the sub-resources names.
     * @return an unmodifiable collection of all the direct sub-resources of this resource
     *         whose name matches <code>oattern</code>.
     */
    public Collection<Resource> getChildrenMatching(String pattern) {
        if ("*".equals(pattern)) {
            return getChildren();
        } else if (hasChild(pattern)) {
            return Collections.singleton(getChild(pattern));
        } else {
            return Collections.emptySet();
        }
    }

    /**
     * Deletes all the sub-resources and attributes of this resource, generating all the
     * corresponding events (depth-first).
     */
    synchronized void dispose() {
        for (String kid : keysCopy(children)) {
            deleteChild(kid);
        }
        for (String attrName : keysCopy(attributes)) {
            deleteAttribute(attrName);
        }
    }

    /**
     * Creates a new attribute for this resource. If the creation succeeds, two distinct
     * events are generated: one corresponding to the actual
     * {@linkplain PathAddedEvent creation} of the attribute's path, and one corresponding
     * to the setting of the initial value (as a {@linkplain PathChangedEvent change} from
     * <code>null</code>).
     * 
     * @param name
     *            the name of the new attribute
     * @param initialValue
     *            the initial value for the new attribute
     * @throws IllegalArgumentException
     *             if this resource is the context root
     * @throws IllegalArgumentException
     *             if this resource already has an attribute with the given name.
     * @throws MalformedPathException
     *             if the name is not a valid attribute name
     */
    public synchronized void createAttribute(String name, Object initialValue) {
        if (path.size() == 0) {
            throw new IllegalArgumentException("No attributes allowed on root resource.");
        } else if (hasAttribute(name)) {
            throw new IllegalArgumentException("Attribute " + name + " already exists.");
        }
        Path p = path.appendAttribute(name);
        if (attributes == null) {
            attributes = new HashMap<String, Object>();
        }
        attributes.put(name, initialValue);
        assert hasAttribute(name);
        assert getAttributeValue(name) == initialValue;
        notify(new PathAddedEvent(p), new PathChangedEvent(p, null, initialValue));
    }

    /**
     * Deletes the specified attribute from this resource. If the deletion succeeds an
     * event is generated corresponding to the {@linkplain PathRemovedEvent removal} of
     * the attribute's path.
     * 
     * @param attrName
     *            the name of the attribute to delete.
     * @throws IllegalArgumentException
     *             if this resource has not attribute with the given name.
     */
    public synchronized void deleteAttribute(String attrName) {
        if (hasAttribute(attrName)) {
            Path attrPath = path.appendAttribute(attrName);
            attributes.remove(attrName);
            notify(new PathRemovedEvent(attrPath));
            assert !hasAttribute(attrName);
        } else {
            throw new IllegalArgumentException("No attribute named " + attrName + " in "
                    + path + ".");
        }
    }

    /**
     * Tests whether this resource has an attribute with the given name.
     * 
     * @param name
     *            the name of the attribute to check
     * @return <code>true</code> iff this resource has an attribute named
     *         <code>name</code>
     */
    public boolean hasAttribute(String name) {
        return attributes != null && attributes.containsKey(name);
    }

    /**
     * Returns the current value of the specified attribute, or <code>null</code> if
     * this resource does not have such an attribute. Note that this method can also
     * return <code>null</code> if the attribute exists but has a <code>null</code>
     * value; use {@link #hasAttribute(String)} to distinguish the two cases.
     * 
     * @param name
     *            the name of the attribute to retrieve
     * @return the current value of the attribute named <code>name</code> of
     *         <code>null</code> if this resource does not have such an attribute.
     */
    public synchronized Object getAttributeValue(String name) {
        if (attributes != null) {
            return attributes.get(name);
        } else {
            return null;
        }
    }

    /**
     * Changes the value of the specified attribute. If the change succeeds, an event is
     * generated corresponding to it.
     * 
     * @param name
     *            the name of the attribute to change.
     * @param value
     *            the new value to give to the attribute.
     * @throws IllegalArgumentException
     *             if this resource does not have an attribute named <code>name</code>
     */
    public synchronized void setAttributeValue(String name, Object value) {
        if (hasAttribute(name)) {
            Path attrPath = path.appendAttribute(name);
            Object oldValue = attributes.get(name);
            attributes.put(name, value);
            if (different(oldValue, value)) {
                notify(new PathChangedEvent(attrPath, oldValue, value));
            }
            assert (getAttributeValue(name) == null && value == null)
                    || getAttributeValue(name).equals(value);
        } else {
            throw new IllegalArgumentException("Not attribute " + name + " to change.");
        }
    }

    /**
     * Returns the names of the attributes this resource currently has.
     * 
     * @return the names of all the attributes of this resource.
     */
    public synchronized Collection<String> getAttributes() {
        if (attributes != null) {
            return Collections.unmodifiableCollection(attributes.keySet());
        } else {
            return Collections.emptySet();
        }
    }

    /**
     * Returns the path of all the attributes of this resources whose name matches the
     * provided pattern. The pattern can be either
     * <ul>
     * <li>a valid attribute name, in which case if must be matched exactly (i.e. the
     * returned collection will be either empty if the resource has no such attribute, or
     * a singleton if it has one),</li>
     * <li>or <code>"*"</code>, in which case the paths of all the attributes are
     * returned (in no particular order).</li>
     * </ul>
     * 
     * @param pattern
     * @return
     */
    public synchronized Collection<Path> getAttributesMatching(String pattern) {
        if (attributes == null) {
            return Collections.emptySet();
        } else if ("*".equals(pattern)) {
            Collection<Path> result = new ArrayList<Path>(attributes.size());
            for (String name : attributes.keySet()) {
                result.add(path.appendAttribute(name));
            }
            return result;
        } else if (hasAttribute(pattern)) {
            return Collections.singleton(path.appendAttribute(pattern));
        } else {
            return Collections.emptySet();
        }
    }

    /**
     * Changes the values of a set of attributes all at once. Attributes which do not
     * exist are automatically created with the initial value supplied. Attributes which
     * already existed but are not mentioned in the updated are not modified.
     * 
     * @param samples
     *            the names and values of the attributes to create or update.
     */
    public void setAttributesValues(Map<String, Object> samples) {
        setAttributesValues(samples, true);
    }

    /**
     * Changes the values of a set of attributes all at once. Attributes which do not
     * exist are automatically created with the initial value supplied.
     * <p>
     * If <code>keepOthers</code> is <code>true</code>, attributes which already
     * existed but are not mentioned in the updated are not modified. Otherwise, they are
     * removed, i.e. after this method has executed, this resource only contains the
     * attributes explicitely mentioned in the update.
     * <p>
     * <em>Note:</em> This method is not atomic: if some of the attribute names are
     * invalid (resulting in a {@link MalformedPathException}), some but not all of the
     * valid attributes may have been updated.
     * 
     * @param samples
     *            the names and values of the attributes to create or update.
     */
    public synchronized void setAttributesValues(Map<String, Object> samples,
            boolean keepOthers) {
        Set<String> toDelete = null;
        if (!keepOthers) {
            toDelete = attributes.keySet();
        }
        for (String attrName : samples.keySet()) {
            if (!hasAttribute(attrName)) {
                createAttribute(attrName, samples.get(attrName));
            } else {
                setAttributeValue(attrName, samples.get(attrName));
            }
            if (!keepOthers) {
                toDelete.remove(attrName);
            }
        }
        if (!keepOthers) {
            for (String name : toDelete) {
                deleteAttribute(name);
            }
        }
        for (String name : samples.keySet()) {
            assert hasAttribute(name);
        }
        if (!keepOthers) {
            for (String name : toDelete) {
                assert !hasAttribute(name);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return path.toString();
    }

    /**
     * Utility method to copy all the keys in a map into an array which can be iterated on
     * without risk of {@link ConcurrentModificationException}.
     */
    private String[] keysCopy(Map<String, ?> map) {
        if (map == null) {
            return new String[0];
        } else {
            Set<String> k = map.keySet();
            return k.toArray(new String[k.size()]);
        }
    }
    
    private boolean different(Object o1, Object o2) {
        if (o1 == null) {
            return o2 != null;
        } else {
            return !o1.equals(o2);
        }
    }
}
