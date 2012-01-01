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

import java.util.Collection;
import java.util.EnumSet;

import org.objectweb.wildcat.dependencies.DependencyManager;
import org.objectweb.wildcat.events.EventMultiplexer;
import org.objectweb.wildcat.expressions.BasicInterpreter;
import org.objectweb.wildcat.expressions.Interpreter;
import org.objectweb.wildcat.providers.DynamicContextProvider;
import org.objectweb.wildcat.providers.EmptyContextProvider;
import org.objectweb.wildcat.providers.ExpressionsProvider;
import org.objectweb.wildcat.providers.OverlayContextProvider;

/**
 * This class is the main façade used by clients to interact with WildCAT.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class Context {
    /**
     * Utility method to obtain the root {@link Path}.
     */
    public static Path getRootPath() {
        return SimplePath.getRoot();
    }

    /**
     * Utility method to construct a {@link Path} from a string.
     */
    public static Path createPath(String path) {
        return new SimplePath(path);
    }

    /**
     * Container for the whole context.
     */
    private OverlayContextProvider root;

    /**
     * Special provider mounted at /_expressions to implement
     * {@link EventKind#EXPRESSION_CHANGED} and {@link EventKind#CONDITION_OCCURED}.
     */
    private ExpressionsProvider expressionsProvider;

    /**
     * The interpreter used to evaluate watched expressions. Also used by
     * {@link DynamicContextProvider}s created using
     * {@link #createDynamicContextProvider()} to evaluate synthetic attributes.
     */
    private Interpreter interpreter;

    /**
     * This object receives all the event notifications from anywhere in the context and
     * dispatches them to the appropriate processors (i.e. the dependency manager and the
     * client subscriptions manager).
     */
    private EventMultiplexer eventMuxer;

    /**
     * Global dependency tracker: propagates the appropriate update requests for paths
     * which depend on other paths.
     */
    private DependencyManager dependencyManager;

    /**
     * Client subscriptions manager: keeps track of user-level subscriptions and
     * translates internal {@link PathEvent}s into the appropriate calls to registered
     * {@link ContextListener}s.
     */
    private SubscriptionManager subscriptionsManager;

    /**
     * Creates a new WildCAT context, initialy empty.
     */
    public Context() {
        interpreter = new BasicInterpreter();
        interpreter.getEnvironment().defineVariable("_context", this);
        eventMuxer = new EventMultiplexer();

        subscriptionsManager = new SubscriptionManager();
        startDaemon("Subscriptions Manager", subscriptionsManager);
        eventMuxer.addListener(subscriptionsManager);

        root = new OverlayContextProvider(new EmptyContextProvider());
        root.setEventListener(eventMuxer);
        dependencyManager = new DependencyManager(root);
        startDaemon("Dependency Manager", dependencyManager);
        eventMuxer.addListener(dependencyManager);
        root.mounted(getRootPath());

        expressionsProvider = new ExpressionsProvider(interpreter);
        try {
            mount(createPath("/_expressions"), expressionsProvider);
        } catch (InvalidMountPointException e) {
            throw new AssertionError("Error while mounting /_expressions.");
        }
    }

    /**
     * Utility method to launch a task inside a named deamon thread.
     */
    private void startDaemon(String name, Runnable task) {
        Thread th = new Thread(task, name);
        th.setDaemon(true);
        th.start();
    }

    public DynamicContextProvider createDynamicContextProvider() {
        return new DynamicContextProvider(interpreter);
    }

    /**
     * Lookup this <code>ContextProvider</code> for the specified location(s).
     * 
     * @param query
     *            a relative path denoting the set of locations in this provider to look
     *            for.
     * @return a collection of all the absolute locations which currently exist inside
     *         this provider and which match the query.
     * @throws IllegalArgumentException
     *             if the query is not absolute.
     */
    public Collection<Path> lookup(Path query) {
        if (query.isRelative()) {
            throw new IllegalArgumentException("Context expects absolute queries.");
        } else {
            return root.lookup(query.subPath(1));
        }
    }

    /**
     * Convenience shortcut for <code>lookup(Context.createPath(query))</code>.
     * 
     * @throws MalformedPathException
     *             if the query supplied as a string is not a valid {@link Path}.
     */
    public Collection<Path> lookup(String query) {
        return lookup(new SimplePath(query));
    }

    /**
     * Looks up the <em>value</em> of an attribute.
     * 
     * @param attribute
     *            the location of the attribute to look for, relative to this provider.
     * @return the value of the attribute, it it exists, or <code>null</code> otherwise.
     * @throws IllegalArgumentException
     *             if the query is not absolute.
     */
    public Object lookupAttribute(Path attribute) {
        if (attribute.isRelative()) {
            throw new IllegalArgumentException("Context expects absolute queries.");
        } else if (attribute.size() > 1) {
            return root.lookupAttribute(attribute.subPath(1));
        } else {
            return null;
        }
    }

    /**
     * Convenience shortcut for <code>lookupAttribute(Context.createPath(query))</code>.
     * 
     * @throws MalformedPathException
     *             if the query supplied as a string is not a valid {@link Path}.
     */
    public Object lookupAttribute(String attribute) throws MalformedPathException {
        return lookupAttribute(new SimplePath(attribute));
    }

    /**
     * Convenience shortcut for
     * <code>mount(Context.createPath(mountPoint), provider)</code>.
     * 
     * @throws MalformedPathException
     *             if the mount point supplied as a string is not a valid {@link Path}.
     */
    public void mount(String mountPoint, ContextProvider provider)
            throws InvalidMountPointException {
        mount(createPath(mountPoint), provider);
    }

    /**
     * Mounts a new provider in the context at the given location. The mount point must be
     * definite, absolute, and represent a resource which does not exist but whose parent
     * exists (this is different from the Unix semantics where the mount point is an
     * existing but empty directory).
     * 
     * @param mountPoint
     *            the point at which to mount the provider
     * @param provider
     *            the provider to mount
     * @throws InvalidMountPointException
     *             if the mount point is invalid or if the provider responsible for this
     *             part of the context does not support sub-providers.
     */
    public void mount(Path mountPoint, ContextProvider provider)
            throws InvalidMountPointException {
        if (mountPoint.isRelative()) {
            throw new InvalidMountPointException(mountPoint);
        } else {
            root.mount(mountPoint.subPath(1), provider);
        }
    }

    /**
     * Convenience shortcut for <code>unmount(Context.createPath(mountPoint))</code>.
     * 
     * @throws MalformedPathException
     *             if the mount point provided as a string is not a valid {@link Path}.
     */
    public void unmount(String mountPoint) throws InvalidMountPointException {
        unmount(createPath(mountPoint));
    }

    /**
     * Unmounts the provider mounted at the given location.
     * 
     * @param mountPoint
     * @throws InvalidMountPointException
     */
    public void unmount(Path mountPoint) throws InvalidMountPointException {
        if (mountPoint.isRelative()) {
            throw new IllegalArgumentException("Context expects absolute paths.");
        } else {
            root.unmount(mountPoint.subPath(1));
        }
    }

    /**
     * Convenience shortcut for <code>register(EnumSet.of(kind), path, listener)</code>
     * to use when registering for only one kind of event.
     */
    public Object register(EventKind kind, Path path, ContextListener listener) {
        return register(EnumSet.of(kind), path, listener);
    }

    /**
     * @param evtKinds
     * @param path
     * @param listener
     * @return
     */
    public Object register(EnumSet<EventKind> evtKinds, Path path,
            ContextListener listener) {
        return subscriptionsManager.register(evtKinds, path, listener);
    }

    /**
     * Convenience shortcut for
     * <code>registerExpression(EnumSet.of(kind), expression, listener)</code> to use
     * when registering for only one kind of event.
     */
    public Object registerExpression(EventKind kind, String expression,
            ContextListener listener) {
        return registerExpression(EnumSet.of(kind), expression, listener);
    }

    /**
     * @param evtKinds
     * @param expression
     * @param listener
     * @return
     */
    public Object registerExpression(EnumSet<EventKind> evtKinds, String expression,
            ContextListener listener) {
        return register(evtKinds, expressionsProvider
                .createExpressionAttribute(expression), listener);
    }

    /**
     * @param cookie
     */
    public void unregister(Object cookie) {
        Path path = subscriptionsManager.unregister(cookie);
        if (path != null) {
            expressionsProvider.deleteExpressionAttribute(path);
        }
    }
}
