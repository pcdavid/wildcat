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
import static org.objectweb.wildcat.EventKind.ATTRIBUTE_CHANGED;

import org.objectweb.wildcat.Context;
import org.objectweb.wildcat.providers.DynamicContextProvider;
import org.objectweb.wildcat.sensors.CPUSensor;
import org.objectweb.wildcat.sensors.JavaRuntimeSensor;
import org.objectweb.wildcat.sensors.LoadSensor;

import example.LoggingContextListener;

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
        ctx.register(ATTRIBUTE_CHANGED, createPath("/sys/*#*"),
                new LoggingContextListener());
        ctx.mount("/sys", provider);
        provider.createResource(createPath("load"));
        provider.attachSensor(createPath("load"), new LoadSensor(), 2, SECONDS);
        provider.startSensor(createPath("load"));
        provider.createResource(createPath("cpu"));
        provider.attachSensor(createPath("cpu"), new CPUSensor(), 1, SECONDS);
        provider.startSensor(createPath("cpu"));
        provider.createResource(createPath("jvm"));
        provider.attachSensor(createPath("jvm"), new JavaRuntimeSensor(), 500,
                MILLISECONDS);
        provider.startSensor(createPath("jvm"));
    }
}
