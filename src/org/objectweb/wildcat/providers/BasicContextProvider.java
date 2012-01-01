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
import java.util.List;

import org.objectweb.wildcat.Context;
import org.objectweb.wildcat.ContextProvider;
import org.objectweb.wildcat.MalformedPathException;
import org.objectweb.wildcat.Path;
import org.objectweb.wildcat.dependencies.DependencyGraph;
import org.objectweb.wildcat.events.EventForwarder;
import org.objectweb.wildcat.events.EventListener;
import org.objectweb.wildcat.events.PathAddedEvent;
import org.objectweb.wildcat.events.PathEvent;
import org.objectweb.wildcat.events.PathRemovedEvent;

/**
 * This class implements the notion of {@link ContextProvider} using a simple in-memory
 * data-structure to represent the tree of resources and attributes. It essentially wraps
 * a top-level {@link Resource resource} and provides the high-level
 * {@link ContextProvider ContextProvider} APIs on it.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class BasicContextProvider extends EventForwarder implements ContextProvider {
    /**
     * The top-level resource.
     */
    protected Resource content;
    
    protected DependencyGraph<Path> dependencyGraph;
    
    /* (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#getDependencyGraph()
     */
    public DependencyGraph<Path> getDependencyGraph() {
        return dependencyGraph;
    }
    
    /* (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#setDependencyGraph(org.objectweb.wildcat.dependencies.DependencyGraph)
     */
    public void setDependencyGraph(DependencyGraph<Path> dg) {
        this.dependencyGraph = dg;
    }
    
    public void setEventListener(EventListener listener) {
        this.listener = listener;
    }

    public EventListener getEventListener() {
        return this.listener;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.Location#getPath()
     */
    public Path getPath() {
        if (content != null) {
            return content.getPath();
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.ContextProvider#mounted(org.objectweb.wildcat.Path)
     */
    public void mounted(Path path) {
        if (content != null) {
            throw new IllegalStateException("Context provider already mounted at "
                    + content.getPath() + ".");
        }
        content = new Resource(path, this);
        notify(new PathAddedEvent(path));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.ContextProvider#unmounted()
     */
    public void unmounted() {
        if (content == null) {
            throw new IllegalStateException("Context provider not mounted.");
        }
        Path p = content.getPath();
        content.dispose();
        content = null;
        notify(new PathRemovedEvent(p));
    }
    
    /* (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#update(org.objectweb.wildcat.Path, org.objectweb.wildcat.events.PathEvent)
     */
    public void update(Path path, PathEvent cause) {
        // Ignore
    }
    
    public void createResource(String res) {
        createResource(Context.createPath(res));
    }

    /**
     * Creates a new resource.
     * 
     * @param res
     */
    public void createResource(Path res) {
        if (res.isPattern()) {
            throw new IllegalArgumentException(
                    "A definite path is required to create a resource.");
        } else if (res.isAttribute()) {
            throw new IllegalArgumentException(
                    "Can not create a resource at an attribute location.");
        } else if (findResource(res, false) != null) {
            throw new IllegalArgumentException("Resource " + res + " already exists.");
        }
        findResource(res, true);
    }
    
    public void createAttribute(String attr, Object initialValue) {
        createAttribute(Context.createPath(attr), initialValue);
    }

    /**
     * Creates a new attribute.
     * 
     * @param attr
     * @param initialValue
     */
    public void createAttribute(Path attr, Object initialValue) {
        if (attr.isPattern()) {
            throw new IllegalArgumentException(
                    "A definite path is required to create an attribute.");
        } else if (attr.isResource()) {
            throw new IllegalArgumentException(
                    "Can not create an attribue at a resource location.");
        }
        Resource res = findResource(attr, false);
        if (res != null) {
            res.createAttribute(attr.getAttributePart(), initialValue);
        } else {
            throw new IllegalArgumentException(attr
                    + " is not a valid location for a new attribute.");
        }
    }
    
    public void setValue(String attr, Object value) {
        setValue(Context.createPath(attr), value);
    }

    /**
     * Sets the value of an attribute.
     * 
     * @param attr
     * @param value
     */
    public void setValue(Path attr, Object value) {
        if (attr.isResource()) {
            throw new IllegalArgumentException("Can not set the value of a resource.");
        } else if (attr.isAbsolute()) {
            throw new IllegalArgumentException("Context provider expects relative paths.");
        } else if (attr.isPattern()) {
            throw new IllegalArgumentException("Definite path expected.");
        }
        Resource res = findResource(attr, false);
        if (res != null) {
            res.setAttributeValue(attr.getAttributePart(), value);
        } else {
            throw new IllegalArgumentException("No such attribute.");
        }
    }
    
    public void delete(String location) {
        delete(Context.createPath(location));
    }

    /**
     * Deletes a resource (and all its content) or an attribute.
     * 
     * @param location
     */
    public void delete(Path location) {
        if (location.isPattern()) {
            throw new IllegalArgumentException("Definite path expected.");
        } else if (location.isAbsolute()) {
            throw new IllegalArgumentException("Context provider expects relative paths.");
        }
        Resource res = content;
        if (location.size() > 1) {
            try {
                res = findResource(location.getParent(), false);
            } catch (MalformedPathException e) {
                throw new AssertionError("Should not happen.");
            }
        }
        if (location.isAttribute()) {
            res.deleteAttribute(location.getAttributePart());
        } else {
            List<String> steps = location.getResourcesPart();
            res.deleteChild(steps.get(steps.size() - 1));
        }
    }

    /**
     * Finds the {@link Resource} implementing the given location.
     * 
     * @param path
     * @return
     */
    protected Resource findResource(Path path, boolean create) {
        assert path.isRelative();
        Resource result = content;
        for (String step : path.getResourcesPart()) {
            if (result.hasChild(step)) {
                result = result.getChild(step);
            } else if (create) {
                result = result.createChild(step);
            } else {
                return null;
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.ContextProvider#lookup(org.objectweb.wildcat.Path)
     */
    public Collection<Path> lookup(Path query) {
        if (content == null) {
            throw new IllegalStateException("Context provider not mounted.");
        } else if (query.isAbsolute()) {
            throw new IllegalArgumentException(
                    "Context provider expects relative queries.");
        } else {
            return relativeLookup(query);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.Query#lookupAttribute(org.objectweb.wildcat.Path)
     */
    public Object lookupAttribute(Path query) {
        if (content == null) {
            throw new IllegalStateException("Context provider not mounted.");
        } else if (query.isAbsolute()) {
            throw new IllegalArgumentException(
                    "Context provider expects relative queries.");
        } else if (query.isPattern() || query.isResource()) {
            throw new IllegalArgumentException(query.toString());
        } else {
            return relativeLookupAttribute(query);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.Query#lookup(org.objectweb.wildcat.Path)
     */
    private Collection<Path> relativeLookup(Path query) {
        Collection<Resource> current = new ArrayList<Resource>();
        current.add(content);
        for (String step : query.getResourcesPart()) {
            Collection<Resource> next = new ArrayList<Resource>();
            for (Resource r : current) {
                next.addAll(r.getChildrenMatching(step));
            }
            current = next;
        }
        Collection<Path> result = new ArrayList<Path>();
        for (Resource r : current) {
            if (query.isResource()) {
                result.add(r.getPath());
            } else {
                result.addAll(r.getAttributesMatching(query.getAttributePart()));
            }
        }
        return result;
    }

    private Object relativeLookupAttribute(Path query) {
        Resource current = content;
        for (String step : query.getResourcesPart()) {
            current = current.getChild(step);
            if (current == null) {
                return null;
            }
        }
        if (current.hasAttribute(query.getAttributePart())) {
            return current.getAttributeValue(query.getAttributePart());
        } else {
            return null;
        }
    }
}
