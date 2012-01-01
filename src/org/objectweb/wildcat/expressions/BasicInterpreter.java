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

import java.io.StringReader;

import org.objectweb.wildcat.expressions.functions.FunctionsLibrary;
import org.objectweb.wildcat.expressions.functions.StandardLibrary;

import antlr.RecognitionException;
import antlr.TokenStreamException;

/**
 * Default implementation of the {@link Interpreter} interface.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class BasicInterpreter implements Interpreter {
    /**
     * The top-level environment containing functions definitions and global variables.
     */
    private Environment topLevel = new Environment();

    /**
     * Creates a new interpreter with the default library of functions.
     */
    public BasicInterpreter() {
        this(new StandardLibrary());
    }

    /**
     * Creates a new interpreter with a specific library of functions pre-loaded.
     * 
     * @param library
     *            the library of functions to load.
     */
    public BasicInterpreter(FunctionsLibrary library) {
        this.topLevel = new Environment();
        this.topLevel.load(library);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.expressions.Interpreter#getEnvironment()
     */
    public Environment getEnvironment() {
        return topLevel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.expressions.Interpreter#parse(java.lang.String)
     */
    public Expression parse(String expr) throws IllegalArgumentException {
        ExpressionsLexer lexer = new ExpressionsLexer(new StringReader(expr));
        ExpressionsParser parser = new ExpressionsParser(lexer);
        try {
            return parser.expression();
        } catch (RecognitionException e) {
            throw new IllegalArgumentException("Syntax error: " + e.getMessage(), e);
        } catch (TokenStreamException e) {
            throw new IllegalArgumentException("Syntax error: " + e.getMessage(), e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.expressions.Interpreter#evaluate(java.lang.String)
     */
    public Object evaluate(String expr) throws EvaluationException {
        return evaluate(parse(expr));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.expressions.Interpreter#evaluate(org.objectweb.wildcat.expressions.Expression)
     */
    public Object evaluate(Expression expr) throws EvaluationException {
        return expr.evaluate(topLevel);
    }
}
