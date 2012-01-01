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
package org.objectweb.wildcat.sensors;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.wildcat.providers.Sampler;

/**
 * Samples informations available through the JVM {@link java.lang.Runtime}:
 * <table border="1">
 * <tr>
 * <th>Name</th>
 * <th>Description</th>
 * <th>Type</th>
 * </tr>
 * <tr>
 * <td><code>processors-count</code></td>
 * <td>The number of processors available to the JVM.</td>
 * <td>{@link java.lang.Integer}</td>
 * </tr>
 * <tr>
 * <td><code>free-memory</code></td>
 * <td>The amount of free memory available to the JVM, in bytes.</td>
 * <td>{@link java.lang.Long}</td>
 * </tr>
 * <tr>
 * <td><code>maximum-memory</code></td>
 * <td>The maximum amount of memory that the JVM will attempt to use, in bytes.
 * </td>
 * <td>{@link java.lang.Long}</td>
 * </tr>
 * <tr>
 * <td><code>total-memory</code></td>
 * <td>The total amount of memory currently available to the JVM, in bytes.
 * </td>
 * <td>{@link java.lang.Long}</td>
 * </tr>
 * </table>
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class JavaRuntimeSensor implements Sampler {
    public Map<String, Object> sample() {
        Map<String, Object> samples = new HashMap<String, Object>();
        Runtime rt = Runtime.getRuntime();
        samples.put("processors-count", rt.availableProcessors());
        samples.put("free-memory", rt.freeMemory());
        samples.put("maximum-memory", rt.maxMemory());
        samples.put("total-memory", rt.totalMemory());
        return samples;
    }
    public static void main(String[] args) {
        System.out.println(new JavaRuntimeSensor().sample());
    }
}