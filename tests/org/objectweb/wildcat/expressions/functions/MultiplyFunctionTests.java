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

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.wildcat.expressions.CallExpression;
import org.objectweb.wildcat.expressions.ConstantExpression;
import org.objectweb.wildcat.expressions.Environment;
import org.objectweb.wildcat.expressions.EvaluationException;
import org.objectweb.wildcat.expressions.Expression;
import org.objectweb.wildcat.expressions.functions.Function;
import org.objectweb.wildcat.expressions.functions.MultiplyFunction;

/**
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class MultiplyFunctionTests {
    private Environment topLevel;

    @Before
    public void setUp() {
        topLevel = new Environment();
        topLevel.defineFunction(new MultiplyFunction());
    }

    @Test
    public void multiplyFunctionExists() {
        Function mult = topLevel.lookupFunction("multiply");
        assertNotNull(mult);
        assertEquals("multiply", mult.getName());
    }

    @Test
    public void multiplyNothing() throws EvaluationException {
        Expression e = multCall();
        assertEquals(1.0, e.evaluate(topLevel));
    }

    @Test
    public void multiplyOneInteger() throws EvaluationException {
        Expression e = multCall(42);
        assertEquals(42.0, e.evaluate(topLevel));
    }

    @Test
    public void multiplyTwoIntegers() throws EvaluationException {
        Expression e = multCall(7, 3);
        assertEquals(7.0 * 3.0, e.evaluate(topLevel));
    }

    @Test
    public void multiplyIntegerByOne() throws EvaluationException {
        Expression e = multCall(4, 1);
        assertEquals(4.0, e.evaluate(topLevel));
    }

    @Test
    public void multiplyIntegerByZero() throws EvaluationException {
        Expression e = multCall(42, 0);
        assertEquals(0.0, e.evaluate(topLevel));
    }

    @Test
    public void multiplyTwoDouble() throws EvaluationException {
        Expression e = multCall(3.5, 2);
        assertEquals(3.5 * 2.0, e.evaluate(topLevel));
    }

    @Test
    public void multiplyIntegerWithDouble() throws EvaluationException {
        Expression e = multCall(1.0, 5);
        assertEquals(5.0, e.evaluate(topLevel));
    }

    @Test
    public void multiplyMany() throws EvaluationException {
        Expression e = multCall(1, 2.0, 3.0, 4.5, -6.5);
        assertEquals(1 * 2.0 * 3.0 * 4.5 * -6.5, e.evaluate(topLevel));
    }

    private Expression multCall(Object... constantArgs) {
        Expression[] args = new Expression[constantArgs.length];
        for (int i = 0; i < constantArgs.length; i++) {
            args[i] = new ConstantExpression(constantArgs[i]);
        }
        return new CallExpression("multiply", args);
    }
}
