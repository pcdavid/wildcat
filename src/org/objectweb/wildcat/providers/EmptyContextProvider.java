package org.objectweb.wildcat.providers;

import java.util.Collection;
import java.util.Collections;

import org.objectweb.wildcat.ContextProvider;
import org.objectweb.wildcat.Path;
import org.objectweb.wildcat.dependencies.DependencyGraph;
import org.objectweb.wildcat.events.EventListener;
import org.objectweb.wildcat.events.EventSource;
import org.objectweb.wildcat.events.PathEvent;

/**
 * Null implementation of {@link ContextProvider}: represents an empty tree with no
 * (sub)resources, no attributes and no events.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class EmptyContextProvider extends EventSource implements ContextProvider {
    private Path path;
    private DependencyGraph<Path> dependencyGraph;

    /*
     * (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#getPath()
     */
    public Path getPath() {
        return path;
    }

    /*
     * (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#getEventListener()
     */
    public EventListener getEventListener() {
        return listener;
    }

    /*
     * (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#lookup(org.objectweb.wildcat.Path)
     */
    public Collection<Path> lookup(Path query) {
        return Collections.emptySet();
    }

    /*
     * (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#lookupAttribute(org.objectweb.wildcat.Path)
     */
    public Object lookupAttribute(Path attribute) {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#mounted(org.objectweb.wildcat.Path)
     */
    public void mounted(Path path) {
        this.path = path;
    }

    /*
     * (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#update(org.objectweb.wildcat.Path, org.objectweb.wildcat.events.PathEvent)
     */
    public void update(Path path, PathEvent cause) {
        // Ignore
    }

    /*
     * (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#setEventListener(org.objectweb.wildcat.events.EventListener)
     */
    public void setEventListener(EventListener listener) {
        this.listener = listener;
    }

    /*
     * (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#getDependencyGraph()
     */
    public DependencyGraph<Path> getDependencyGraph() {
        return dependencyGraph;
    }

    /*
     * (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#setDependencyGraph(org.objectweb.wildcat.dependencies.DependencyGraph)
     */
    public void setDependencyGraph(DependencyGraph<Path> dg) {
        this.dependencyGraph = dg;
    }

    /*
     * (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#unmounted()
     */
    public void unmounted() {
        this.path = null;
    }
}
