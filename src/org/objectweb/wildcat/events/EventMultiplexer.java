package org.objectweb.wildcat.events;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * This event listener forwards all the events it receives to a dynamically configurable
 * set of other listeners.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class EventMultiplexer implements EventListener {
    private Collection<EventListener> listeners;

    /**
     * Creates a new event multiplexer.
     */
    public EventMultiplexer() {
        listeners = Collections.synchronizedCollection(new HashSet<EventListener>());
    }

    /**
     * Adds a new listener to the set of targets.
     * 
     * @param listener
     *            the listener to add
     */
    public void addListener(EventListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener from the set of targets.
     * 
     * @param listener
     *            the listener to remove.
     */
    public void removeListener(EventListener listener) {
        listeners.remove(listener);
    }

    /**
     * Forwards the events to all the target listeners in turn, in a unspecified order.
     */
    public void eventOccured(List<PathEvent> evts) {
        for (EventListener l : listeners) {
            l.eventOccured(evts);
        }
    }

    /**
     * Forwards the event to all the target listeners in turn, in a unspecified order.
     */
    public void eventOccured(PathEvent evt) {
        for (EventListener l : listeners) {
            l.eventOccured(evt);
        }
    }
}
