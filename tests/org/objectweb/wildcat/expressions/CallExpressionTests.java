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
package org.objectweb.wildcat.expressions;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.wildcat.expressions.functions.Function;

/**
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class CallExpressionTests {
    class Addition implements Function {
        public String getName() {
            return "add";
        }

        public Object apply(Object[] args, Environment env) throws EvaluationException {
            int sum = 0;
            for (Object arg : args) {
                Number n = (Number) arg;
                sum += n.intValue();
            }
            return sum;
        }
    }

    private Environment env;
    private Function add;

    @Before
    public void setUp() {
        env = new Environment();
        add = new Addition();
        env.defineFunction(add);
    }

    @Test(expected = EvaluationException.class)
    public void undefinedFunction() throws EvaluationException {
        Expression e = new CallExpression("foo");
        e.evaluate(env);
    }

    @Test
    public void callWithNoArgs() throws EvaluationException {
        Expression e = new CallExpression("add");
        assertEquals(0, e.evaluate(env));
    }

    @Test
    public void callWithOneValidArg() throws EvaluationException {
        Expression e = new CallExpression("add", new ConstantExpression(42));
        assertEquals(42, e.evaluate(env));
    }

    @Test
    public void callWithTwoValidArgs() throws EvaluationException {
        Expression e = new CallExpression("add", new ConstantExpression(42),
                new ConstantExpression(43));
        assertEquals(42 + 43, e.evaluate(env));
    }

    @Test(expected = EvaluationException.class)
    public void callWithOneInvalidArg() throws EvaluationException {
        Expression e = new CallExpression("add", new ConstantExpression("foo"));
        e.evaluate(env);
    }

    @Test
    public void argumentEvaluation() throws EvaluationException {
        ObservableExpression obs42 = new ObservableExpression(new ConstantExpression(42));
        ObservableExpression obs43 = new ObservableExpression(new ConstantExpression(43));
        Expression e = new CallExpression("add", obs42, obs43);
        assertEquals(42 + 43, e.evaluate(env));
        assertTrue(obs42.isEvaluated());
        assertTrue(obs43.isEvaluated());
    }

    @Test
    public void argumentEvaluationOrder() throws EvaluationException {
        ObservableExpression obs42 = new ObservableExpression(new ConstantExpression(42));
        Expression error = new Expression() {
            public Object evaluate(Environment env) throws EvaluationException {
                throw new EvaluationException("Error!");
            }
            public void accept(ExpressionVisitor visitor) {
            }
        };
        ObservableExpression obs43 = new ObservableExpression(new ConstantExpression(43));
        Expression e = new CallExpression("add", obs42, error, obs43);
        try {
            e.evaluate(env);
        } catch (EvaluationException ee) {
            assertTrue(obs42.isEvaluated());
            assertFalse(obs43.isEvaluated());
        }
    }
}
