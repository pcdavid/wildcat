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
package org.objectweb.wildcat.events;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * An <code>EventBuffer</code> buffers the path-related events it receives through its
 * {@link EventListener} interface in an internal queue/FIFO, and makes them avaiable
 * asynchronously using its {@link EventQueue} interface. In effect, it a a push-to-pull
 * converter.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class EventBuffer implements EventListener, EventQueue {
    /**
     * The internal FIFO.
     */
    private BlockingQueue<PathEvent> queue;

    /**
     * Creates a new <code>EventBuffer</code> with a specific FIFO size.
     * 
     * @param size
     *            the size of the FIFO.
     */
    public EventBuffer(int size) {
        this.queue = new ArrayBlockingQueue<PathEvent>(size);
    }

    /**
     * Creates a new <code>EventBuffer</code> with a default FIFO size.
     */
    public EventBuffer() {
        this(32);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.events.PathEventListener#eventOccured(org.objectweb.wildcat.events.PathEvent)
     */
    public void eventOccured(PathEvent evt) {
        try {
            queue.put(evt);
        } catch (InterruptedException e) {
            // Accept interruption. Nothing special to do.
        }
    }
    
    /* (non-Javadoc)
     * @see org.objectweb.wildcat.events.EventListener#eventOccured(java.util.List)
     */
    public void eventOccured(List<PathEvent> evts) {
        for (PathEvent event : evts) {
            eventOccured(event);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.events.EventQueue#take()
     */
    public PathEvent take() throws InterruptedException {
        return queue.take();
    }
}
