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
 * Represents a boolean disjunction.
 * 
 * @author Pierre-Charles David <PierreCharles.David@francetelecom.com>
 */
public class OrExpression implements Expression {
    private Expression[] clauses;

    /**
     * Creates a new <code>OrExpression</code>.
     * 
     * @param clauses
     *            the clauses of the disjunction.
     */
    public OrExpression(Expression... clauses) {
        this.clauses = clauses;
    }
    
    /**
     * @return the clauses
     */
    public Expression[] getClauses() {
        return clauses;
    }

    /*
     * (non-Javadoc)
     * @see org.objectweb.wildcat.expressions.Expression#evaluate(org.objectweb.wildcat.expressions.Environment)
     */
    public Object evaluate(Environment env) throws EvaluationException {
        for (Expression clause : clauses) {
            Object result = clause.evaluate(env);
            boolean clauseValue = false;
            if (result instanceof Boolean) {
                clauseValue = ((Boolean) result).booleanValue();
            }
            if (clauseValue) {
                // Stop as soon as a clause is true.
                return result;
            }
        }
        return Boolean.FALSE;
    }
    
    
    /* (non-Javadoc)
     * @see org.objectweb.wildcat.expressions.Expression#accept(org.objectweb.wildcat.expressions.ExpressionVisitor)
     */
    public void accept(ExpressionVisitor visitor) {
        visitor.visitOrExpression(this);
        for (Expression clause : clauses) {
            clause.accept(visitor);
        }
    }
}
