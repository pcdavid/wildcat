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
package org.objectweb.wildcat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility methods to deal with time-points/timestamps.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class TimeUtil {
    /**
     * The textual format used to represent timestamps. Conforms to ISO 8601.
     */
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ssZ");

    /**
     * Formats a numeric timestamp into a textual representation.
     * 
     * @param timeStamp
     * @return
     */
    public static String format(long timeStamp) {
        return FORMAT.format(new Date(timeStamp));
    }

    /**
     * Parses a textual representation of a timestamp into a numeric timestamp.
     * 
     * @param time
     * @return
     * @throws ParseException
     *             if the <code>time</code> parameter does not conform to the expected
     *             syntax (see <code>FORMAT</code>).
     */
    public static long parse(String date) throws ParseException {
        return FORMAT.parse(date).getTime();
    }

    /**
     * Returns the current time.
     * 
     * @return the current time.
     */
    public static long now() {
        return System.currentTimeMillis();
    }
}
