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
package example;

import org.objectweb.wildcat.ContextListener;
import org.objectweb.wildcat.Path;
import org.objectweb.wildcat.TimeUtil;

/**
 * Simple implementation of {@link org.objectweb.wildcat.ContextListener} for example
 * programs, which logs all the events to stderr.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class LoggingContextListener implements ContextListener {
    public void attributeAdded(Path attr, long timeStamp) {
        log("Attribute added: " + attr.toString() + " @ " + TimeUtil.format(timeStamp));
    }

    public void attributeRemoved(Path attr, long timeStamp) {
        log("Attribute removed: " + attr.toString() + " @ " + TimeUtil.format(timeStamp));
    }

    public void attributeChanged(Path attr, Object oldValue, Object newValue,
            long timeStamp) {
        log("Attribute changed: " + attr.toString() + " (" + oldValue + " => " + newValue
                + ") @ " + TimeUtil.format(timeStamp));
    }

    public void resourceAdded(Path res, long timeStamp) {
        log("Resource added: " + res + " @ " + TimeUtil.format(timeStamp));
    }

    public void resourceRemoved(Path res, long timeStamp) {
        log("Resource removed: " + res + " @ " + TimeUtil.format(timeStamp));
    }

    public void conditionOccured(Object cookie, long timeStamp) {
        log("Condition " + cookie + " occured @ " + TimeUtil.format(timeStamp));
    }

    public void expressionValueChanged(Object cookie, Object oldValue, Object newValue,
            long timeStamp) {
        log("Expression " + cookie + " changed" + " (" + oldValue + " => " + newValue
                + ") @ " + TimeUtil.format(timeStamp));
    }

    private void log(String message) {
        System.err.println(message);
    }
}
