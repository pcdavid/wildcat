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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.objectweb.wildcat.ContextProvider;
import org.objectweb.wildcat.ContextProviderContainer;
import org.objectweb.wildcat.InvalidMountPointException;
import org.objectweb.wildcat.MalformedPathException;
import org.objectweb.wildcat.Path;
import org.objectweb.wildcat.dependencies.DependencyGraph;
import org.objectweb.wildcat.events.EventForwarder;
import org.objectweb.wildcat.events.EventListener;
import org.objectweb.wildcat.events.PathAddedEvent;
import org.objectweb.wildcat.events.PathEvent;
import org.objectweb.wildcat.events.PathRemovedEvent;

/**
 * An overlay context provider wraps a normal context provider, but is able to divert
 * requests to some sub-path to other providers (un)mounted using the
 * {@link ContextProviderContainer} API.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class OverlayContextProvider extends EventForwarder implements
        ContextProviderContainer {
    /**
     * <code>true</code> iff this <code>ContextProvider</code> is currently mounted.
     */
    private boolean mounted;

    /**
     * The default provider used outside of more specific providers mounted explicitely.
     */
    private ContextProvider defaultProvider;

    /**
     * Relative path of mount point -> provider.
     */
    private Map<Path, ContextProvider> mountPoints;

    /**
     * Creates a new <code>OverlayContextProvider</code> wrapping the specified
     * provider.
     * 
     * @param defaultProvider
     *            the context provider to wrap.
     */
    public OverlayContextProvider(ContextProvider defaultProvider) {
        if (defaultProvider.getPath() != null) {
            throw new IllegalArgumentException("Can not wrap " + defaultProvider
                    + ": already mounted.");
        }
        this.defaultProvider = defaultProvider;
        defaultProvider.setEventListener(this);
        this.mountPoints = new HashMap<Path, ContextProvider>();
        this.mounted = false;
    }

    public void setEventListener(EventListener listener) {
        this.listener = listener;
    }

    public EventListener getEventListener() {
        return this.listener;
    }
    
    /* (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#getDependencyGraph()
     */
    public DependencyGraph<Path> getDependencyGraph() {
        return defaultProvider.getDependencyGraph();
    }
    
    /* (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#setDependencyGraph(org.objectweb.wildcat.dependencies.DependencyGraph)
     */
    public void setDependencyGraph(DependencyGraph<Path> dg) {
        defaultProvider.setDependencyGraph(dg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.ContextProvider#getPath()
     */
    public Path getPath() {
        return defaultProvider.getPath();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.ContextProvider#lookup(org.objectweb.wildcat.Path)
     */
    public Collection<Path> lookup(Path query) {
        assert query != null : "no query specified";
        assert query.isRelative() : "query must be relative";

        Collection<ContextProvider> contributors = findPotentialContributorsTo(query);
        Collection<Path> result = new ArrayList<Path>();
        Path absoluteQuery = defaultProvider.getPath().append(query);
        for (ContextProvider provider : contributors) {
            Path relativeQuery = absoluteQuery.subPath(provider.getPath().size() + 1);
            result.addAll(provider.lookup(relativeQuery));
        }
        // Don't forget the mount points themselves, which don't appear anywhere else.
        for (Path path : mountPoints.keySet()) {
            if (path.matches(query)) {
                result.add(defaultProvider.getPath().append(path));
            }
        }
        return result;
    }

    /**
     * Finds and returns all the local context providers (default and locally mounted)
     * which can contribute an answer to the specified query.
     * 
     * @param query
     * @return
     */
    private Collection<ContextProvider> findPotentialContributorsTo(Path query) {
        Collection<ContextProvider> candidates = new ArrayList<ContextProvider>();
        candidates.add(defaultProvider);
        candidates.addAll(mountPoints.values());
        for (Iterator iter = candidates.iterator(); iter.hasNext();) {
            if (!canContribute((ContextProvider) iter.next(), query)) {
                iter.remove();
            }
        }
        return candidates;
    }

    /**
     * Returns <code>true</code> iff the specified provider can potentially contribute
     * to the result of the query.
     * 
     * @param provider
     * @param query
     * @return
     */
    private boolean canContribute(ContextProvider provider, Path query) {
        Path mountPoint = provider.getPath();
        Path absoluteQuery = defaultProvider.getPath().append(query);
        if (absoluteQuery.size() <= mountPoint.size()) {
            // Request not "deep" enough to see below the provider's mountPoint.
            return false;
        } else {
            // Checks whether the mount-point matches the beginning of the query.
            Path queryPrefix = absoluteQuery.subPath(0, mountPoint.size() + 1);
            return mountPoint.matches(queryPrefix);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.ContextProvider#lookupAttribute(org.objectweb.wildcat.Path)
     */
    public Object lookupAttribute(Path attribute) {
        ContextProvider provider = providerFor(attribute);
        Path absoluteQuery = defaultProvider.getPath().append(attribute);
        Path relativeQuery = absoluteQuery.subPath(provider.getPath().size() + 1);
        return provider.lookupAttribute(relativeQuery);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.ContextProvider#mounted(org.objectweb.wildcat.Path)
     */
    public void mounted(Path path) {
        synchronized (this) {
            if (mounted) {
                throw new IllegalStateException("Provider already mounted.");
            } else {
                defaultProvider.mounted(path);
                mounted = true;
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.ContextProvider#unmounted()
     */
    public void unmounted() {
        synchronized (this) {
            if (mounted) {
                defaultProvider.unmounted();
                for (ContextProvider provider : mountPoints.values()) {
                    provider.unmounted();
                }
                mounted = false;
            } else {
                throw new IllegalStateException("Provider not mounted.");
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.ContextProviderContainer#mount(org.objectweb.wildcat.Path,
     *      org.objectweb.wildcat.ContextProvider)
     */
    public void mount(Path mountPoint, ContextProvider provider)
            throws InvalidMountPointException {
        assert mountPoint != null && mountPoint.isRelative() && mountPoint.isDefinite()
                && mountPoint.isResource();

        synchronized (this) {
            if (lookup(mountPoint).size() > 0) {
                throw new InvalidMountPointException(mountPoint);
            }
            ContextProvider cp = providerFor(mountPoint);
            if (cp == defaultProvider) {
                try {
                    provider.setEventListener(this);
                    provider.setDependencyGraph(defaultProvider.getDependencyGraph());
                    provider.mounted(defaultProvider.getPath().append(mountPoint));
                    mountPoints.put(mountPoint, provider);
                    notify(new PathAddedEvent(this.getPath().append(mountPoint)));
                } catch (MalformedPathException e) {
                    throw new AssertionError("Should not happen.");
                }
            } else {
                // Delegate to sub-container
                try {
                    ContextProviderContainer container = (ContextProviderContainer) cp;
                    Path fullPath = defaultProvider.getPath().append(mountPoint);
                    container.mount(fullPath.relativeTo(cp.getPath()), provider);
                } catch (MalformedPathException e) {
                    throw new AssertionError("Should not happen.");
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.ContextProviderContainer#unmount(org.objectweb.wildcat.Path)
     */
    public void unmount(Path mountPoint) throws InvalidMountPointException {
        assert mountPoint != null && mountPoint.isRelative() && mountPoint.isDefinite()
                && mountPoint.isResource();

        synchronized (this) {
            if (mountPoints.containsKey(mountPoint)) {
                ContextProvider provider = mountPoints.get(mountPoint);
                provider.unmounted();
                provider.setEventListener(null);
                provider.setDependencyGraph(null);
                mountPoints.remove(mountPoint);
                notify(new PathRemovedEvent(this.getPath().append(mountPoint)));
            } else {
                ContextProvider cp = providerFor(mountPoint);
                if (cp instanceof ContextProviderContainer) {
                    ((ContextProviderContainer) cp).unmount(mountPoint);
                } else {
                    throw new InvalidMountPointException(mountPoint);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.dependencies.Updatable#update(org.objectweb.wildcat.Path,
     *      org.objectweb.wildcat.events.PathEvent)
     */
    public void update(Path path, PathEvent cause) {
        ContextProvider cp = providerFor(path.relativeTo(getPath()));
        cp.update(path.relativeTo(cp.getPath()), cause);
    }

    /**
     * Finds the context provider which is responsible for the given relative path. Note
     * that the location denoted by the path might not actually exist, even though it is
     * logically inside the sub-tree managed by the returned povider.
     * 
     * @param path
     * @return
     */
    private ContextProvider providerFor(Path path) {
        assert path != null : "path must be non-null";
        assert path.isRelative() : "path must be relative";
        assert path.isDefinite() : "path must be definite";

        if (mountPoints.containsKey(path)) {
            return mountPoints.get(path);
        } else if (path.size() > 1) {
            return providerFor(path.getParent());
        } else {
            return defaultProvider;
        }
    }
}
