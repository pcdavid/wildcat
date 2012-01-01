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
import java.text.ParseException;
import java.util.Map;

/**
 * This probe reads Linux kernel information from <code>/proc/version</code>.
 * The samples it produces are: <table border="1">
 * <tr>
 * <th>Name</th>
 * <th>Description</th>
 * <th>Type</th>
 * </tr>
 * <tr>
 * <td><code>uname</code></td>
 * <td>Full kernel information, i.e. verbatim content of
 * <code>/proc/version</code>.</td>
 * <td>{@link java.lang.String}</td>
 * </tr>
 * <tr>
 * <td><code>version</code></td>
 * <td>The kernel version number, eg. <code>2.4.21</code>.</td>
 * <td>{@link java.lang.String}</td>
 * </tr>
 * <tr>
 * <td><code>major-version</code></td>
 * <td>The major version number, eg. <code>2</code> for kernel
 * <code>2.4.21</code></td>
 * <td>{@link java.lang.Integer}</td>
 * </tr>
 * <tr>
 * <td><code>minor-version</code></td>
 * <td>The minor version number, eg. <code>4</code> for kernel
 * <code>2.4.21</code></td>
 * <td>{@link java.lang.Integer}</td>
 * </tr>
 * <tr>
 * <td><code>patch-level</code></td>
 * <td>The patch-level, eg. <code>21</code> for kernel <code>2.4.21</code>
 * </td>
 * <td>{@link java.lang.Integer}</td>
 * </tr>
 * </table>
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class KernelVersionSensor extends TextFileSensor {
    public KernelVersionSensor() {
        super("/proc/version");
    }

    protected void fillSamples(Map<String, Object> samples, String[] lines) {
        assert (lines != null && lines.length == 1);
        String version = lines[0];
        samples.put("uname", version);
        try {
            Object[] numbers = (Object[]) VERSION_NUMBER.parseObject(version);
            assert (numbers != null && numbers.length == 3);
            assert (numbers[0] instanceof Integer && numbers[1] instanceof Integer && numbers[2] instanceof Integer);
            samples.put("major-version", numbers[0]);
            samples.put("minor-version", numbers[1]);
            samples.put("patch-level", numbers[2]);
            samples.put("version", numbers[0] + "." + numbers[1] + "." + numbers[2]);
        } catch (ParseException pe) {
            // Do nothing.
        }
    }

    private static final MessageFormat VERSION_NUMBER = new MessageFormat(
            "Linux version {0,number,integer}.{1,number,integer}.{2,number,integer}");
    
    public static void main(String[] args) {
        System.out.println(new KernelVersionSensor().sample());
    }
}