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
package org.objectweb.wildcat.providers;

import static org.junit.Assert.*;
import static org.objectweb.wildcat.Context.createPath;
import static org.objectweb.wildcat.TestUtils.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the {@link BasicContextProvider} class.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class BasicContextProviderTests {
    private EventRecorder recorder;

    private BasicContextProvider provider;

    @Before
    public void setUp() {
        recorder = new EventRecorder();
        provider = new BasicContextProvider();
        provider.setEventListener(recorder);
    }

    @Test(expected = IllegalStateException.class)
    public void unmountUnmountedProvider() {
        provider.unmounted();
    }

    @Test(expected = IllegalStateException.class)
    public void mountMountedProvider() {
        provider.mounted(createPath("/here"));
        provider.mounted(createPath("/somewhere/else"));
    }

    @Test
    public void mountProviderAsRoot() {
        provider.mounted(createPath("/"));
        assertEquals(createPath("/"), provider.getPath());
        assertRecorded(recorder, added("/"));
    }

    @Test
    public void mountProviderAsResource() {
        provider.mounted(createPath("/foo/bar"));
        assertEquals(createPath("/foo/bar"), provider.getPath());
        assertRecorded(recorder, added("/foo/bar"));
    }

    @Test
    public void unmountEmptyRootProvider() {
        provider.mounted(createPath("/"));
        recorder.clear();
        provider.unmounted();
        assertNull(provider.getPath());
        assertRecorded(recorder, removed("/"));
    }

    @Test
    public void unmountEmptyResourceProvider() {
        provider.mounted(createPath("/foo/bar"));
        recorder.clear();
        provider.unmounted();
        assertNull(provider.getPath());
        assertRecorded(recorder, removed("/foo/bar"));
    }

    @Test
    public void createSubResourceFromRootProvider() {
        provider.mounted(createPath("/"));
        recorder.clear();
        provider.createResource(createPath("foo"));
        assertRecorded(recorder, added("/foo"));
        assertLookup(provider, "*", "/foo");
        assertLookup(provider, "foo", "/foo");
        assertLookup(provider, "bar");
    }

    @Test
    public void createDeepSubResourceFromRootProvider() {
        provider.mounted(createPath("/"));
        recorder.clear();
        provider.createResource(createPath("baz/qux"));
        assertRecorded(recorder, added("/baz"), added("/baz/qux"));
        assertLookup(provider, "*", "/baz");
        assertLookup(provider, "baz/*", "/baz/qux");
        assertLookup(provider, "baz/qux", "/baz/qux");
        assertLookup(provider, "baz/quux");
    }

    @Test
    public void createDeepSubResourceWithExistingParentFromRootProvider() {
        provider.mounted(createPath("/"));
        provider.createResource(createPath("baz"));
        recorder.clear();
        provider.createResource(createPath("baz/qux"));
        assertRecorded(recorder, added("/baz/qux"));
    }

    @Test
    public void createSubResourceFromResourceProvider() {
        provider.mounted(createPath("/fred"));
        recorder.clear();
        provider.createResource(createPath("foo"));
        assertRecorded(recorder, added("/fred/foo"));
    }

    @Test
    public void createDeepSubResourceFromResourceProvider() {
        provider.mounted(createPath("/fred"));
        recorder.clear();
        provider.createResource(createPath("baz/qux"));
        assertRecorded(recorder, added("/fred/baz"), added("/fred/baz/qux"));
    }

    @Test
    public void createDeepSubResourceWithExistingParentFromResourceProvider() {
        provider.mounted(createPath("/fred"));
        provider.createResource(createPath("baz"));
        recorder.clear();
        provider.createResource(createPath("baz/qux"));
        assertRecorded(recorder, added("/fred/baz/qux"));
    }

    @Test
    public void deleteLeaveResourceWithouthAttributes() {
        provider.mounted(createPath("/cartoons"));
        provider.createResource(createPath("simpsons"));
        provider.createResource(createPath("simpsons/bart"));
        recorder.clear();
        provider.delete(createPath("simpsons/bart"));
        assertRecorded(recorder, removed("/cartoons/simpsons/bart"));
        assertLookup(provider, "simpsons/bart");
        assertLookup(provider, "simpsons/*");
    }

    @Test
    public void deleteLeaveResourceWithAttributes() {
        provider.mounted(createPath("/cartoons"));
        provider.createResource(createPath("simpsons"));
        provider.createResource(createPath("simpsons/bart"));
        provider.createAttribute(createPath("simpsons/bart#moto"), "eat my shorts");
        recorder.clear();
        provider.delete(createPath("simpsons/bart"));
        assertRecorded(recorder, removed("/cartoons/simpsons/bart#moto"),
                removed("/cartoons/simpsons/bart"));
        assertLookup(provider, "simpsons/bart");
        assertLookup(provider, "simpsons/*");
    }

    @Test
    public void deleteSubTree() {
        provider.mounted(createPath("/"));
        provider.createResource(createPath("cartoons"));
        provider.createResource(createPath("cartoons/simpsons"));
        provider.createResource(createPath("cartoons/simpsons/bart"));
        provider.createAttribute(createPath("cartoons/simpsons/bart#moto"),
                "eat my shorts");
        recorder.clear();
        provider.delete(createPath("cartoons"));
        assertRecorded(recorder, removed("/cartoons/simpsons/bart#moto"),
                removed("/cartoons/simpsons/bart"), removed("/cartoons/simpsons"),
                removed("/cartoons"));
        assertLookup(provider, "*");
        assertLookup(provider, "cartoons");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createAttributeOnRoot() {
        provider.mounted(createPath("/"));
        provider.createAttribute(createPath("#foo"), "foo");
    }

    @Test
    public void createAttribute() {
        provider.mounted(createPath("/"));
        provider.createResource(createPath("foo"));
        recorder.clear();
        provider.createAttribute(createPath("foo#bar"), "bar");
        assertRecorded(recorder, added("/foo#bar"), changed("/foo#bar", null, "bar"));
        assertLookup(provider, "foo#*", "/foo#bar");
    }

    @Test
    public void readAttribute() {
        provider.mounted(createPath("/"));
        provider.createResource(createPath("foo"));
        recorder.clear();
        provider.createAttribute(createPath("foo#bar"), "bar");
        assertEquals("bar", provider.lookupAttribute(createPath("foo#bar")));
    }

    @Test
    public void readNonExistingAttribute() {
        provider.mounted(createPath("/"));
        provider.createResource(createPath("foo"));
        recorder.clear();
        provider.createAttribute(createPath("foo#bar"), "bar");
        assertNull(provider.lookupAttribute(createPath("foo#baz")));
    }

    @Test
    public void changeAttributeValue() {
        provider.mounted(createPath("/"));
        provider.createResource(createPath("foo"));
        provider.createAttribute(createPath("foo#bar"), "bar");
        assertEquals("bar", provider.lookupAttribute(createPath("foo#bar")));
        recorder.clear();
        provider.setValue(createPath("foo#bar"), "baz");
        assertRecorded(recorder, changed("/foo#bar", "bar", "baz"));
        assertEquals("baz", provider.lookupAttribute(createPath("foo#bar")));
    }

    @Test
    public void deleteAttribute() {
        provider.mounted(createPath("/"));
        provider.createResource(createPath("foo"));
        provider.createAttribute(createPath("foo#bar"), "bar");
        recorder.clear();
        provider.delete(createPath("foo#bar"));
        assertRecorded(recorder, removed("/foo#bar"));
        assertLookup(provider, "foo#bar");
        assertLookup(provider, "foo#*");
    }
}
