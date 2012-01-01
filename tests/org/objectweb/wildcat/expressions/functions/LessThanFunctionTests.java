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
import org.objectweb.wildcat.expressions.Environment;
import org.objectweb.wildcat.expressions.functions.Function;
import org.objectweb.wildcat.expressions.functions.LessThanFunction;

/**
 * @author Pierre-Charles David <pcdavid@gmail.com>
 *
 */
public class LessThanFunctionTests {
    private Environment topLevel;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        topLevel = new Environment();
        topLevel.defineFunction(new LessThanFunction());
    }


    @Test
    public void ltFunctionExists() {
        Function lt = topLevel.lookupFunction("less");
        assertNotNull(lt);
        assertEquals("less", lt.getName());
    }
}
