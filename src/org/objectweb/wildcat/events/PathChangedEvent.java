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
 * This kind of event indicates that the value of an attribute changed.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class PathChangedEvent extends PathEvent {
    /**
     * The previous value of the attribute, before the change.
     */
    private Object oldValue;
    /**
     * The new value of the attribute, after the change.
     */
    private Object newValue;

    /**
     * Creates a new <code>PathChangedEvent</code>.
     * 
     * @param path
     *            the path denoting the attribute which changed.
     * @param oldValue
     *            the previous value of the attribute, before the change.
     * @param newValue
     *            the new value of the attribute, after the change.
     * @param timeStamp
     *            the time at which the attribute value changed.
     */
    public PathChangedEvent(Path path, Object oldValue, Object newValue, long timeStamp) {
        super(path, timeStamp);
        if (! path.isAttribute()) {
            throw new IllegalArgumentException("Path-changed events can only occur for attributes.");
        }
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
    
    public PathChangedEvent(Path path, Object oldValue, Object newValue) {
        this(path, oldValue, newValue, TimeUtil.now());
    }

    /**
     * @return the previous value of the attribute, before the change.
     */
    public Object getNewValue() {
        return newValue;
    }

    /**
     * @return the new value of the attribute, after the change.
     */
    public Object getOldValue() {
        return oldValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.events.PathEvent#toString()
     */
    @Override
    public String toString() {
        return "~" + path + " : " + oldValue + " -> " + newValue;
    }
}
