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
 * @author Pierre-Charles David <pcdavid@gmail.com>
 *
 */
public class EmptyContextProvider extends EventSource implements ContextProvider {
    private Path path;
    private DependencyGraph<Path> dependencyGraph;

    public Path getPath() {
        return path;
    }
    
    public EventListener getEventListener() {
        return listener;
    }

    public Collection<Path> lookup(Path query) {
        return Collections.emptySet();
    }

    public Object lookupAttribute(Path attribute) {
        return null;
    }

    public void mounted(Path path) {
        this.path = path;
    }
    
    public void update(Path path, PathEvent cause) {
        // Ignore
    }

    public void setEventListener(EventListener listener) {
        this.listener = listener;
    }

    public DependencyGraph<Path> getDependencyGraph() {
        return dependencyGraph;
    }
    
    public void setDependencyGraph(DependencyGraph<Path> dg) {
        this.dependencyGraph = dg;
    }

    public void unmounted() {
        this.path = null;
    }
}
