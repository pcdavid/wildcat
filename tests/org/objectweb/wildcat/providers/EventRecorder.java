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
package org.objectweb.wildcat.providers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.objectweb.wildcat.events.EventListener;
import org.objectweb.wildcat.events.PathChangedEvent;
import org.objectweb.wildcat.events.PathEvent;

/**
 * This <code>EventListener</code> records all the events it receives and provides
 * methods to test and clear its recorded trace. It is designed to be used in unit tests
 * to check that the correct events have been generated.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class EventRecorder implements EventListener {
    /**
     * All the recorded events since the recorder was created or cleared.
     */
    private List<PathEvent> events = new ArrayList<PathEvent>();

    /*
     * (non-Javadoc)
     * @see org.objectweb.wildcat.events.EventListener#eventOccured(org.objectweb.wildcat.events.PathEvent)
     */
    public void eventOccured(PathEvent evt) {
        events.add(evt);
    }

    /*
     * (non-Javadoc)
     * @see org.objectweb.wildcat.events.EventListener#eventOccured(java.util.List)
     */
    public void eventOccured(List<PathEvent> evts) {
        events.addAll(evts);
    }

    public void clear() {
        events.clear();
    }

    public int size() {
        return events.size();
    }

    public boolean contains(PathEvent evt) {
        return events.contains(evt);
    }

    public boolean containsAll(Collection<?> c) {
        return events.containsAll(c);
    }

    public int indexOf(Object o) {
        return events.indexOf(o);
    }

    public boolean isEmpty() {
        return events.isEmpty();
    }

    public List<PathEvent> getEvents() {
        return events;
    }

    public boolean recorded(PathEvent... expected) {
        if (events.size() != expected.length) {
            return false;
        }
        int i = 0;
        for (PathEvent event : events) {
            if (!equiv(expected[i], event)) {
                return false;
            }
            i++;
        }
        return true;
    }

    private boolean equiv(PathEvent evt1, PathEvent evt2) {
        if (!evt1.getPath().equals(evt2.getPath())) {
            return false;
        } else if (!evt1.getClass().equals(evt2.getClass())) {
            return false;
        } else if (evt1 instanceof PathChangedEvent) {
            PathChangedEvent change1 = (PathChangedEvent) evt1;
            PathChangedEvent change2 = (PathChangedEvent) evt2;
            return equals(change1.getOldValue(), change2.getOldValue())
                    && equals(change1.getNewValue(), change2.getNewValue());
        }
        return true;
    }

    private boolean equals(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        } else {
            return o1.equals(o2);
        }
    }

}