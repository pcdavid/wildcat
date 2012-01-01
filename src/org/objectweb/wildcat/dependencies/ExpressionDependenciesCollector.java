package org.objectweb.wildcat.dependencies;

import java.util.Collection;
import java.util.HashSet;

import org.objectweb.wildcat.Path;
import org.objectweb.wildcat.expressions.ConstantExpression;
import org.objectweb.wildcat.expressions.ExpressionVisitorAdapter;

/**
 * An expression visitor which collects all the literal paths constants it finds in the
 * expression, effectively computing the dependencies of the expression.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class ExpressionDependenciesCollector extends ExpressionVisitorAdapter {
    private Collection<Path> dependencies = new HashSet<Path>();

    /**
     * Returns set of all {@link Path}s mentioned in the expression visited.
     * 
     * @return the dependencies of the visited expression.
     */
    public Collection<Path> getDependencies() {
        return dependencies;
    }

    /**
     * Adds literal {@link Path}s to the set of dependencies.
     */
    @Override
    public void visitConstantExpression(ConstantExpression expr) {
        Object value = expr.getValue();
        if (value instanceof Path) {
            dependencies.add((Path) value);
        }
    }
}
