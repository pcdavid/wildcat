package org.objectweb.wildcat.expressions;

/**
 * Implements the <em>Visitor</em> pattern for expressions.
 * 
 * @see Expression#accept(ExpressionVisitor)
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public interface ExpressionVisitor {
    void visitAndExpression(AndExpression expr);
    void visitOrExpression(OrExpression expr);
    void visitCallExpression(CallExpression expr);
    void visitConstantExpression(ConstantExpression expr);
    void visitVariableExpression(VariableExpression expr);
}
