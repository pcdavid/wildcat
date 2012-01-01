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
 * Contact: Pierre-Charles David <PierreCharles.David@francetelecom.com>
 */
package org.objectweb.wildcat.expressions.functions;

import org.objectweb.wildcat.expressions.Environment;
import org.objectweb.wildcat.expressions.EvaluationException;

/**
 * Represents a function.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public interface Function {
    /**
     * Returns the name of this function. 
     * 
     * @return the function's name.
     */
    String getName();

    /**
     * Apply the function to the provided parameters.
     * 
     * @param args
     *            the actual parameters of the invocation.
     * @param env
     *            the dynamic environment.
     * @return an optional return value for the invocation;
     * @throws EvaluationException
     */
    Object apply(Object[] args, Environment env) throws EvaluationException;
}
