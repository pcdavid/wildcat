package org.objectweb.wildcat.events;

import java.util.List;


/**
 * An event listener which forwards all the events it receives to its own listener.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class EventForwarder extends EventSource implements EventListener {
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
