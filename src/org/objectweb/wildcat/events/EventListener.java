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

/**
 * A listener interface for all kinds of path-related events.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public interface EventListener {
    /**
     * Indicates that a path-related event has occured.
     * 
     * @param evt
     *            the event that occured.
     */
    void eventOccured(PathEvent evt);

    /**
     * Indicates that a series of events occured. This method is to be used for "bulk"
     * notification, when a series of events happen in a short time. Some implementers may
     * have more efficient ways of dealing with such a global notification than a series
     * of primitive ones (e.g. when sending the events over a network).
     * 
     * @param evts
     *            the list of events to be notified, which must be ordered by their
     *            timestamp.
     */
    void eventOccured(List<PathEvent> evts);
}
