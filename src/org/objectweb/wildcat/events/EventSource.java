package org.objectweb.wildcat.events;

import java.util.ArrayList;
import java.util.List;


/**
 * Provides some common code for classes which must notify events to an
 * {@link EventListener}.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class EventSource {
    /**
     * The listener we notify.
     */
    protected EventListener listener;
    
    /**
     * Convenience method to notify the listener using the most appropriate method
     * depending on the number of events, and avoiding the inconvenience of creating a
     * collection.
     */
    protected synchronized void notify(PathEvent... events) {
        if (listener == null || events.length == 0) {
            return;
        } else if (events.length == 1) {
            listener.eventOccured(events[0]);
        } else {
            List<PathEvent> list = new ArrayList<PathEvent>();
            for (PathEvent evt : events) {
                list.add(evt);
            }
            listener.eventOccured(list);
        }
    }
    
    protected synchronized void notify(List<PathEvent> events) {
        if (listener != null) {
            listener.eventOccured(events);
        }
    }
}
