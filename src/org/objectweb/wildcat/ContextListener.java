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
 * This interface can be implemented by clients who want to be notified of
 * changes in the context.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public interface ContextListener {
	/**
	 * Indicates that a new resource has been added in the context.
	 * 
	 * @param res
	 *            the path designating the new resource.
	 * @param timeStamp
	 *            the instant of creation of the resource.
	 */
	void resourceAdded(Path res, long timeStamp);

	/**
	 * Indicates that a resource has been removed from the context.
	 * 
	 * @param res
	 *            the path designating the location of the resource.
	 * @param timeStamp
	 *            the instant of removal of the resource.
	 */
	void resourceRemoved(Path res, long timeStamp);

	/**
	 * Indicates that a new attribute has been added in the context.
	 * 
	 * @param attr
	 *            the path designating the (location of) the attribute.
	 * @param timeStamp
	 *            the instant of creation of the attribute.
	 */
	void attributeAdded(Path attr, long timeStamp);

	/**
	 * Indicates that an attribute has been removed from the context.
	 * 
	 * @param attr
	 *            the path designating the (previous location of) the attribute.
	 * @param timeStamp
	 *            the instant of removal of the attribute.
	 */
	void attributeRemoved(Path attr, long timeStamp);

	/**
	 * Indicates that the value of an attribute has changed.
	 * 
	 * @param attr
	 *            the path designating the (location of) the attribute.
	 * @param oldValue
	 *            the previous value of the attribute.
	 * @param newValue
	 *            the new value of the attribute.
	 * @param timeStamp
	 *            the instant of the change.
	 */
	void attributeChanged(Path attr, Object oldValue, Object newValue,
			long timeStamp);

	/**
	 * Indicates that a monitored expression's value has changed.
	 * 
	 * @param cookie
	 *            the registration identifier ("cookie") corresponding to the
	 *            monitored expression.
	 * @param oldValue
	 *            the previous value of the expression.
	 * @param newValue
	 *            the new value of the expression;
	 * @param timeStamp
	 *            the time at which the expression's value changed.
	 */
	void expressionValueChanged(Object cookie, Object oldValue, Object newValue,
			long timeStamp);

	/**
	 * Indicates that a condition occurred, i.e. a monitored boolean
	 * expression's value changed from <code>false</code> to <code>true</code>.
	 * 
	 * @param cookie
	 *            the registration identifier ("cookie") corresponding to the
	 *            monitored expression.
	 * @param timeStamp
	 *            the time at which the expression's value changed.
	 */
	void conditionOccured(Object cookie, long timeStamp);
}