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

import org.objectweb.wildcat.Path;

/**
 * Root class for all the concrete event types occurring in a WildCAT context.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public abstract class PathEvent {
    /**
     * The path at which the event occured.
     */
    protected Path path;
    /**
     * The time at which the event occurred.
     */
    protected long timeStamp;

    /**
     * Creates a new <code>PathEvent</code>.
     * 
     * @param path
     *            the path at which the event occured.
     * @param timeStamp
     *            the time at which the event occurred.
     */
    public PathEvent(Path path, long timeStamp) {
        if (path.isPattern()) {
            throw new IllegalArgumentException("Events can only occur on definite paths.");
        } else if (path.isRelative()) {
            throw new IllegalArgumentException(
                    "Events must be specified using absolute paths.");
        }
        this.path = path;
        this.timeStamp = timeStamp;
    }

    /**
     * @return the path at which the event occured.
     */
    public Path getPath() {
        return path;
    }

    /**
     * @return the time at which the event occurred.
     */
    public long getTimeStamp() {
        return timeStamp;
    }
}
