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
package org.objectweb.wildcat;

/**
 * Adapter class for {@link org.objectweb.wildcat.ContextListener}: provides an empty
 * implementation of all the methods. Can easily be inherited to specialize only some of
 * the methods.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class ContextListenerAdapter implements ContextListener {
    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.ContextListener#attributeAdded(org.objectweb.wildcat.Path,
     *      long)
     */
    public void attributeAdded(Path attr, long timeStamp) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.ContextListener#attributeRemoved(org.objectweb.wildcat.Path,
     *      long)
     */
    public void attributeRemoved(Path attr, long timeStamp) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.ContextListener#attributeChanged(org.objectweb.wildcat.Path,
     *      java.lang.Object, java.lang.Object, long)
     */
    public void attributeChanged(Path attr, Object oldValue, Object newValue,
            long timeStamp) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.ContextListener#resourceAdded(org.objectweb.wildcat.Path,
     *      long)
     */
    public void resourceAdded(Path res, long timeStamp) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.ContextListener#resourceRemoved(org.objectweb.wildcat.Path,
     *      long)
     */
    public void resourceRemoved(Path res, long timeStamp) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.ContextListener#expressionValueChanged(java.lang.Object,
     *      java.lang.Object, java.lang.Object, long)
     */
    public void expressionValueChanged(Object cookie, Object oldValue, Object newValue,
            long timeStamp) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.ContextListener#conditionOccured(java.lang.Object, long)
     */
    public void conditionOccured(Object cookie, long timeStamp) {
    }
}
