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
public class EnvironmentTests {
    private Environment topLevel;
    private Environment local;
    private Function dummy;
    private Function dummy2;

    @Before
    public void setUp() {
        topLevel = new Environment();
        local = new Environment(topLevel);
        dummy = new Function() {
            public String getName() {
                return "dummy";
            }

            public Object apply(Object[] args, Environment env)
                    throws EvaluationException {
                return 1;
            }
        };
        dummy2 = new Function() {
            public String getName() {
                return "dummy";
            }
    
            public Object apply(Object[] args, Environment env) throws EvaluationException {
                return 2;
            }
        };
    }

    @Test
    public void topLevelCreatedEmpty() {
        assertNull(topLevel.lookupFunction("foo"));
        assertNull(topLevel.lookupVariable("foo"));
    }

    @Test
    public void defineTopLevelInteger() {
        assertNull(topLevel.lookupVariable("i"));
        topLevel.defineVariable("i", 42);
        assertEquals(42, topLevel.lookupVariable("i"));
    }

    @Test
    public void redefineTopLevelInteger() {
        assertNull(topLevel.lookupVariable("i"));
        topLevel.defineVariable("i", 42);
        topLevel.defineVariable("i", 43);
        assertEquals(43, topLevel.lookupVariable("i"));
    }

    @Test
    public void defineTopLevelString() {
        assertNull(topLevel.lookupVariable("s"));
        topLevel.defineVariable("s", "foo");
        assertEquals("foo", topLevel.lookupVariable("s"));
    }

    @Test
    public void redefineTopLevelString() {
        assertNull(topLevel.lookupVariable("s"));
        topLevel.defineVariable("s", "foo");
        topLevel.defineVariable("s", "bar");
        assertEquals("bar", topLevel.lookupVariable("s"));
    }

    @Test
    public void redefineTopLevelIntegerAsString() {
        assertNull(topLevel.lookupVariable("s"));
        topLevel.defineVariable("s", 42);
        topLevel.defineVariable("s", "bar");
        assertEquals("bar", topLevel.lookupVariable("s"));
    }

    @Test
    public void defineTopLevelFunction() {
        assertNull(topLevel.lookupFunction(dummy.getName()));
        topLevel.defineFunction(dummy);
        assertSame(dummy, topLevel.lookupFunction(dummy.getName()));
    }

    @Test
    public void redefineTopLevelFunction() {
        assertNull(topLevel.lookupFunction(dummy.getName()));
        topLevel.defineFunction(dummy);
        assertNotSame(dummy, dummy2);
        topLevel.defineFunction(dummy2);
        assertNotSame(dummy, topLevel.lookupFunction(dummy.getName()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void defineNullVariable() {
        topLevel.defineVariable("null", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void defineNullFunction() {
        topLevel.defineFunction(null);
    }

    @Test
    public void createSubEnvironment() {
        new Environment(new Environment(topLevel));
    }

    @Test
    public void variableInheritance() {
        topLevel.defineVariable("foo", "foo");
        assertEquals("foo", local.lookupVariable("foo"));
    }

    @Test
    public void functionInheritance() {
        topLevel.defineFunction(dummy);
        assertSame(dummy, local.lookupFunction(dummy.getName()));
    }
    
    @Test
    public void localVariableDefinition() {
        assertNull(topLevel.lookupVariable("foo"));
        assertNull(local.lookupVariable("foo"));
        local.defineVariable("foo", "foo");
        assertEquals("foo", local.lookupVariable("foo"));
        assertNull(topLevel.lookupVariable("foo"));
    }
    
    @Test
    public void localFunctionDefinition() {
        assertNull(topLevel.lookupFunction(dummy.getName()));
        assertNull(local.lookupFunction(dummy.getName()));
        local.defineFunction(dummy);
        assertSame(dummy, local.lookupFunction(dummy.getName()));
        assertNull(topLevel.lookupFunction(dummy.getName()));
    }

    @Test
    public void variableShadowing() {
        topLevel.defineVariable("foo", "foo");
        local.defineVariable("foo", "bar");
        assertEquals("foo", topLevel.lookupVariable("foo"));
        assertEquals("bar", local.lookupVariable("foo"));
    }
    
    @Test
    public void functionShadowing() {
        topLevel.defineFunction(dummy);
        local.defineFunction(dummy2);
        assertSame(dummy, topLevel.lookupFunction(dummy.getName()));
        assertSame(dummy2, local.lookupFunction(dummy.getName()));
    }
}
