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
 */
public class OrExpressionTests {
    private Environment topLevel;
    private Expression trueExpr;
    private Expression falseExpr;

    @Before
    public void setUp() {
        topLevel = new Environment();
        trueExpr = new ConstantExpression(true);
        falseExpr = new ConstantExpression(false);
    }

    @Test
    public void emptyDisjunction() throws EvaluationException {
        Expression e = new OrExpression();
        assertEquals(Boolean.FALSE, e.evaluate(topLevel));
    }

    @Test
    public void singleTrueClause() throws EvaluationException {
        Expression e = new OrExpression(trueExpr);
        assertEquals(Boolean.TRUE, e.evaluate(topLevel));
    }

    @Test
    public void singleFalseClause() throws EvaluationException {
        Expression e = new OrExpression(falseExpr);
        assertEquals(Boolean.FALSE, e.evaluate(topLevel));
    }
    
    @Test
    public void allTrueClauses() throws EvaluationException {
        Expression e = new OrExpression(trueExpr, trueExpr, trueExpr);
        assertEquals(Boolean.TRUE, e.evaluate(topLevel));
    }
    
    @Test
    public void allFalseClauses() throws EvaluationException {
        Expression e = new OrExpression(falseExpr, falseExpr, falseExpr);
        assertEquals(Boolean.FALSE, e.evaluate(topLevel));
    }
    
    @Test
    public void mixedClauses() throws EvaluationException {
        Expression e = new OrExpression(trueExpr, falseExpr, trueExpr);
        assertEquals(Boolean.TRUE, e.evaluate(topLevel));
    }
    
    @Test
    public void shortcut() throws EvaluationException {
        ObservableExpression obsTrueExpr = new ObservableExpression(trueExpr);
        ObservableExpression obsFalseExpr = new ObservableExpression(falseExpr);
        ObservableExpression obsFalseExpr2 = new ObservableExpression(falseExpr);
        //
        assertFalse(obsTrueExpr.isEvaluated());
        assertFalse(obsFalseExpr.isEvaluated());
        Expression e = new OrExpression(obsTrueExpr, obsFalseExpr);
        assertEquals(Boolean.TRUE, e.evaluate(topLevel));
        assertTrue(obsTrueExpr.isEvaluated());
        assertFalse(obsFalseExpr.isEvaluated());
        //
        obsTrueExpr.resetEvaluated();
        obsFalseExpr.resetEvaluated();
        assertFalse(obsTrueExpr.isEvaluated());
        assertFalse(obsFalseExpr.isEvaluated());
        e = new OrExpression(obsFalseExpr, obsTrueExpr);
        assertEquals(Boolean.TRUE, e.evaluate(topLevel));
        assertTrue(obsFalseExpr.isEvaluated());
        assertTrue(obsTrueExpr.isEvaluated());
        //
        obsTrueExpr.resetEvaluated();
        obsFalseExpr.resetEvaluated();
        assertFalse(obsTrueExpr.isEvaluated());
        assertFalse(obsFalseExpr2.isEvaluated());
        assertFalse(obsFalseExpr.isEvaluated());
        e = new OrExpression(obsFalseExpr, obsTrueExpr, obsFalseExpr2);
        assertEquals(Boolean.TRUE, e.evaluate(topLevel));
        assertTrue(obsFalseExpr.isEvaluated());
        assertTrue(obsTrueExpr.isEvaluated());
        assertFalse(obsFalseExpr2.isEvaluated());
    }
}
