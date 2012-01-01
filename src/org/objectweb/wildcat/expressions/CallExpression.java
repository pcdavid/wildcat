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

import org.objectweb.wildcat.expressions.functions.Function;

/**
 * Represents a function call with arguments. Arguments are evaluated in order, and the
 * results are passed to the underlying {@link Function}.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class CallExpression implements Expression {
    /**
     * The name of the functionto call.
     */
    private String functionName;

    /**
     * The expressions to evaluate to obtain the arguments to the call.
     */
    private Expression[] parameters;

    /**
     * Creates a new <code>CallExpression</code>.
     * 
     * @param functionName
     *            the name of the function to call
     * @param params
     *            the expressions denoting the actual parameters
     */
    public CallExpression(String functionName, Expression... params) {
        this.functionName = functionName;
        this.parameters = params;
    }
    
    /**
     * @return the functionName
     */
    public String getFunctionName() {
        return functionName;
    }
    
    /**
     * @return the parameters
     */
    public Expression[] getParameters() {
        return parameters;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.expressions.Expression#evaluate(org.objectweb.wildcat.expressions.Environment)
     */
    public Object evaluate(Environment env) throws EvaluationException {
        Function f = env.lookupFunction(functionName);
        if (f == null) {
            throw new EvaluationException("Undefined function '" + functionName + "'.");
        }
        Object[] args = evalArguments(env);
        try {
            return f.apply(args, env);
        } catch (Exception e) {
            throw new EvaluationException("Error while executing function " + functionName
                    + ".", e);
        }
    }

    /**
     * Evaluates the parameters to yield the actual call arguments.
     * 
     * @param env
     *            the evaluation environment.
     * @return the call arguments for the procedure.
     * @throws FScriptException
     *             if one of the parameters caused an <code>FScriptException</code>
     *             during its evaluation.
     */
    private Object[] evalArguments(Environment env) throws EvaluationException {
        if (parameters != null && parameters.length > 0) {
            Object[] args = new Object[parameters.length];
            for (int i = 0; i < args.length; i++) {
                args[i] = parameters[i].evaluate(env);
            }
            return args;
        } else {
            return new Object[0];
        }
    }
    
    /* (non-Javadoc)
     * @see org.objectweb.wildcat.expressions.Expression#accept(org.objectweb.wildcat.expressions.ExpressionVisitor)
     */
    public void accept(ExpressionVisitor visitor) {
        visitor.visitCallExpression(this);
        for (Expression param : parameters  ) {
            param.accept(visitor);
        }
    }
}
