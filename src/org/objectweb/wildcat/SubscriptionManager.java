package org.objectweb.wildcat;

import static org.objectweb.wildcat.EventKind.*;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.wildcat.events.EventBuffer;
import org.objectweb.wildcat.events.EventListener;
import org.objectweb.wildcat.events.PathAddedEvent;
import org.objectweb.wildcat.events.PathChangedEvent;
import org.objectweb.wildcat.events.PathEvent;
import org.objectweb.wildcat.events.PathRemovedEvent;

/**
 * This class manages user-level subscriptions: it keeps track of these subscriptions and
 * asynchronously notifies the appropriate {@link ContextListener}s when it receives
 * watched events.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
class SubscriptionManager implements EventListener, Runnable {
    private EventBuffer buffer;
    private Map<Object, Subscription> subscriptions;

    /**
     * Creates a new <code>SubscriptionManager</code>.
     */
    public SubscriptionManager() {
        this.buffer = new EventBuffer();
        this.subscriptions = Collections
                .synchronizedMap(new HashMap<Object, Subscription>());
    }

    /**
     * Registers a new user-level subscription.
     * 
     * @param evtKinds
     *            the kinds of events the user is interested in.
     * @param location
     *            the locations at which the events interest the user.
     * @param listener
     *            the listener to notify when a matching event occurs.
     * @return an opaque identifier (cookie) to represent this subscription, to be used to
     *         cancel it using {@link #unregister(Object)}.
     */
    public Object register(EnumSet<EventKind> evtKinds, Path location,
            ContextListener listener) {
        assert evtKinds != null && !evtKinds.isEmpty();
        assert location != null && location.isAbsolute();
        assert listener != null;
        Object cookie = new Object();
        Subscription sub = new Subscription(evtKinds, location, listener);
        subscriptions.put(cookie, sub);
        return cookie;
    }

    /**
     * Cancels a user-level subscription.
     * 
     * @param cookie
     *            the subscription identifier, as returned from
     *            {@link #register(EnumSet, Path, ContextListener)}.
     * @return the path of the synthetic expression which was used to implement this
     *         subscription, which can be deleted, or <code>null</code> if the
     *         subscription did not use a synthetic attribute.
     */
    public Path unregister(Object cookie) {
        Subscription sub = subscriptions.get(cookie);
        if (sub != null) {
            subscriptions.remove(cookie);
            if (sub.matches(CONDITION_OCCURED) || sub.matches(EXPRESSION_CHANGED)) {
                return sub.path;
            } else {
                return null;
            }
        } else {
            throw new IllegalArgumentException("Invalid subscription identifier.");
        }
    }

    /**
     * Buffers the event for asynchronous processing.
     */
    public void eventOccured(PathEvent evt) {
        buffer.eventOccured(evt);
    }

    /**
     * Buffers the events for asynchronous processing.
     */
    public void eventOccured(List<PathEvent> evts) {
        buffer.eventOccured(evts);
    }

    /**
     * Takes events from the buffer in turn, and notifies all the registered
     * {@link ContextListener} whose subscription matches the event.
     */
    public void run() {
        while (true) {
            try {
                notifyClientListeners(buffer.take());
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void notifyClientListeners(PathEvent evt) {
        assert evt != null;
        for (Object cookie : subscriptions.keySet()) {
            Subscription sub = subscriptions.get(cookie);
            if (evt.getPath().matches(sub.path)) {
                sub.notify(evt, cookie);
            }
        }
    }

    /**
     * Represents a user-level subscription to a class of context events.
     * 
     * @author Pierre-Charles David <pcdavid@gmail.com>
     */
    private class Subscription {
        /**
         * The absolute path of the events of interest. May be a pattern.
         */
        private Path path;
        /**
         * The kinds of events the subscriber is interested in at this location.
         */
        private EnumSet<EventKind> kinds;
        /**
         * The listener to notify when a matching event occurs.
         */
        private ContextListener listener;

        /**
         * Creates a new subscription.
         */
        public Subscription(EnumSet<EventKind> kinds, Path path, ContextListener listener) {
            assert kinds != null && !kinds.isEmpty() : "no event kinds to notify";
            assert path != null && path.isAbsolute();
            assert listener != null : "no listener to notify";
            this.kinds = kinds;
            this.path = path;
            this.listener = listener;
        }

        /**
         * Tests whether a given kind of event is relevant for this subscription.
         */
        public boolean matches(EventKind kind) {
            return kinds.contains(kind);
        }

        /**
         * Calls the appropriate method on the listener depending on the actual type of
         * the event and the kinds of events the subscriber is interested in.
         * 
         * @param evt
         *            the internal event
         * @param cookie
         *            the optional identifier of the watched expression
         */
        public void notify(PathEvent evt, Object cookie) {
            if (evt instanceof PathAddedEvent) {
                notifyAddition((PathAddedEvent) evt);
            } else if (evt instanceof PathRemovedEvent) {
                notifyRemoval((PathRemovedEvent) evt);
            } else if (evt instanceof PathChangedEvent) {
                notifyChange((PathChangedEvent) evt, cookie);
            }
        }

        private void notifyAddition(PathAddedEvent evt) {
            if (evt.getPath().isResource() && matches(RESOURCE_ADDED)) {
                listener.resourceAdded(evt.getPath(), evt.getTimeStamp());
            } else if (evt.getPath().isAttribute() && matches(ATTRIBUTE_ADDED)) {
                listener.attributeAdded(evt.getPath(), evt.getTimeStamp());
            }
        }

        private void notifyRemoval(PathRemovedEvent evt) {
            if (evt.getPath().isResource() && matches(RESOURCE_REMOVED)) {
                listener.resourceRemoved(evt.getPath(), evt.getTimeStamp());
            } else if (evt.getPath().isAttribute() && matches(ATTRIBUTE_REMOVED)) {
                listener.attributeRemoved(evt.getPath(), evt.getTimeStamp());
            }
        }

        private void notifyChange(PathChangedEvent change, Object cookie) {
            if (matches(ATTRIBUTE_CHANGED)) {
                listener.attributeChanged(change.getPath(), change.getOldValue(), change
                        .getNewValue(), change.getTimeStamp());
            }
            if (matches(EXPRESSION_CHANGED)) {
                listener.expressionValueChanged(cookie, change.getOldValue(), change
                        .getNewValue(), change.getTimeStamp());
            }
            if (matches(CONDITION_OCCURED) && changedToTrue(change)) {
                listener.conditionOccured(cookie, change.getTimeStamp());
            }
        }

        private boolean changedToTrue(PathChangedEvent change) {
            Object oldValue = change.getOldValue();
            Object newValue = change.getNewValue();
            return (Boolean.FALSE.equals(oldValue) || oldValue == null)
                    && Boolean.TRUE.equals(newValue);
        }
    }
}
