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

import org.objectweb.wildcat.expressions.EvaluationException;

/**
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class DivideFunction extends ArithmeticFunction {
    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.expressions.Function#getName()
     */
    public String getName() {
        return "divide";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.functions.ArithmeticFunction#getMinimumArgs()
     */
    @Override
    protected int getMinimumArgs() {
        return 2;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.functions.ArithmeticFunction#apply(double[])
     */
    @Override
    protected double apply(double[] args) throws EvaluationException {
        double result = args[0];
        for (int i = 1; i < args.length; i++) {
            double divisor = args[i];
            if (divisor == 0) {
                throw new EvaluationException(new ArithmeticException(
                        "Attempted division by zero."));
            }
            result /= divisor;
        }
        return result;
    }
}
