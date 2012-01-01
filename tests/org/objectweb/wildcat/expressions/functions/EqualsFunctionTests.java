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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.wildcat.expressions.CallExpression;
import org.objectweb.wildcat.expressions.ConstantExpression;
import org.objectweb.wildcat.expressions.Environment;
import org.objectweb.wildcat.expressions.EvaluationException;
import org.objectweb.wildcat.expressions.Expression;
import org.objectweb.wildcat.expressions.functions.EqualsFunction;
import org.objectweb.wildcat.expressions.functions.Function;

/**
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class EqualsFunctionTests {
    private Environment topLevel;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        topLevel = new Environment();
        topLevel.defineFunction(new EqualsFunction());
    }

    @Test
    public void equalsFunctionExists() {
        Function eq = topLevel.lookupFunction("equals");
        assertNotNull(eq);
        assertEquals("equals", eq.getName());
    }

    @Test
    public void booleanEquals() throws EvaluationException {
        assertEquals(true, eqCall(true, true).evaluate(topLevel));
        assertEquals(true, eqCall(false, false).evaluate(topLevel));
        assertEquals(false, eqCall(true, false).evaluate(topLevel));
        assertEquals(false, eqCall(false, true).evaluate(topLevel));
    }

    @Test
    public void booleanObjectEquals() throws EvaluationException {
        assertEquals(true, eqCall(Boolean.TRUE, Boolean.TRUE).evaluate(topLevel));
        assertEquals(true, eqCall(Boolean.FALSE, Boolean.FALSE).evaluate(topLevel));
        assertEquals(false, eqCall(Boolean.TRUE, Boolean.FALSE).evaluate(topLevel));
        assertEquals(false, eqCall(Boolean.FALSE, Boolean.TRUE).evaluate(topLevel));
    }

    @Test
    public void booleanPrimitivesAndObjects() throws EvaluationException {
        assertEquals(true, eqCall(true, Boolean.TRUE).evaluate(topLevel));
        assertEquals(true, eqCall(false, Boolean.FALSE).evaluate(topLevel));
        assertEquals(false, eqCall(true, Boolean.FALSE).evaluate(topLevel));
        assertEquals(false, eqCall(false, Boolean.TRUE).evaluate(topLevel));
    }

    @Test
    public void numberEquals() throws EvaluationException {
        assertEquals(true, eqCall(1, 1).evaluate(topLevel));
        assertEquals(false, eqCall(1, 1.0).evaluate(topLevel));
        assertEquals(false, eqCall(0, 0.0).evaluate(topLevel));
        assertEquals(false, eqCall(0.0, -0.0).evaluate(topLevel));
        assertEquals(true, eqCall(-1, -1).evaluate(topLevel));
        assertEquals(false, eqCall(1, 0).evaluate(topLevel));
    }
    
    @Test
    public void stringEquals() throws EvaluationException {
        assertEquals(true, eqCall("", "").evaluate(topLevel));
        assertEquals(true, eqCall("foo", "foo").evaluate(topLevel));
        assertEquals(false, eqCall("foo", "bar").evaluate(topLevel));
        assertEquals(false, eqCall("foo", null).evaluate(topLevel));
        assertEquals(false, eqCall(null, "bar").evaluate(topLevel));
    }
    
    @Test
    public void nullEquals() throws EvaluationException {
        assertEquals(true, eqCall(null, null).evaluate(topLevel));
    }
    
    @Test
    public void multiWayEquals() throws EvaluationException {
        assertEquals(true, eqCall("major", "major", "major", "major").evaluate(topLevel));
    }
    
    @Test(expected=EvaluationException.class)
    public void zeroParameters() throws EvaluationException {
        eqCall().evaluate(topLevel);
    }
    
    @Test(expected=EvaluationException.class)
    public void oneParameter() throws EvaluationException {
        eqCall(1).evaluate(topLevel);
    }

    private Expression eqCall(Object... constantArgs) {
        Expression[] params = new Expression[constantArgs.length];
        for (int i = 0; i < params.length; i++) {
            params[i] = new ConstantExpression(constantArgs[i]);
        }
        return new CallExpression("equals", params);
    }
}
