package example;

import static org.objectweb.wildcat.EventKind.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.objectweb.wildcat.Context;
import org.objectweb.wildcat.providers.DynamicContextProvider;
import org.objectweb.wildcat.sensors.JavaRuntimeSensor;

/**
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class WatchedExpression {
    public static void main(String[] args) throws Exception {
        Context ctx = new Context();
        LoggingContextListener l = new LoggingContextListener();
        ctx.registerExpression(EXPRESSION_CHANGED, "/test#foo + /test#bar", l);
        ctx.registerExpression(CONDITION_OCCURED, "(/test#foo + /test#bar) > 2", l);
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
        System.out.println("Press enter to finish...");
        new BufferedReader(new InputStreamReader(System.in)).readLine();
    }
}
