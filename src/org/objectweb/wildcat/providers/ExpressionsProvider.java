package org.objectweb.wildcat.providers;

import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;

import org.objectweb.wildcat.Context;
import org.objectweb.wildcat.ContextProvider;
import org.objectweb.wildcat.Path;
import org.objectweb.wildcat.dependencies.DependencyGraph;
import org.objectweb.wildcat.events.EventListener;
import org.objectweb.wildcat.events.PathEvent;
import org.objectweb.wildcat.expressions.Interpreter;

/**
 * Custom context provider used to monitor watched expressions.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class ExpressionsProvider implements ContextProvider {
    /**
     * The underlying context provider.
     */
    private DynamicContextProvider backend;
    /**
     * The index used to generated synthetic attributes names.
     */
    private int expressionIndex = 0;

    public ExpressionsProvider(Interpreter inter) {
        backend = new DynamicContextProvider(inter);
    }

    public ExpressionsProvider(ScheduledExecutorService scheduler, Interpreter inter) {
        backend = new DynamicContextProvider(scheduler, inter);
    }
    
    /* (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#getDependencyGraph()
     */
    public DependencyGraph<Path> getDependencyGraph() {
        return backend.getDependencyGraph();
    }
    
    /* (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#setDependencyGraph(org.objectweb.wildcat.dependencies.DependencyGraph)
     */
    public void setDependencyGraph(DependencyGraph<Path> dg) {
        backend.setDependencyGraph(dg);
    }

    /**
     * Creates a new synthetic attribute with the supplied definition.
     * 
     * @param expression
     *            the definition of the attribute to create.
     * @return the full path of the created attribute.
     */
    public Path createExpressionAttribute(String expression) {
        if (backend.getPath() == null) {
            throw new IllegalStateException();
        }
        synchronized (this) {
            expressionIndex++;
        }
        Path path = Context.createPath("#expression_" + expressionIndex);
        backend.createAttribute(path, null);
        backend.setDefinition(path, expression);
        return backend.getPath().append(path);
    }

    /**
     * Deletes a synthetic attribute.
     * 
     * @param path
     *            the full path to the attribute to delete (as returned from
     *            {@link #createExpressionAttribute(String)}).
     */
    public void deleteExpressionAttribute(Path path) {
        backend.delete(path.relativeTo(backend.getPath()));
    }
    
    /*
     * (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#getPath()
     */
    public Path getPath() {
        return backend.getPath();
    }

    /*
     * (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#getEventListener()
     */
    public EventListener getEventListener() {
        return backend.getEventListener();
    }

    /*
     * (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#setEventListener(org.objectweb.wildcat.events.EventListener)
     */
    public void setEventListener(EventListener listener) {
        backend.setEventListener(listener);
    }

    /*
     * (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#lookup(org.objectweb.wildcat.Path)
     */
    public Collection<Path> lookup(Path query) {
        return backend.lookup(query);
    }

    /*
     * (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#lookupAttribute(org.objectweb.wildcat.Path)
     */
    public Object lookupAttribute(Path attribute) {
        return backend.lookupAttribute(attribute);
    }

    /*
     * (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#mounted(org.objectweb.wildcat.Path)
     */
    public void mounted(Path path) {
        backend.mounted(path);
    }

    /*
     * (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#unmounted()
     */
    public void unmounted() {
        backend.unmounted();
    }
    
    /* (non-Javadoc)
     * @see org.objectweb.wildcat.dependencies.Updatable#update(org.objectweb.wildcat.Path, org.objectweb.wildcat.events.PathEvent)
     */
    public void update(Path path, PathEvent cause) {
        backend.update(path, cause);
    }
}
