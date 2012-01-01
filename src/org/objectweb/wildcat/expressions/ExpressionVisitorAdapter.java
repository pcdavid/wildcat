package org.objectweb.wildcat.expressions;

/**
 * Adapter class to make it more convenient to create visitors which are interested only
 * to some kinds of expressions.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class ExpressionVisitorAdapter implements ExpressionVisitor {
    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.expressions.ExpressionVisitor#visitAndExpression(org.objectweb.wildcat.expressions.AndExpression)
     */
    public void visitAndExpression(AndExpression expr) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.expressions.ExpressionVisitor#visitCallExpression(org.objectweb.wildcat.expressions.CallExpression)
     */
    public void visitCallExpression(CallExpression expr) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.expressions.ExpressionVisitor#visitConstantExpression(org.objectweb.wildcat.expressions.ConstantExpression)
     */
    public void visitConstantExpression(ConstantExpression expr) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.expressions.ExpressionVisitor#visitOrExpression(org.objectweb.wildcat.expressions.OrExpression)
     */
    public void visitOrExpression(OrExpression expr) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.expressions.ExpressionVisitor#visitVariableExpression(org.objectweb.wildcat.expressions.VariableExpression)
     */
    public void visitVariableExpression(VariableExpression expr) {
    }
}
