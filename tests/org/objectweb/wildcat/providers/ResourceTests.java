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

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.wildcat.Context;
import org.objectweb.wildcat.Path;

/**
 * Tests for the {@link Resource} class.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class ResourceTests {
    private Resource root;

    private EventRecorder recorder;

    @Before
    public void setUp() {
        recorder = new EventRecorder();
        root = new Resource(Context.getRootPath(), recorder);
    }

    @Test
    public void createRootResource() {
        assertEquals(Context.getRootPath(), root.getPath());
        assertNoSubResources(root);
        assertNoAttributes(root);
        assertTrue(recorder.isEmpty());
    }

    @Test
    public void createNonRootResource() {
        Path p = createPath("/foo/bar");
        Resource r = new Resource(p, recorder);
        assertEquals(p, r.getPath());
        assertNoSubResources(r);
        assertNoAttributes(r);
        assertTrue(recorder.isEmpty());
    }

    @Test(expected = AssertionError.class)
    public void createResourceWithNullPath() {
        new Resource(null, recorder);
    }

    @Test(expected = AssertionError.class)
    public void createResourceWithRelativePath() {
        new Resource(createPath("foo/bar"), recorder);
    }

    @Test(expected = AssertionError.class)
    public void createResourceWithAttributePath() {
        new Resource(createPath("/foo#bar"), recorder);
    }

    @Test(expected = AssertionError.class)
    public void createResourceWithPatternPath() {
        new Resource(createPath("/foo/*"), recorder);
    }

    @Test(expected = AssertionError.class)
    public void createResourceWithNullListener() {
        new Resource(createPath("/foo"), null);
    }

    @Test
    public void createChild() {
        Resource foo = root.createChild("foo");
        assertNotNull(foo);
        assertEquals(createPath("/foo"), foo.getPath());
        assertTrue(root.hasChild("foo"));
        Collection<Resource> kids = root.getChildren();
        assertNotNull(kids);
        assertEquals(1, kids.size());
        assertTrue(kids.contains(foo));
        assertContentEquals(kids, root.getChildrenMatching("foo"));
        assertContentEquals(kids, root.getChildrenMatching("*"));
        assertTrue(root.getChildrenMatching("bar").isEmpty());
        assertRecorded(recorder, added("/foo"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createExistingChild() {
        root.createChild("foo");
        root.createChild("foo");
    }

    @Test
    public void createMultipleChildren() {
        Collection<Resource> created = new ArrayList<Resource>() {
            {
                add(root.createChild("foo"));
                add(root.createChild("bar"));
                add(root.createChild("baz"));
            }
        };
        assertRecorded(recorder, added("/foo"), added("/bar"), added("/baz"));
        Collection<Resource> actual = root.getChildren();
        assertContentEquals(created, actual);
        actual = root.getChildrenMatching("*");
        assertContentEquals(created, actual);
        actual = root.getChildrenMatching("foo");
        assertEquals(1, actual.size());
        Resource child = actual.iterator().next();
        assertEquals(createPath("/foo"), child.getPath());
        assertEquals(root.getChild("foo"), child);
    }

    @Test
    public void deleteChild() {
        Resource foo = root.createChild("foo");
        root.createChild("bar");
        assertEquals(2, root.getChildren().size());
        assertEquals(2, recorder.size());
        recorder.clear();
        root.deleteChild("foo");
        assertTrue(!root.hasChild("foo"));
        assertTrue(root.hasChild("bar"));
        assertEquals(1, root.getChildren().size());
        assertEquals(1, recorder.size());
        assertRecorded(recorder, removed(foo.getPath().toString()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void deletedNonExistingChild() {
        root.deleteChild("foo");
    }

    @Test
    public void disposeResource() {
        root.createChild("foo").createChild("bar").createAttribute("baz", "baz");
        assertRecorded(recorder, added("/foo"), added("/foo/bar"), added("/foo/bar#baz"),
                changed("/foo/bar#baz", null, "baz"));
        recorder.clear();
        root.dispose();
        assertEquals(3, recorder.size());
        assertRecorded(recorder, removed("/foo/bar#baz"), removed("/foo/bar"),
                removed("/foo"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createAttributeOnRoot() {
        root.createAttribute("foo", "foo");
    }

    @Test
    public void createAttribute() {
        Resource foo = root.createChild("foo");
        recorder.clear();
        foo.createAttribute("bar", "bar");
        assertRecorded(recorder, added("/foo#bar"), changed("/foo#bar", null, "bar"));
        //
        Collection<String> attrs = foo.getAttributes();
        assertNotNull(attrs);
        assertEquals(1, attrs.size());
        assertTrue(attrs.contains("bar"));
        //
        Collection<Path> ap = foo.getAttributesMatching("bar");
        assertEquals(1, ap.size());
        assertEquals(createPath("/foo#bar"), ap.iterator().next());
        ap = foo.getAttributesMatching("*");
        assertEquals(1, ap.size());
        assertEquals(createPath("/foo#bar"), ap.iterator().next());
        ap = foo.getAttributesMatching("foo");
        assertEquals(0, ap.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createExistingAttribute() {
        Resource foo = root.createChild("foo");
        foo.createAttribute("bar", "bar");
        foo.createAttribute("bar", "bar");
    }

    @Test
    public void createMultipleAttributes() {
        Resource flinstones = root.createChild("flinstones");
        recorder.clear();
        flinstones.createAttribute("fred", "fred");
        flinstones.createAttribute("wilma", "wilma");
        flinstones.createAttribute("barney", "barney");
        assertRecorded(recorder, added("/flinstones#fred"), changed("/flinstones#fred",
                null, "fred"), added("/flinstones#wilma"), changed("/flinstones#wilma",
                null, "wilma"), added("/flinstones#barney"), changed(
                "/flinstones#barney", null, "barney"));
        assertTrue(flinstones.hasAttribute("fred"));
        assertTrue(flinstones.hasAttribute("wilma"));
        assertTrue(flinstones.hasAttribute("barney"));
        assertTrue(!flinstones.hasAttribute("homer"));
        Collection<String> attrs = flinstones.getAttributes();
        assertContentEquals(attrs, new ArrayList<String>() {
            {
                add("fred");
                add("wilma");
                add("barney");
            }
        });
        Collection<Path> attrPaths = flinstones.getAttributesMatching("*");
        assertContentEquals(attrPaths, new ArrayList<Path>() {
            {
                add(createPath("/flinstones#fred"));
                add(createPath("/flinstones#wilma"));
                add(createPath("/flinstones#barney"));
            }
        });
        attrPaths = flinstones.getAttributesMatching("fred");
        assertTrue(attrPaths.size() == 1
                && attrPaths.iterator().next().equals(createPath("/flinstones#fred")));
    }

    @Test
    public void deleteAttribute() {
        Resource simpsons = root.createChild("simpsons");
        simpsons.createAttribute("homer", "doh");
        simpsons.createAttribute("bart", "eat my shorts");
        simpsons.createAttribute("bender", "bite my shiny metal ass");
        recorder.clear();
        simpsons.deleteAttribute("bender"); // Wrong show!
        assertRecorded(recorder, removed("/simpsons#bender"));
        assertTrue(!simpsons.hasAttribute("bender"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void deletedNonExistingAttribute() {
        root.deleteAttribute("spoon");
    }

    @Test
    public void readAttributeValue() {
        Resource simpsons = root.createChild("simpsons");
        simpsons.createAttribute("homer", "doh");
        assertEquals("doh", simpsons.getAttributeValue("homer"));
    }

    @Test
    public void changeAttributeValue() {
        Resource simpsons = root.createChild("simpsons");
        simpsons.createAttribute("homer", "doh");
        recorder.clear();
        simpsons.setAttributeValue("homer", "Mmmm, donut...");
        assertEquals("Mmmm, donut...", simpsons.getAttributeValue("homer"));
        assertRecorded(recorder, changed("/simpsons#homer", "doh", "Mmmm, donut..."));
    }

    private void assertNoAttributes(Resource r) {
        Collection<String> attrs = r.getAttributes();
        assertNotNull(attrs);
        assertTrue(attrs.isEmpty());
    }

    private void assertNoSubResources(Resource r) {
        Collection<Resource> kids = r.getChildren();
        assertNotNull(kids);
        assertTrue(kids.isEmpty());
    }
}
