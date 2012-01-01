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
import org.objectweb.wildcat.expressions.functions.DivideFunction;
import org.objectweb.wildcat.expressions.functions.Function;

/**
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class DivideFunctionTests {
    private Environment topLevel;

    @Before
    public void setUp() {
        topLevel = new Environment();
        topLevel.defineFunction(new DivideFunction());
    }

    @Test
    public void divideFunctionExists() {
        Function div = topLevel.lookupFunction("divide");
        assertNotNull(div);
        assertEquals("divide", div.getName());
    }

    @Test
    public void divideTwoIntegers() throws EvaluationException {
        Expression e = divCall(12, 4);
        assertEquals(12.0 / 4.0, e.evaluate(topLevel));
    }

    private Expression divCall(Object... constantArgs) {
        Expression[] args = new Expression[constantArgs.length];
        for (int i = 0; i < constantArgs.length; i++) {
            args[i] = new ConstantExpression(constantArgs[i]);
        }
        return new CallExpression("divide", args);
    }
}
