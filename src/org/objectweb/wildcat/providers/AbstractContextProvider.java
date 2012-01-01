package org.objectweb.wildcat.providers;

import java.util.List;

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
public abstract class AbstractContextProvider extends EventSource implements ContextProvider {
    protected DependencyGraph<Path> dependencyGraph;
    
    /* (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#getDependencyGraph()
     */
    public synchronized DependencyGraph<Path> getDependencyGraph() {
        return dependencyGraph;
    }
    
    /* (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#setDependencyGraph(org.objectweb.wildcat.dependencies.DependencyGraph)
     */
    public synchronized void setDependencyGraph(DependencyGraph<Path> dg) {
        this.dependencyGraph = dg;
    }
    
    /* (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#getEventListener()
     */
    public synchronized EventListener getEventListener() {
        return listener;
    }
    
    /* (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#setEventListener(org.objectweb.wildcat.events.EventListener)
     */
    public synchronized void setEventListener(EventListener listener) {
        this.listener = listener;
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
     * 
     * @see org.objectweb.wildcat.events.EventListener#eventOccured(org.objectweb.wildcat.events.PathEvent)
     */
    public void eventOccured(PathEvent evt) {
        notify(evt);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.events.EventListener#eventOccured(java.util.List)
     */
    public void eventOccured(List<PathEvent> evts) {
        notify(evts);
    }
}
