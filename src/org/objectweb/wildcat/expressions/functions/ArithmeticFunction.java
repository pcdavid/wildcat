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
 * Base class used to implement arithmetic functions.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public abstract class ArithmeticFunction implements Function {
    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.expressions.Function#apply(java.lang.Object[],
     *      org.objectweb.wildcat.expressions.Environment)
     */
    public Object apply(Object[] args, Environment env) throws EvaluationException {
        if (args.length < getMinimumArgs()) {
            throw new EvaluationException("Function " + getName() + " needs at least "
                    + getMinimumArgs() + " arguments.");
        }
        double[] numericArgs = new double[args.length];
        for (int i = 0; i < numericArgs.length; i++) {
            numericArgs[i] = asDouble(args[i], i);
        }
        return apply(numericArgs);
    }

    protected int getMinimumArgs() {
        return 0;
    }

    protected abstract double apply(double[] args) throws EvaluationException;

    protected double asDouble(Object arg, int pos) throws EvaluationException {
        if (arg instanceof Number) {
            Number n = (Number) arg;
            return n.doubleValue();
        } else {
            throw new EvaluationException("Invalid argument type: " + getName()
                    + "() expected a number at position " + pos + ".");
        }
    }
}
