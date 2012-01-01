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

/**
 * Represents a variable access.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class VariableExpression implements Expression {
    private String variableName;

    /**
     * Creates a new <code>VariableExpression</code>.
     * 
     * @param name
     *            the name of the variable.
     */
    public VariableExpression(String name) {
        variableName = name;
    }
    
    /**
     * @return the variableName
     */
    public String getVariableName() {
        return variableName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.expressions.Expression#evaluate(org.objectweb.wildcat.expressions.Environment)
     */
    public Object evaluate(Environment env) throws EvaluationException {
        Object result = env.lookupVariable(variableName);
        if (result == null) {
            throw new EvaluationException("Unknown variable dereferenced: "
                    + variableName + ".");
        }
        return result;
    }
    
    /* (non-Javadoc)
     * @see org.objectweb.wildcat.expressions.Expression#accept(org.objectweb.wildcat.expressions.ExpressionVisitor)
     */
    public void accept(ExpressionVisitor visitor) {
        visitor.visitVariableExpression(this);
    }
}
