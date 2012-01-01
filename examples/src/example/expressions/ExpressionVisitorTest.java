package example.expressions;

import static org.objectweb.wildcat.Context.createPath;

import org.objectweb.wildcat.Context;
import org.objectweb.wildcat.EventKind;
import org.objectweb.wildcat.providers.DynamicContextProvider;

import example.LoggingContextListener;

/**
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class ExpressionVisitorTest {
    public static void main(String[] args) throws Exception {
        Context ctx = new Context();
        DynamicContextProvider provider = ctx.createDynamicContextProvider();
        ctx.mount("/test", provider);
        provider.createResource("foo");
        provider.createAttribute("foo#bar", 42);
        provider.createAttribute("foo#baz", 43);
        provider.createAttribute("foo#qux", null);
        provider.setDefinition("foo#qux", "/test/foo#bar + /test/foo#baz");
        System.out.println("qux = " + ctx.lookupAttribute("/test/foo#qux"));
        ctx.register(EventKind.ATTRIBUTE_CHANGED, createPath("/test/foo#*"),
                new LoggingContextListener());
        provider.setValue(createPath("foo#bar"), 44);
        Thread.sleep(100);
        System.out.println("qux = " + ctx.lookupAttribute("/test/foo#qux"));
    }
}
