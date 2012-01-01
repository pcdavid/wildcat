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

import java.text.ParseException;
import java.util.Map;

/**
 * This sensor gathers static information about the CPU of the host. It uses
 * Linux's <tt>/proc/cpuinfo</tt> file.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class CPUSensor extends TextFileSensor {
    /**
     * Creates a sensor. No configuration needed/possible.
     *
     */
    public CPUSensor() {
        super("/proc/cpuinfo");
    }

    /*
     *  (non-Javadoc)
     * @see org.obasco.wildcat.sensors.TextFileSensor#fillSamples(org.obasco.wildcat.sensing.SampleSet, java.lang.String[])
     */
    protected void fillSamples(Map<String, Object> samples, String[] lines) {
        for (int i = 0; i < LINE_FORMATS.length; i++) {
            String name = LINE_FORMATS[i].getProperty();
            Object value = null;
            try {
                value = ((Object[]) LINE_FORMATS[i].getFormat().parseObject(lines[i]))[0];
            } catch (ParseException e) {
                // ignore and continue
            }
            if (value instanceof Number) {
                samples.put(name, value);
            } else if (value instanceof String) {
                samples.put(name, value);
            } else if (value instanceof Boolean) {
                samples.put(name, value);
            } else {
                throw new RuntimeException("Should not happen.");
            }
        }
    }

    /**
     * Describes the format of the lines in <tt>/proc/cpuinfo</tt>.
     */
    private static final LineFormat[] LINE_FORMATS = new LineFormat[] {
            new LineFormat("processor", "processor\t: {0,number,integer}"),
            new LineFormat("vendor_id", "vendor_id\t: {0}"),
            new LineFormat("cpu_family", "cpu family\t: {0,number,integer}"),
            new LineFormat("model", "model\t\t: {0,number,integer}"),
            new LineFormat("model_name", "model name\t: {0}"),
            new LineFormat("stepping", "stepping\t: {0,number,integer}"),
            new LineFormat("speed", "cpu MHz\t\t: {0,number}"),
            new LineFormat("cache", "cache size\t: {0,number,integer} KB"),
            new LineFormat("bugs_fdiv", "fdiv_bug\t: {0}"), new LineFormat("bugs_hlt", "hlt_bug\t\t: {0}"),
            new LineFormat("bugs_f00F", "f00f_bug\t: {0}"), new LineFormat("bugs_coma", "coma_bug\t: {0}"),
            new LineFormat("fpu", "fpu\t\t: {0}"), new LineFormat("fpu_exception", "fpu_exception\t: {0}"),
            new LineFormat("cpuid_level", "cpuid level\t: {0,number,integer}"), new LineFormat("wp", "wp\t\t: {0}"),
            new LineFormat("flags", "flags\t\t: {0}"), new LineFormat("bogomips", "bogomips\t: {0,number}") };
    
    public static void main(String[] args) {
        System.out.println(new CPUSensor().sample());
    }
}