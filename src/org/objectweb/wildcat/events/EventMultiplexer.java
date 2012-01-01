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

    public EventMultiplexer() {
        listeners = Collections.synchronizedCollection(new HashSet<EventListener>());
    }

    public void addListener(EventListener listener) {
        listeners.add(listener);
    }

    public void removeListener(EventListener listener) {
        listeners.remove(listener);
    }

    public void eventOccured(List<PathEvent> evts) {
        for (EventListener l : listeners) {
            l.eventOccured(evts);
        }
    }

    public void eventOccured(PathEvent evt) {
        for (EventListener l : listeners) {
            l.eventOccured(evt);
        }
    }
}
