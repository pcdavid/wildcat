package org.objectweb.wildcat.providers;

import org.objectweb.wildcat.Context;
import org.objectweb.wildcat.Path;
import org.objectweb.wildcat.expressions.Interpreter;

/**
 * Custom context provider used to monitor watched expressions. Watched expressions are
 * implemented as synthetic attributes with automatically generated names (<code>#expression_N</code>)
 * on the top-level resource of an internal {@link DynamicContextProvider}.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class ExpressionsProvider extends DynamicContextProvider {
    /**
     * The index used to generated synthetic attributes names.
     */
    private int expressionIndex = 0;

    /**
     * Creates a new <code>ExpressionsProvider</code>.
     * 
     * @param inter
     *            the interpreter to use to evaluate expressions.
     */
    public ExpressionsProvider(Interpreter inter) {
        super(inter);
    }

    /**
     * Creates a new synthetic attribute with the supplied definition.
     * 
     * @param expression
     *            the definition of the attribute to create.
     * @return the full path of the created attribute.
     */
    public Path createExpressionAttribute(String expression) {
        if (getPath() == null) {
            throw new IllegalStateException();
        }
        synchronized (this) {
            expressionIndex++;
        }
        Path path = Context.createPath("#expression_" + expressionIndex);
        createAttribute(path, null);
        setDefinition(path, expression);
        return getPath().append(path);
    }

    /**
     * Deletes a synthetic attribute.
     * 
     * @param path
     *            the full path to the attribute to delete (as returned from
     *            {@link #createExpressionAttribute(String)}).
     */
    public void deleteExpressionAttribute(Path path) {
        super.delete(path.relativeTo(getPath()));
    }
}
