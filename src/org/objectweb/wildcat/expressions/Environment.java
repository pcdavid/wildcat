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
 * Contact: Pierre-Charles David <PierreCharles.David@francetelecom.com>
 */
package org.objectweb.wildcat.expressions;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.wildcat.expressions.functions.Function;
import org.objectweb.wildcat.expressions.functions.FunctionsLibrary;

/**
 * Represents the dynamic environment in which expressions are evaluated.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class Environment {
    /**
     * The parent environment from which this one inherits; <code>null</code> for the
     * top-level.
     */
    private Environment parent;

    /**
     * Local variables defined in this environment; may shadow same-named variables from
     * the parent.
     */
    private Map<String, Object> variables;

    /**
     * Local functions defined in this environment. This is initialized only if a new
     * function is defined explicitely, and hence is generally <code>null</code> except
     * in the top-level. The field is nevertheless defined here to avoid creating a
     * special-purpose <code>TopLevelEnvironment</code>.
     */
    private Map<String, Function> functions;

    /**
     * Creates a new top-level environment.
     */
    public Environment() {
        this(null);
    }

    /**
     * Creates a new environment, optionally inheriting from a parent.
     * 
     * @param parent
     *            the parent environment; may be null.
     */
    public Environment(Environment parent) {
        this.parent = parent;
        this.variables = new HashMap<String, Object>();
        this.functions = null;
    }

    /**
     * Finds a function by name.
     * 
     * @param name
     *            the name of the function to find
     * @return the function, or <code>null</code> if it was not found.
     */
    public Function lookupFunction(String name) {
        Function fun = null;
        if (functions != null) {
            fun = functions.get(name);
        }
        if (fun == null && parent != null) {
            fun = parent.lookupFunction(name);
        }
        return fun;
    }

    /**
     * Defines a new function.
     * 
     * @param fun
     *            the new function to define
     */
    public void defineFunction(Function fun) {
        if (fun == null) {
            throw new IllegalArgumentException("The function to define can not be null.");
        } else {
            defineFunction(fun.getName(), fun);
        }
    }
    
    /**
     * Defines a whole set of functions from a library.
     * 
     * @param library
     */
    public void load(FunctionsLibrary library) {
        for (Function fun : library.getFunctions()) {
            defineFunction(fun);
        } 
    }
    
    /**
     * Defines a function using a user-supplied name. Useful for aliasing, e.g.
     * <code>env.defineFunction("+", env.lookupFunction("add"))</code>.
     * 
     * @param name the name to use for the function.
     * @param fun the function to define (may be already known under another name).
     */
    public void defineFunction(String name, Function fun) {
        if (fun == null) {
            throw new IllegalArgumentException("The function to define can not be null.");
        } else if (name == null || name.equals("")) {
            throw new IllegalArgumentException("The function to define must have a name.");
        }
        if (functions == null) {
            functions = new HashMap<String, Function>();
        }
        functions.put(name, fun);
    }

    /**
     * Finds a variable by name.
     * 
     * @param name
     *            the name of the variable to find.
     * @return the value of the variable, or <code>null</code> if it was not found.
     */
    public Object lookupVariable(String name) {
        Object value = variables.get(name);
        if (value == null && parent != null) {
            value = parent.lookupVariable(name);
        }
        return value;
    }

    /**
     * Defines or updates the value of a variable in this environment.
     * 
     * @param name
     *            the name of the variable.
     * @param value
     *            the new value of the variable; may not be <code>null</code>
     * @throws IllegalArgumentException
     *             if the value is <code>null</code>
     */
    public void defineVariable(String name, Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Variable value may not be null.");
        }
        variables.put(name, value);
    }
}
