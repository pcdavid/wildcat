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
import org.objectweb.wildcat.expressions.functions.SubstractFunction;

/**
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class SubstractFunctionTests {
    private Environment topLevel;

    @Before
    public void setUp() {
        topLevel = new Environment();
        topLevel.defineFunction(new SubstractFunction());
    }

    @Test
    public void substractFunctionExists() throws EvaluationException {
        Function sub = topLevel.lookupFunction("substract");
        assertNotNull(sub);
        assertEquals("substract", sub.getName());
    }

    @Test
    public void substractTwoIntegers() throws EvaluationException {
        Expression e = substractCall(1, 2);
        assertEquals(-1.0, e.evaluate(topLevel));
    }

    @Test
    public void substractTwoFloats() throws EvaluationException {
        Expression e = substractCall(1.0, 3.14);
        assertEquals(1.0 - 3.14, e.evaluate(topLevel));
    }

    @Test
    public void substractIntToFloat() throws EvaluationException {
        Expression e = substractCall(1, 1.0);
        assertEquals(0.0, e.evaluate(topLevel));
    }

    @Test
    public void substractTenNumbers() throws EvaluationException {
        Expression e = substractCall(1, -1, 2, -2, 3, -3, 4, -4, 5, -5);
        // 1 - (-1) - 2 - (-2) - 3 - (-3) - 4 - (-4) - 5 - (-5)
        // => 2
        assertEquals(2.0, e.evaluate(topLevel));
    }

    @Test(expected = EvaluationException.class)
    public void substractTwoStrings() throws EvaluationException {
        Expression e = substractCall("foo", "bar");
        e.evaluate(topLevel);
    }

    @Test
    public void recursiveSubstract() throws EvaluationException {
        // substract(1, substract(2, 3), substract(4, 5))
        // => 1 - (2 - 3) - (4 - 5) => 1 - (-1) - (-1) => 3
        Expression e = new CallExpression("substract", new ConstantExpression(1),
                substractCall(2, 3), substractCall(4, 5));
        assertEquals(3.0, e.evaluate(topLevel));
    }

    private Expression substractCall(Object... constArgs) {
        Expression[] args = new Expression[constArgs.length];
        for (int i = 0; i < constArgs.length; i++) {
            args[i] = new ConstantExpression(constArgs[i]);
        }
        return new CallExpression("substract", args);
    }
}
