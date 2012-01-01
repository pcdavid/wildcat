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
 * Represents a constant expression.
 * 
 * @author Pierre-Charles David <pcavid@gmail.com>
 */
public class ConstantExpression implements Expression {
    /**
     * The constant value of this expression.
     */
    private Object value;

    /**
     * Creates a new <code>ConstantExpression</code>.
     * 
     * @param value
     *            the constant value.
     */
    public ConstantExpression(Object value) {
        this.value = value;
    }
    
    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /*
     * (non-Javadoc)
     * @see org.objectweb.wildcat.expressions.Expression#evaluate(org.objectweb.wildcat.expressions.Environment)
     */
    public Object evaluate(Environment env) throws EvaluationException {
        return value;
    }
    
    /* (non-Javadoc)
     * @see org.objectweb.wildcat.expressions.Expression#accept(org.objectweb.wildcat.expressions.ExpressionVisitor)
     */
    public void accept(ExpressionVisitor visitor) {
        visitor.visitConstantExpression(this);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return value.toString();
    }
}
