package org.objectweb.wildcat.expressions.functions;

import org.objectweb.wildcat.Context;
import org.objectweb.wildcat.Path;
import org.objectweb.wildcat.expressions.Environment;
import org.objectweb.wildcat.expressions.EvaluationException;

/**
 * Implements the <code>attribute</code> function which, given a {@link Path} and a
 * {@link Context}, returns the current value of the attribute denoted by the path, in
 * that context.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class AttributeFunction implements Function {
    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.expressions.functions.Function#getName()
     */
    public String getName() {
        return "attribute";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.expressions.functions.Function#apply(java.lang.Object[],
     *      org.objectweb.wildcat.expressions.Environment)
     */
    public Object apply(Object[] args, Environment env) throws EvaluationException {
        Path path = (Path) args[0];
        Context ctx = (Context) args[1];
        return ctx.lookupAttribute(path);
    }
}
