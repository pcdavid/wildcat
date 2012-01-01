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
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class BasicInterpreter implements Interpreter {
    private Environment topLevel = new Environment();

    public BasicInterpreter() {
        this(new StandardLibrary());
    }

    public BasicInterpreter(FunctionsLibrary library) {
        this.topLevel = new Environment();
        this.topLevel.load(library);
    }
    
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
