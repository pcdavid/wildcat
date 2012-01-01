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
package org.objectweb.wildcat.expressions.functions;

import org.objectweb.wildcat.expressions.Environment;
import org.objectweb.wildcat.expressions.EvaluationException;

/**
 * Implements the "less than" function/operator.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class LessThanFunction implements Function {
    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.expressions.Function#getName()
     */
    public String getName() {
        return "less";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.expressions.Function#apply(java.lang.Object[],
     *      org.objectweb.wildcat.expressions.Environment)
     */
    public Object apply(Object[] args, Environment env) throws EvaluationException {
        if (args.length < 2) {
            throw new EvaluationException("Function " + getName()
                    + "() requires at least two arguments.");
        }
        for (int i = 0; i < args.length - 1; i++) {
            if (!lessThan(args[i], args[i + 1])) {
                return false;
            }
        }
        return true;
    }

    private boolean lessThan(Object o1, Object o2) throws EvaluationException {
        if (o1 instanceof Number && o2 instanceof Number) {
            return ((Number) o1).doubleValue() < ((Number) o2).doubleValue();
        } else {
            throw new EvaluationException("Function " + getName()
                    + " expected numbers as argument.");
        }
    }
}
