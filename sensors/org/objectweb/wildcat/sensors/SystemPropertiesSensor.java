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
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.objectweb.wildcat.providers.Sampler;

/**
 * Converts the system properties into {@link Sample}s. For each system
 * property, creates a sample whose name is the name of the property and whose
 * value is the value of the property, as a {@link String}.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class SystemPropertiesSensor implements Sampler {
    public Map<String, Object> sample() {
        Map<String, Object> samples = new HashMap<String, Object>();
        Properties prop = System.getProperties();
        for (Iterator iter = prop.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            samples.put(name, prop.getProperty(name));
        }
        return samples;
    }
    
    public static void main(String[] args) {
        System.out.println(new SystemPropertiesSensor().sample());
    }
}