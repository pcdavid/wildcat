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
package example.sensors;

import static java.util.concurrent.TimeUnit.*;
import static org.objectweb.wildcat.Context.createPath;

import java.util.List;

import org.objectweb.wildcat.Context;
import org.objectweb.wildcat.events.EventListener;
import org.objectweb.wildcat.events.PathEvent;
import org.objectweb.wildcat.providers.DynamicContextProvider;
import org.objectweb.wildcat.sensors.CPUSensor;
import org.objectweb.wildcat.sensors.JavaRuntimeSensor;
import org.objectweb.wildcat.sensors.LoadSensor;

/**
 * This small example show how to attach sensors to resources so that their attributes
 * will be regularly updated from external measures.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class SensorsExample {
    public static void main(String[] args) throws Exception {
        Context ctx = new Context();
        DynamicContextProvider provider = ctx.createDynamicContextProvider();
        provider.setEventListener(new LoggingEventListener());
        ctx.mount(Context.getRootPath(), provider);
        provider.createResource(createPath("sys"));
        provider.createResource(createPath("sys/load"));
        provider.attachSensor(createPath("sys/load"), new LoadSensor(), 2, SECONDS);
        provider.startSensor(createPath("sys/load"));
        provider.createResource(createPath("sys/cpu"));
        provider.attachSensor(createPath("sys/cpu"), new CPUSensor(), 60, SECONDS);
        provider.startSensor(createPath("sys/cpu"));
        provider.createResource(createPath("jvm"));
        provider.attachSensor(createPath("jvm"), new JavaRuntimeSensor(), 500,
                MILLISECONDS);
        provider.startSensor(createPath("jvm"));
    }

    public static class LoggingEventListener implements EventListener {
        public void eventOccured(PathEvent evt) {
            System.err.println(evt);
        }

        public void eventOccured(List<PathEvent> evts) {
            for (PathEvent evt : evts) {
                eventOccured(evt);
            }
        }
    }
}
