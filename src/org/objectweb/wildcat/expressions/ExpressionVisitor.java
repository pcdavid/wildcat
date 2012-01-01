package org.objectweb.wildcat.expressions;

/**
 * Implements the <em>Visitor</em> pattern for expressions.
 * 
 * @see Expression#accept(ExpressionVisitor)
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public interface ExpressionVisitor {
    /**
     * Visit an {@link AndExpression}.
     */
    void visitAndExpression(AndExpression expr);

    /**
     * Visit an {@link OrExpression}.
     */
    void visitOrExpression(OrExpression expr);

    /**
     * Visit a {@link CallExpression}.
     */
    void visitCallExpression(CallExpression expr);

    /**
     * Visit a {@link ConstantExpression}.
     */
    void visitConstantExpression(ConstantExpression expr);

    /**
     * Visit a {@link VariableExpression}.
     */
    void visitVariableExpression(VariableExpression expr);
}
