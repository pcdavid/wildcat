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
package example.providers;

import static org.objectweb.wildcat.Context.createPath;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;

import org.objectweb.wildcat.Context;
import org.objectweb.wildcat.MalformedPathException;
import org.objectweb.wildcat.Path;
import org.objectweb.wildcat.events.EventListener;
import org.objectweb.wildcat.events.PathAddedEvent;
import org.objectweb.wildcat.events.PathEvent;
import org.objectweb.wildcat.providers.BasicContextProvider;

/**
 * This small example program tests the performance of resource and attributes creation
 * using the default {@link BasicContextProvider}. It creates a complete tree of
 * resources with attributes and reports: the number of resources and attributes created,
 * and the total time taken. The depth, width and number of attributes per resource to
 * create are supplied as numeric parameters to the program, e.g.
 * <code>java example.providers.CreationPerformanceTest 5 6 7</code> to create a full
 * tree of depth 5, where each resource has 6 sub-resources and 7 attributes.
 * <p>
 * Where run, the program pauses before and after actually populating the context, waiting
 * for the user to press enter. This is to make it easier to monitor the behaviour using
 * e.g. <code>jconsole</code>.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class CreationPerformanceTest {
    private static LineNumberReader inputReader;
    private static int depth = 0;
    private static int width = 0;
    private static int attrs = 0;

    /**
     * An event listener which counts how many resources and attributes are created.
     * 
     * @author Pierre-Charles David <pcdavid@gmail.com>
     */
    public static class EventCounter implements EventListener {
        private long resources = 0;

        private long attributes = 0;

        public void eventOccured(PathEvent evt) {
            if (evt instanceof PathAddedEvent) {
                if (evt.getPath().isAttribute()) {
                    attributes++;
                } else {
                    resources++;
                }
            }
        }

        public void eventOccured(List<PathEvent> evts) {
            for (PathEvent event : evts) {
                eventOccured(event);
            }
        }

        public long getAttributesCount() {
            return attributes;
        }

        public long getResourcesCount() {
            return resources;
        }

        public void reset() {
            resources = 0;
            attributes = 0;
        }
    }

    public static void main(String[] args) throws MalformedPathException, IOException {
        if (args.length != 3) {
            printUsageAndExit();
        }
        inputReader = new LineNumberReader(new InputStreamReader(System.in));
        try {
            depth = Integer.parseInt(args[0]);
            width = Integer.parseInt(args[1]);
            attrs = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            printUsageAndExit();
        }
        EventCounter counter = new EventCounter();
        BasicContextProvider provider = new BasicContextProvider();
        provider.setEventListener(counter);
        provider.mounted(Context.getRootPath());
        counter.reset();
        //
        System.out.print("Press enter to start");
        System.out.print(" (depth = " + depth + "; width = " + width + "; attrs = "
                + attrs + ").");
        inputReader.readLine();
        System.out.print("Populating context...");
        long start = System.currentTimeMillis();
        populate(provider, null, depth);
        long elapsed = System.currentTimeMillis() - start;
        System.out.println(" done.");
        System.out.println("Created " + counter.getResourcesCount() + " resources and "
                + counter.getAttributesCount() + " attributes in " + (elapsed / 1000.0)
                + "s.");
        System.out.print("Press enter to exit.");
        inputReader.readLine();
    }

    private static void populate(BasicContextProvider dcp, Path base, int depth) {
        if (depth > 0) {
            for (int i = 0; i < width; i++) {
                String name = "child" + i;
                Path child = (base == null) ? createPath(name) : base.appendResource(name);
                dcp.createResource(child);
                for (int j = 0; j < attrs; j++) {
                    dcp.createAttribute(child.appendAttribute("attr" + j), j);
                }
                populate(dcp, child, depth - 1);
            }
        }
    }

    private static void printUsageAndExit() {
        System.out.println("Usage: java " + CreationPerformanceTest.class.getName()
                + " <depth> <width> <attrs>");
        System.exit(1);
    }
}
