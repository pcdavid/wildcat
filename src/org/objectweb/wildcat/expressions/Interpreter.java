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

/**
 * An interpreter is able to parse and evaluate {@linkplain Expression expressions}.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public interface Interpreter {
    /**
     * Parses a string representing an expression.
     * 
     * @param expression
     *            a string representing an expression.
     * @return a parsed {@link Expression}
     * @throws IllegalArgumentException
     *             if the string is not a valid expression.
     */
    Expression parse(String expression) throws IllegalArgumentException;

    /**
     * Evaluates a preparsed expression in the interpreter's top-level environement.
     * 
     * @param expr
     *            the expression to evaluate.
     * @return the value of the expression
     * @throws EvaluationException
     *             if an error occurred during the evaluation of the expression.
     */
    Object evaluate(Expression expr) throws EvaluationException;

    /**
     * Evaluates an expression from a string in the interpreter's top-level environement.
     * 
     * @param expr
     *            the expression to evaluate.
     * @return the value of the expression
     * @throws EvaluationException
     *             if an error occurred during the evaluation of the expression.
     * @throws IllegalArgumentException
     *             if the string is not a valid expression.
     */
    Object evaluate(String expr) throws EvaluationException;

    /**
     * Returns the top-level environement of this interpreter. This environment can be
     * manipulated to define or redefine top-level variables and load new functions.
     * 
     * @return the top-level environement of this interpreter.
     */
    Environment getEnvironment();
}
