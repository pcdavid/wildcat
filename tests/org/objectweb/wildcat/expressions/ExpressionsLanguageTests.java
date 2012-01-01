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
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class ExpressionsLanguageTests {
    private Interpreter inter;

    @Before
    public void setUp() {
        inter = new BasicInterpreter();
    }

    @Test
    public void simpleArithmeticExpressions() throws EvaluationException {
        assertExpressionValue(2.0, "1+1");
        assertExpressionValue(0.0, "1-1");
        assertExpressionValue(0.0, "1.0-1");
        assertExpressionValue(0.0, "1-1.0");
        assertExpressionValue(0.0, "1 - 1.0");
        assertExpressionValue(0.0, "1 -     1.0");
        assertExpressionValue(0.0, "1    -1.0");
        assertExpressionValue(1.0, "1.0 * 1.0");
        assertExpressionValue(1.0, "1.0 div 1.0");
        assertExpressionValue(3.0, "1+1+1-2+4-3+1");
    }

    @Test
    public void arithmeticPrecedence() throws EvaluationException {
        assertExpressionValue(1 + 3 * 5.0, "1+3*5");
        assertExpressionValue(1 * 3 + 5.0, "1*3+5");
        assertExpressionValue(1.0 / 2.0 + 3.0, "1.0 div 2.0+3.0");
        assertExpressionValue(1.0 + 2.0 / 3.0, "1.0+2.0 div 3.0");
    }

    @Test
    public void litteralNumbers() throws EvaluationException {
        assertExpressionValue(0.0, "0");
        assertExpressionValue(0.0, "0.0");
        assertExpressionValue(1.0, "1.0");
        assertExpressionValue(-1.0, "-1.0");
        assertExpressionValue(42.314, "42.314");
    }

    @Test
    public void litteralStrings() throws EvaluationException {
        assertExpressionValue("", "\"\"");
        assertExpressionValue("foo", "\"foo\"");
        assertExpressionValue("foo and bar", "\"foo and bar\"");
        assertExpressionValue("foo \"and\" bar", "\"foo \\\"and\\\" bar\"");
        assertExpressionValue("foo 'and' bar", "\"foo 'and' bar\"");
    }

    @Test
    public void complexExpression() throws EvaluationException {
        assertExpressionValue(true, "not(equals(3*5+1, 3*(5+1)))");
    }

    @Test
    public void validPathExpression() throws EvaluationException {
        String[] paths = new String[] { "/path/to/resource#attribute" };
        for (String p : paths) {
            Expression expr = inter.parse(p);
            assertNotNull(expr);
            assertTrue(expr instanceof CallExpression);
        }
    }

    @Test
    @Ignore("ANTLR grammar needs to be fixed.")
    public void invalidPathExpression() throws EvaluationException {
        String[] paths = new String[] { "domain:/ /path/to/resource",
                "domain:/#attribute", "domain://#attribute",
                "domain://resource/#attribute",
                "domain://resource#attribute/anotherresource", };
        for (String p : paths) {
            try {
                inter.parse(p);
                fail("Invalid path '" + p + "' should be rejected.");
            } catch (IllegalArgumentException e) {
                // OK
            }
        }
    }

    private void assertExpressionValue(Object expected, String expression)
            throws EvaluationException {
        Expression expr = inter.parse(expression);
        assertNotNull(expr);
        assertEquals(expected, inter.evaluate(expr));
    }
}
