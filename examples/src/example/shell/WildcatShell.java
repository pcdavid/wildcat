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
package example.shell;

import static org.objectweb.wildcat.Context.createPath;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jline.ConsoleReader;

import org.objectweb.wildcat.Context;
import org.objectweb.wildcat.Path;
import org.objectweb.wildcat.events.EventListener;
import org.objectweb.wildcat.events.PathEvent;
import org.objectweb.wildcat.providers.BasicContextProvider;
import org.objectweb.wildcat.providers.DynamicContextProvider;
import org.objectweb.wildcat.providers.FileSystemProvider;
import org.objectweb.wildcat.sensors.LoadSensor;

/**
 * Implements a Unix-like "shell" to interact with a WildCAT context. The shell simulates
 * some of the standard shell commands:
 * <ul>
 * <li><code>pwd</code> shows the current resource</li>
 * <li><code>cd [..|name]</code> goes to the parent resource or the named sub-resource</li>
 * <li><code>ls [name|*]</code> lists sub-resources and attributes of the current
 * resources</li>
 * <li><code>mkdir name</code> creates a new sub-resource with the given name, assuming
 * the underlying provider supports it.</li>
 * <li><code>rm [name|#name]</code> deletes the specified sub-resource or atribute,
 * assuming the underlying provider supports it.</li>
 * <li><code>cat #name</code> prints the content of the specified attribute.</li>
 * <li><code>touch #name</code> creates a new attribute with the given name and an
 * initial value of <code>null</code></li>
 * <li><code>put #name value</code> sets the value of the specified attribute (the
 * value is always interpreted as a string)</li>
 * <li><code>exit</code> quits the shell</li>
 * </ul>
 * <p>
 * As configured initialy, the context is implemented by a {@link BasicContextProvider}
 * (and hence supports <code>mkdir</code>, <code>rm</code>, <code>touch</code> and
 * <code>put</code>) and populated with some dummy data under <code>/simpsons</code>.
 * However, a {@link FileSystemProvider} is also mounted under <code>/file</code> and is
 * read-only.
 * <p>
 * All the events generated by the {@link DynamicContextProvider} are sent to
 * <code>/tmp/wildcat.log</code> and can be monitored by e.g.
 * <code>tail -f /tmp/wildcat.org</code>.
 * <p>
 * A small helper script <code>wish.sh</code> (<em>Wi</em>ldCAT <em>sh</em>ell)
 * is provided at the root of the project to launch this example.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class WildcatShell {
    private static Context context = new Context();
    private static Path pwd = Context.getRootPath();
    private static boolean exitRequested = false;
    private static BasicContextProvider provider;

    public static void main(String[] args) throws Exception {
        initialize(context);
        ConsoleReader console = new ConsoleReader();
        console.setUseHistory(true);
        while (!exitRequested) {
            try {
                execute(console.readLine("[WildCAT] " + pwd + "% ").split("\\s+"));
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
        System.exit(0);
    }

    private static void execute(String[] args) {
        String command = args[0];
        if ("ls".equals(command)) {
            String query = "*";
            if (args.length > 1) {
                query = args[1];
            }
            for (Path res : context.lookup(pwd.append(query))) {
                System.out.println(res);
            }
            if (pwd.size() > 0 && query.indexOf('#') == -1) {
                for (Path res : context.lookup(pwd.append("#" + query))) {
                    System.out.println(res);
                }
            }
        } else if ("pwd".equals(command)) {
            System.out.println(pwd);
        } else if ("cat".equals(command)) {
            Collection<Path> attr = context.lookup(pwd.append(args[1]));
            if (attr.isEmpty()) {
                System.err.println("No such attribute.");
            } else {
                System.out.println(context.lookupAttribute(pwd.append(args[1])));
            }
        } else if ("cd".equals(command)) {
            if (args.length != 2 || !args[1].matches("(\\.\\.)|([^\\*]+)")) {
                System.err.println("Usage: cd [res | ..] (ex: cd foo)");
            } else if ("..".equals(args[1])) {
                if (pwd.size() >= 1) {
                    pwd = pwd.getParent();
                } else {
                    System.err.println("Already at context root.");
                }
            } else {
                Collection<Path> res = context.lookup(pwd.append(args[1]));
                if (res.isEmpty()) {
                    System.err.println("No such subresource.");
                } else {
                    pwd = res.iterator().next();
                }
            }
        } else if ("mkdir".equals(command)) {
            if (args.length != 2) {
                System.err.println("Usage: mkdir <resource>");
            } else {
                provider.createResource(pwd.append(args[1])
                        .relativeTo(provider.getPath()));
            }
        } else if ("touch".equals(command)) {
            if (args.length != 2 || !args[1].matches("#[^\\*]+")) {
                System.err.println("Usage: touch <attribute> (ex: touch #foo)");
            } else {
                provider.createAttribute(pwd.append(args[1]).relativeTo(
                        provider.getPath()), null);
            }
        } else if ("put".equals(command)) {
            if (args.length != 3 || !args[1].matches("#[^\\*]+")) {
                System.err.println("Usage: put <attribute> <value> (ex: put #foo bar)");
            } else {
                provider.setValue(pwd.append(args[1]).relativeTo(provider.getPath()),
                        args[2]);
            }
        } else if ("rm".equals(command)) {
            if (args.length != 2) {
                System.err.println("Usage: rm [<resource> | <attribute>]");
            } else {
                provider.delete(pwd.append(args[1]).relativeTo(provider.getPath()));
            }
        } else if ("exit".equals(command)) {
            exitRequested = true;
        } else {
            System.err.println("Command not found.");
        }
    }

    private static void initialize(Context ctx) throws Exception {
        provider = ctx.createDynamicContextProvider();
        ctx.mount("/simpsons", provider);
        provider.createResource(createPath("homer"));
        provider.createAttribute(createPath("homer#quote"), "doh!");
        provider.createResource(createPath("bart"));
        provider.createAttribute(createPath("bart#quote"), "eat my shorts");
        provider.createResource(createPath("bender"));
        provider.createAttribute(createPath("bender#quote"), "bite my shiny metal ass");
        ctx.mount("/file", new FileSystemProvider(new File("/")));
        DynamicContextProvider sys = ctx.createDynamicContextProvider();
        ctx.mount("/sys", sys);
        Path load = createPath("load");
        sys.createResource(load);
        sys.attachSensor(load, new LoadSensor(), 2, TimeUnit.SECONDS);
        sys.startSensor(load);
    }

    public static class LoggingEventListener implements EventListener {
        private FileWriter writer;

        public LoggingEventListener(String fileName) throws IOException {
            writer = new FileWriter(fileName, true);
        }

        public void eventOccured(PathEvent evt) {
            try {
                writer.write(evt.toString());
                writer.write("\n");
                writer.flush();
            } catch (IOException e) {
                System.err.println("Logging error: " + e.getMessage());
            }
        }

        public void eventOccured(List<PathEvent> evts) {
            for (PathEvent event : evts) {
                eventOccured(event);
            }
        }
    }
}
