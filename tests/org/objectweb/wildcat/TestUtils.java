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

import static org.junit.Assert.*;
import static org.objectweb.wildcat.Context.createPath;

import java.util.ArrayList;
import java.util.Collection;

import org.objectweb.wildcat.events.PathAddedEvent;
import org.objectweb.wildcat.events.PathChangedEvent;
import org.objectweb.wildcat.events.PathEvent;
import org.objectweb.wildcat.events.PathRemovedEvent;
import org.objectweb.wildcat.providers.EventRecorder;

/**
 * This class provides some utility methods for writing WildCAT tests, including
 * WildCAT-specific assertions.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public abstract class TestUtils {
    private TestUtils() {
    }

    /**
     * Creates a new <code>PathAddedEvent</code> at the given location.
     */
    public static PathAddedEvent added(String location) {
        return new PathAddedEvent(createPath(location));
    }

    /**
     * Creates a new <code>PathRemovedEvent</code> at the given location.
     */
    public static PathRemovedEvent removed(String location) {
        return new PathRemovedEvent(createPath(location));
    }

    /**
     * Creates a new <code>PathChangedEvent</code> at the given location and values.
     */
    public static PathChangedEvent changed(String location, Object oldValue,
            Object newValue) {
        return new PathChangedEvent(createPath(location), oldValue, newValue);
    }

    /**
     * Checks that two collections contain the same elements, not taking the order into
     * account.
     */
    @SuppressWarnings("unchecked")
    public static void assertContentEquals(Collection expected, Collection actual) {
        if (!expected.containsAll(actual)) {
            Collection additional = new ArrayList(actual);
            additional.removeAll(expected);
            fail("Unexpected elements: " + additional);
        } else if (!actual.containsAll(expected)) {
            Collection missing = new ArrayList(expected);
            missing.removeAll(actual);
            fail("Missing elements: " + missing);
        }
    }

    /**
     * Checks the result of a {@link ContextProvider#lookup(Path)} call.
     * 
     * @param provider the provider to query
     * @param query the path to query
     * @param expected the expected paths in the result (ordering irrelevant)
     */
    public static void assertLookup(ContextProvider provider, String query,
            String... expected) {
        Collection<Path> expectedPaths = new ArrayList<Path>();
        for (String p : expected) {
            expectedPaths.add(createPath(p));
        }
        assertContentEquals(expectedPaths, provider.lookup(createPath(query)));
    }

    /**
     * Checks the trace of events recorded by an {@link EventRecorder}
     * 
     * @param recorder the {@link EventRecorder} to checks
     * @param trace the expected trace of events, in the expected order.
     */
    public static void assertRecorded(EventRecorder recorder, PathEvent... trace) {
        assertTrue("Invalid recorded event trace.", recorder.recorded(trace));
    }
}
