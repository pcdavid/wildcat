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


/**
 * @author Pierre-Charles David <pcdavid@gmail.com>
 *
 */
public class VariableExpressionTests {
    private Environment env;
    
    @Before
    public void setUp() {
        env = new Environment();
        env.defineVariable("foo", "foo");
        env.defineVariable("bar", 42);
    }
    
    @Test
    public void stringVariable() throws EvaluationException {
        Expression e = new VariableExpression("foo");
        assertEquals("foo", e.evaluate(env)); 
    }

    @Test
    public void intVariable() throws EvaluationException {
        Expression e = new VariableExpression("bar");
        assertEquals(42, e.evaluate(env)); 
    }
    
    @Test(expected=EvaluationException.class)
    public void unknownVariable() throws EvaluationException {
        Expression e = new VariableExpression("qux");
        e.evaluate(env);
    }
}
