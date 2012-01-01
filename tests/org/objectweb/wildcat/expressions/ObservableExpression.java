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
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class ObservableExpression implements Expression {
    private Expression expr;
    private boolean evaluated;

    public ObservableExpression(Expression expr) {
        this.expr = expr;
        this.evaluated = false;
    }

    public Object evaluate(Environment env) throws EvaluationException {
        Object result = expr.evaluate(env);
        evaluated = true;
        return result;
    }
    
    public void accept(ExpressionVisitor visitor) {
        expr.accept(visitor);
    }

    public boolean isEvaluated() {
        return evaluated;
    }

    public void resetEvaluated() {
        this.evaluated = false;
    }
}
