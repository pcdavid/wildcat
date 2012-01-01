package example;

import static org.objectweb.wildcat.EventKind.*;

import java.util.concurrent.TimeUnit;

import org.objectweb.wildcat.Context;
import org.objectweb.wildcat.providers.DynamicContextProvider;
import org.objectweb.wildcat.sensors.JavaRuntimeSensor;

/**
 * This small example shows how to register watched expressions and create synthetic
 * attributes (which use the same language).
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class WatchedExpression {
    public static void main(String[] args) throws Exception {
        // Create an initial, empty context
        Context ctx = new Context();
        LoggingContextListener l = new LoggingContextListener();
        // Register to watched expressions on context elements which do not exist yet.
        // The first registration will be triggered each time the expression's value
        // changes; the second only when it becomes true.
        ctx.registerExpression(EXPRESSION_CHANGED, "/test#foo + /test#bar", l);
        ctx.registerExpression(CONDITION_OCCURED, "(/test#foo + /test#bar) > 2", l);
        // Now we populate the context with the data mentioned in the expressions.
        // In this case, we use a standard configurable context provider,
        // which we mount at /test.
        DynamicContextProvider content = ctx.createDynamicContextProvider();
        ctx.mount("/test", content);
        content.createResource("jvm");
        
        content.attachSensor(Context.createPath("jvm"), new JavaRuntimeSensor(), 500,
                TimeUnit.MILLISECONDS);
        content.startSensor(Context.createPath("jvm"));
        content.createResource("memory");
        content.createAttribute("memory#free", 0);
        content.setDefinition("memory#free", "/test/jvm#free-memory div 1024");
        ctx.register(ATTRIBUTE_CHANGED, Context.createPath("/test/memory#free"), l);
        content.createAttribute("#foo", 1);
        content.createAttribute("#bar", 1);
        content.createAttribute("#baz", null);
        content.setDefinition("#baz", "/test#foo + /test#bar");
        content.setValue(Context.createPath("#foo"), 2);
    }
}
