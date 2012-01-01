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
package org.objectweb.wildcat.dependencies;

import java.util.Collection;
import java.util.List;

import org.objectweb.wildcat.ContextProvider;
import org.objectweb.wildcat.Path;
import org.objectweb.wildcat.events.EventBuffer;
import org.objectweb.wildcat.events.EventListener;
import org.objectweb.wildcat.events.PathEvent;

/**
 * This class keeps track of dependencies between paths (e.g. synthetic paths defined in
 * terms of others), and asynchronously requests the appropriate updates when it receives
 * events.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class DependencyManager extends DependencyGraphImpl<Path> implements
        EventListener, Runnable {
    /**
     * The buffer when received events are stored for further (asynchronous) processing.
     */
    private EventBuffer buffer;

    /**
     * The target for update requests; should be able to route the request to the actual
     * context provider responsible for the path to update.
     */
    private ContextProvider target;

    /**
     * Creates a new <code>DependencyManager</code>.
     * 
     * @param target
     */
    public DependencyManager(ContextProvider target) {
        assert target != null;
        this.buffer = new EventBuffer();
        this.target = target;
    }

    /**
     * Buffers the event for further processing.
     */
    public void eventOccured(PathEvent evt) {
        buffer.eventOccured(evt);
    }

    /**
     * Buffers the event for further processing.
     */
    public void eventOccured(List<PathEvent> evts) {
        buffer.eventOccured(evts);
    }

    /**
     * Takes each buffered event in turn, and requests an update for all the paths which
     * have declared dependencies on it.
     */
    public void run() {
        while (true) {
            try {
                PathEvent evt = buffer.take();
                Collection<Path> deps = getAllDependingOn(evt.getPath());
                for (Path path : deps) {
                    target.update(path, evt);
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
