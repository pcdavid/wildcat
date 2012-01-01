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
import static org.objectweb.wildcat.TestUtils.*;
import static org.objectweb.wildcat.Context.createPath;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.wildcat.InvalidMountPointException;
import org.objectweb.wildcat.providers.BasicContextProvider;
import org.objectweb.wildcat.providers.OverlayContextProvider;

public class OverlayContextProviderTests {
    private EventRecorder recorder;
    private BasicContextProvider defaultProvider;
    private OverlayContextProvider overlay;

    @Before
    public void setUp() {
        recorder = new EventRecorder();
        defaultProvider = new BasicContextProvider();
        overlay = new OverlayContextProvider(defaultProvider);
        overlay.setEventListener(recorder);
    }

    @Test(expected = IllegalStateException.class)
    public void unmounUnmountedOverlay() {
        overlay.unmounted();
    }

    @Test(expected = IllegalStateException.class)
    public void mountMountedOverlay() {
        overlay.mounted(createPath("/"));
        overlay.mounted(createPath("/"));
    }

    @Test
    public void rootOverlayWithDefaultProvider() throws InvalidMountPointException {
        overlay.mounted(createPath("/"));
        assertEquals(createPath("/"), overlay.getPath());
        assertEquals(createPath("/"), defaultProvider.getPath());
        assertRecorded(recorder, added("/"));
        recorder.clear();
        defaultProvider.createResource(createPath("fred"));
        assertRecorded(recorder, added("/fred"));
        recorder.clear();
        defaultProvider.createResource(createPath("foo/bar/baz"));
        assertRecorded(recorder, added("/foo"), added("/foo/bar"), added("/foo/bar/baz"));
        for (String query : new String[] { "foo", "foo/*", "foo/foo", "foo/bar", "foo#*",
                "foo#bar", "foo/bar/*", "foo/bar/baz", "foo/bar#*", "foo/bar#baz",
                "foo/bar/qux" }) {
            assertContentEquals(defaultProvider.lookup(createPath(query)), overlay
                    .lookup(createPath(query)));
        }
    }

    @Test
    public void mountInternalProvider() throws InvalidMountPointException {
        overlay.mounted(createPath("/"));
        recorder.clear();
        BasicContextProvider internal = new BasicContextProvider();
        overlay.mount(createPath("cartoons"), internal);
        recorder.recorded(added("/cartoons"));
    }

    @Test(expected = InvalidMountPointException.class)
    public void mountInternalProviderOnExistingResource()
            throws InvalidMountPointException {
        overlay.mounted(createPath("/"));
        defaultProvider.createResource(createPath("cartoons"));
        BasicContextProvider internal = new BasicContextProvider();
        overlay.mount(createPath("cartoons"), internal);
    }

    @Test
    public void lookupOverlayedResource() throws InvalidMountPointException {
        overlay.mounted(createPath("/"));
        BasicContextProvider internal = new BasicContextProvider();
        overlay.mount(createPath("cartoons"), internal);
        recorder.clear();
        defaultProvider.createResource(createPath("movies"));
        defaultProvider.createResource(createPath("movies/pitch_black"));
        assertRecorded(recorder, added("/movies"), added("/movies/pitch_black"));
        recorder.clear();
        internal.createResource(createPath("simpsons"));
        internal.createResource(createPath("simpsons/homer"));
        internal.createResource(createPath("simpsons/marge"));
        assertRecorded(recorder, added("/cartoons/simpsons"),
                added("/cartoons/simpsons/homer"), added("/cartoons/simpsons/marge"));
        //
        assertLookup(overlay, "cartoons/*", "/cartoons/simpsons");
        assertLookup(overlay, "movies/*", "/movies/pitch_black");
        assertLookup(overlay, "*", "/cartoons", "/movies");
        assertLookup(overlay, "*/*", "/cartoons/simpsons", "/movies/pitch_black");
    }

    @Test
    public void lookupOverlayedAttributes() throws InvalidMountPointException {
        overlay.mounted(createPath("/"));
        BasicContextProvider internal = new BasicContextProvider();
        overlay.mount(createPath("cartoons"), internal);
        defaultProvider.createResource(createPath("movies"));
        defaultProvider.createResource(createPath("movies/pitch_black"));
        defaultProvider.createAttribute(createPath("movies/pitch_black#director"),
                "David Twohy");
        defaultProvider.createAttribute(createPath("movies/pitch_black#rating"), "great");
        defaultProvider.createResource(createPath("movies/below"));
        defaultProvider.createAttribute(createPath("movies/below#rating"), "great too");
        internal.createResource(createPath("simpsons"));
        internal.createResource(createPath("simpsons/homer"));
        internal.createAttribute(createPath("simpsons/homer#moto"), "doh");
        internal.createResource(createPath("simpsons/bart"));
        internal.createAttribute(createPath("simpsons/bart#moto"), "eat my shorts");
        assertLookup(overlay, "movies#*");
        assertLookup(overlay, "movies/pitch_black#*", "/movies/pitch_black#rating",
                "/movies/pitch_black#director");
        assertLookup(overlay, "movies/*#*", "/movies/pitch_black#rating",
                "/movies/pitch_black#director", "/movies/below#rating");
        assertLookup(overlay, "movies/*#rating", "/movies/pitch_black#rating",
                "/movies/below#rating");
        assertLookup(overlay, "*/*#rating", "/movies/pitch_black#rating",
                "/movies/below#rating");
        assertLookup(overlay, "*/*#*", "/movies/pitch_black#rating",
                "/movies/pitch_black#director", "/movies/below#rating");
        assertLookup(overlay, "cartoons#*");
        assertLookup(overlay, "cartoons/simpsons#*");
        assertLookup(overlay, "cartoons/simpsons/marge#*");
        assertLookup(overlay, "cartoons/simpsons/homer#*",
                "/cartoons/simpsons/homer#moto");
        assertLookup(overlay, "cartoons/simpsons/bart#*", "/cartoons/simpsons/bart#moto");
        assertLookup(overlay, "cartoons/simpsons/*#*", "/cartoons/simpsons/homer#moto",
                "/cartoons/simpsons/bart#moto");
        assertLookup(overlay, "cartoons/simpsons/*#moto",
                "/cartoons/simpsons/homer#moto", "/cartoons/simpsons/bart#moto");
        assertLookup(overlay, "cartoons/simpsons/*#quote");
        assertLookup(overlay, "*/simpsons/*#moto", "/cartoons/simpsons/homer#moto",
                "/cartoons/simpsons/bart#moto");
        assertLookup(overlay, "*/*/*#moto", "/cartoons/simpsons/homer#moto",
                "/cartoons/simpsons/bart#moto");
        assertLookup(overlay, "*/*/*#*", "/cartoons/simpsons/homer#moto",
                "/cartoons/simpsons/bart#moto");
    }

    @Test
    public void lookupOverlayedAttributeValues() throws InvalidMountPointException {
        overlay.mounted(createPath("/"));
        BasicContextProvider internal = new BasicContextProvider();
        overlay.mount(createPath("cartoons"), internal);
        defaultProvider.createResource(createPath("movies"));
        defaultProvider.createResource(createPath("movies/pitch_black"));
        defaultProvider.createAttribute(createPath("movies/pitch_black#director"),
                "David Twohy");
        defaultProvider.createAttribute(createPath("movies/pitch_black#rating"), "great");
        defaultProvider.createResource(createPath("movies/below"));
        defaultProvider.createAttribute(createPath("movies/below#rating"), "great too");
        internal.createResource(createPath("simpsons"));
        internal.createResource(createPath("simpsons/homer"));
        internal.createAttribute(createPath("simpsons/homer#moto"), "doh");
        internal.createResource(createPath("simpsons/bart"));
        internal.createAttribute(createPath("simpsons/bart#moto"), "eat my shorts");
        assertEquals("great", overlay.lookupAttribute(createPath("movies/pitch_black#rating")));
        assertNull(overlay.lookupAttribute(createPath("movies/pitch_black#year")));
        assertNull(overlay.lookupAttribute(createPath("movies/fight_club#rating")));
        assertEquals("doh", overlay.lookupAttribute(createPath("cartoons/simpsons/homer#moto")));
        assertEquals("eat my shorts", overlay.lookupAttribute(createPath("cartoons/simpsons/bart#moto")));
    }
}
