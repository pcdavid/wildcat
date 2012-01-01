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

import java.text.MessageFormat;
import java.text.ParsePosition;
import java.util.Map;

/**
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class LoadSensor extends TextFileSensor {
    public LoadSensor() {
        super("/proc/loadavg");
    }

    protected void fillSamples(Map<String, Object> samples, String[] lines) {
        Object[] loads = FORMAT.parse(lines[0], new ParsePosition(0));
        samples.put("avg1", new Double((String) loads[0]));
        samples.put("avg5", new Double((String) loads[1]));
        samples.put("avg15", new Double((String) loads[2]));
    }

    private static MessageFormat FORMAT = new MessageFormat("{0} {1} {2} {3}");
    
    public static void main(String[] args) {
        System.out.println(new LoadSensor().sample());
    }
}