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
 * Represent the different kinds of events in a context which clients might be interested
 * in. For each of these codes, there is one corresponding method in
 * {@link org.objectweb.wildcat.ContextListener}. Clients which register their intereset
 * for some kinds of events will be notified of their occurence through these methods.
 * 
 * @see org.objectweb.wildcat.ContextListener
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public enum EventKind {
    /**
     * Indicates the addition of a resource.
     * 
     * @see ContextListener#resourceAdded(Path, long)
     */
    RESOURCE_ADDED,

    /**
     * Indicates the removal of a resource.
     * 
     * @see ContextListener#resourceRemoved(Path, long)
     */
    RESOURCE_REMOVED,

    /**
     * Indicates the addition of an attribute.
     * 
     * @see ContextListener#attributeAdded(Path, long)
     */
    ATTRIBUTE_ADDED,

    /**
     * Indicates the removal of an attribute.
     * 
     * @see ContextListener#attributeRemoved(Path, long)
     */
    ATTRIBUTE_REMOVED,

    /**
     * Indicates the change of the value of an attribute.
     * 
     * @see ContextListener#attributeChanged(Path, Object, Object, long)
     */
    ATTRIBUTE_CHANGED,

    /**
     * Indicates that an expression's value has changed.
     * 
     * @see ContextListener#expressionValueChanged(long, Object, Object, long)
     */
    EXPRESSION_CHANGED,

    /**
     * Indicates that a boolean expression has occured (value changed from
     * <code>false</code> to <code>true</code>).
     * 
     * @see ContextListener#conditionOccured(long, long)
     */
    CONDITION_OCCURED;
}
