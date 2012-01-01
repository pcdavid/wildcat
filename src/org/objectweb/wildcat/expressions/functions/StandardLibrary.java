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

import java.util.ArrayList;
import java.util.Collection;

/**
 * Provides the default set of functions to the interpreter.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class StandardLibrary implements FunctionsLibrary {
    /**
     * The functions provided by this library.
     */
    private Collection<Function> functions;
   
    /**
     * Creates and populates the standard library.
     */
    public StandardLibrary() {
        functions = new ArrayList<Function>();
        functions.add(new AddFunction());
        functions.add(new DivideFunction());
        functions.add(new EqualsFunction());
        functions.add(new LessThanFunction());
        functions.add(new MultiplyFunction());
        functions.add(new NotFunction());
        functions.add(new SubstractFunction());
        functions.add(new AttributeFunction());
    }

    /* (non-Javadoc)
     * @see org.objectweb.wildcat.functions.FunctionsLibrary#getFunctions()
     */
    public Function[] getFunctions() {
        return functions.toArray(new Function[0]);
    }
}
