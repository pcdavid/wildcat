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
import org.objectweb.wildcat.TimeUtil;

/**
 * This kind of event indicates that a new element has appeared in the context. This new
 * element, denoted by the path of the event, can be either a resource or an attribute.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class PathAddedEvent extends PathEvent {
    /**
     * Creates a new <code>PathAddedEvent</code>.
     * 
     * @param path
     *            a path denoting the new element
     * @param timeStamp
     *            the time at which the new element appeared in the context.
     */
    public PathAddedEvent(Path path, long timeStamp) {
        super(path, timeStamp);
    }
    
    public PathAddedEvent(Path path) {
        this(path, TimeUtil.now());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "+" + path;
    }
}
