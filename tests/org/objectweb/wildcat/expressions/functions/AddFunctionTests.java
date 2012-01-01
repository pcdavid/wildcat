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
import org.objectweb.wildcat.expressions.functions.AddFunction;
import org.objectweb.wildcat.expressions.functions.Function;

/**
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class AddFunctionTests {
    private Environment topLevel;

    @Before
    public void setUp() {
        topLevel = new Environment();
        topLevel.defineFunction(new AddFunction());
    }

    @Test
    public void addFunctionExists() throws EvaluationException {
        Function add = topLevel.lookupFunction("add");
        assertNotNull(add);
        assertEquals("add", add.getName());
    }

    @Test
    public void addNothing() throws EvaluationException {
        Expression e = addCall();
        assertEquals(0.0, e.evaluate(topLevel));
    }

    @Test
    public void addOneInteger() throws EvaluationException {
        Expression e = addCall(1);
        assertEquals(1.0, e.evaluate(topLevel));
    }

    @Test
    public void addOneDouble() throws EvaluationException {
        Expression e = addCall(1.0);
        assertEquals(1.0, e.evaluate(topLevel));
    }

    @Test
    public void addTwoIntegers() throws EvaluationException {
        Expression e = addCall(1, 2);
        assertEquals(3.0, e.evaluate(topLevel));
    }

    @Test
    public void addTwoFloats() throws EvaluationException {
        Expression e = addCall(1.0, 3.14);
        assertEquals(1.0 + 3.14, e.evaluate(topLevel));
    }

    @Test
    public void addIntToFloat() throws EvaluationException {
        Expression e = addCall(1, 1.0);
        assertEquals(2.0, e.evaluate(topLevel));
    }

    @Test
    public void symmetry() throws EvaluationException {
        Expression e1 = addCall(1, 2);
        Expression e2 = addCall(2, 1);
        assertEquals(e1.evaluate(topLevel), e2.evaluate(topLevel));
    }

    @Test
    public void addTenNumbers() throws EvaluationException {
        Expression e = addCall(1, -1, 2, -2, 3, -3, 4, -4, 5, -5);
        assertEquals(0.0, e.evaluate(topLevel));
    }

    @Test(expected = EvaluationException.class)
    public void addTwoStrings() throws EvaluationException {
        Expression e = addCall("foo", "bar");
        e.evaluate(topLevel);
    }

    @Test
    public void recursiveAdd() throws EvaluationException {
        // add(1, add(2, 3), add(4, 5)) => 15
        Expression e = new CallExpression("add", new ConstantExpression(1),
                addCall(2, 3), addCall(4, 5));
        assertEquals(15.0, e.evaluate(topLevel));
    }

    private Expression addCall(Object... constArgs) {
        Expression[] args = new Expression[constArgs.length];
        for (int i = 0; i < constArgs.length; i++) {
            args[i] = new ConstantExpression(constArgs[i]);
        }
        return new CallExpression("add", args);
    }
}
