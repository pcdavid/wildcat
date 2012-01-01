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
public class ConstantExpressionTests {
    private Environment topLevel;
    
    @Before
    public void setUp() {
        topLevel = new Environment();
    }
    
    @Test
    public void numericConstant() throws EvaluationException {
        Expression e = new ConstantExpression(42);
        assertEquals(42, e.evaluate(topLevel));
    }
    
    @Test
    public void stringConstant() throws EvaluationException {
        Expression e = new ConstantExpression("foo");
        assertEquals("foo", e.evaluate(topLevel));
    }
    
    @Test
    public void trueConstant() throws EvaluationException {
        Expression e = new ConstantExpression(true);
        assertEquals(Boolean.TRUE, e.evaluate(topLevel));
        e = new ConstantExpression(Boolean.TRUE);
        assertEquals(Boolean.TRUE, e.evaluate(topLevel));
    }

    @Test
    public void falseConstant() throws EvaluationException {
        Expression e = new ConstantExpression(false);
        assertEquals(Boolean.FALSE, e.evaluate(topLevel));
        e = new ConstantExpression(Boolean.FALSE);
        assertEquals(Boolean.FALSE, e.evaluate(topLevel));
    }

    @Test
    public void nullConstant() throws EvaluationException {
        Expression e = new ConstantExpression(null);
        assertNull(e.evaluate(topLevel));
    }
}
