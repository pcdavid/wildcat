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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.wildcat.TimeUtil;
import org.objectweb.wildcat.providers.Sampler;

/**
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class DateTimeSensor implements Sampler {
    public Map<String, Object> sample() {
        Map<String, Object> samples = new HashMap<String, Object>();
        long now = TimeUtil.now();
        cal.setTimeInMillis(now);
        samples.put("hour", cal.get(Calendar.HOUR));
        samples.put("minute", cal.get(Calendar.MINUTE));
        samples.put("second", cal.get(Calendar.SECOND));
        samples.put("time", now);
        return samples;
    }

    private static final Calendar cal = Calendar.getInstance();
    
    public static void main(String[] args) {
        System.out.println(new DateTimeSensor().sample());
    }
}