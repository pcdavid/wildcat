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
package org.objectweb.wildcat.providers;

import java.util.Map;

/**
 * A <code>Sampler</code> is an object which can observe some aspect of the context and
 * return the result as a set of named values.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public interface Sampler {
    /**
     * Observe some element of the context and return it current status as a set of named
     * values.
     * 
     * @return the result of the observation.
     */
    Map<String, Object> sample();
}
