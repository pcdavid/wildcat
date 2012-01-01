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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;

public class PathTests {
    protected Path pathFromString(String path) {
        return new SimplePath(path);
    }
    
    @Test(expected = NullPointerException.class)
    public void nullPath() throws MalformedPathException {
        pathFromString(null);
    }

    @Test @Ignore
    public void invalidPathCreation() {
        String[] paths = {
                "",
                "//",
                "//////",
                "ùoskdé=)à'mù",
                "smldaz!:sqsdqs", "://",
                ":/", "domain://foo/bar",
                "#attr/before/resources",
                "//double/slashes",
                "double//slashes/in/the/middle",
                "***",
                "/***",
                "***/foo",
                "#attribute#ofattribute",
                "/foo/*/#bar",
                "42",
                "/42",
                "/42/43#44",
                "#42"
        };
        for (String path : paths) {
            try {
                pathFromString(path);
                fail("Invalid path \"" + path + "\" should not be accepted.");
            } catch (MalformedPathException e) {
            }
        }
    }

    @Test
    public void absoluteDefiniteResourcePathsCreation() throws MalformedPathException {
        String[] paths = {
                "/",
                "/foo",
                "/foo/bar",
                "/foo_bar/baz/qux/fred/wilma"
        };
        for (String path : paths) {
            Path p = pathFromString(path);
            assertTrue(p.isAbsolute());
            assertFalse(p.isRelative());
            assertTrue(p.isDefinite());
            assertFalse(p.isPattern());
            assertTrue(p.isResource());
            assertFalse(p.isAttribute());
            assertNull(p.getAttributePart());
            assertEquals(path, p.toString());
        }
    }

    @Test
    public void relativeDefiniteResourcePathsCreation() throws MalformedPathException {
        String[] paths = {
                "foo",
                "foo/bar",
                "foo_bar/baz/qux/fred/wilma"
        };
        for (String path : paths) {
            Path p = pathFromString(path);
            assertTrue(p.isRelative());
            assertFalse(p.isAbsolute());
            assertTrue(p.isDefinite());
            assertFalse(p.isPattern());
            assertTrue(p.isResource());
            assertFalse(p.isAttribute());
            assertNull(p.getAttributePart());
            assertEquals(path, p.toString());
        }
    }

    @Test
    public void absolutePatternResourcePathsCreation() throws MalformedPathException {
        String[] paths = {
                "/*",
                "/**",
                "/*/*/**/**/*",
                "/foo/*",
                "/foo/*/bar",
                "/foo/bar/*",
                "/foo/*/bar/**/baz",
                "/**/foo",
                "/*/foo/bar"
        };
        for (String path : paths) {
            Path p = pathFromString(path);
            assertTrue(p.isAbsolute());
            assertFalse(p.isRelative());
            assertTrue(p.isPattern());
            assertFalse(p.isDefinite());
            assertTrue(p.isResource());
            assertFalse(p.isAttribute());
            assertNull(p.getAttributePart());
            assertEquals(path, p.toString());
        }
    }

    @Test
    public void relativePatternResourcePathsCreation() throws MalformedPathException {
        String[] paths = {
                "*",
                "**",
                "*/*/**/**/*",
                "foo/*",
                "foo/*/bar",
                "foo/bar/*",
                "foo/*/bar/**/baz",
                "**/foo",
                "*/foo/bar"
        };
        for (String path : paths) {
            Path p = pathFromString(path);
            assertTrue(p.isRelative());
            assertFalse(p.isAbsolute());
            assertTrue(p.isPattern());
            assertFalse(p.isDefinite());
            assertTrue(p.isResource());
            assertFalse(p.isAttribute());
            assertNull(p.getAttributePart());
            assertEquals(path, p.toString());
        }
    }

    @Test
    public void absoluteDefiniteAttributePathsCreation() throws MalformedPathException {
        String[] paths = {
                "/foo#bar",
                "/foo/bar#baz",
                "/foo_bar/fred_wilma#arch_stanton"
        };
        for (String path : paths) {
            Path p = pathFromString(path);
            assertTrue(p.isAbsolute());
            assertFalse(p.isRelative());
            assertTrue(p.isDefinite());
            assertFalse(p.isPattern());
            assertTrue(p.isAttribute());
            assertFalse(p.isResource());
            assertNotNull(p.getAttributePart());
            assertEquals(path, p.toString());
        }
    }

    @Test
    public void relativeDefiniteAttributePathsCreation() throws MalformedPathException {
        String[] paths = {
                "#foo",
                "foo#bar",
                "foo/bar#baz",
                "foo_bar/fred_wilma#arch_stanton"
        };
        for (String path : paths) {
            Path p = pathFromString(path);
            assertTrue(p.isRelative());
            assertFalse(p.isAbsolute());
            assertTrue(p.isDefinite());
            assertFalse(p.isPattern());
            assertTrue(p.isAttribute());
            assertFalse(p.isResource());
            assertNotNull(p.getAttributePart());
            assertEquals(path, p.toString());
        }
    }

    @Test
    public void absolutePatternAttributePathsCreation() throws MalformedPathException {
        String[] paths = {
                "/*#*",
                "/**#*",
                "/foo/*#bar",
                "/*/foo#bar",
                "/**/**/*/**#*",
                "/foo/bar#*",
                "/foo/bar_baz#*",
                "/foo/*#bar_baz"
        };
        for (String path : paths) {
            Path p = pathFromString(path);
            assertTrue(p.isAbsolute());
            assertFalse(p.isRelative());
            assertTrue(p.isPattern());
            assertFalse(p.isDefinite());
            assertTrue(p.isAttribute());
            assertFalse(p.isResource());
            assertNotNull(p.getAttributePart());
            assertEquals(path, p.toString());
        }
    }

    @Test
    public void relativePatternAttributePathsCreation() throws MalformedPathException {
        String[] paths = {
                "*#*",
                "**#*",
                "foo/*#bar",
                "*/foo#bar",
                "**/**/*/**#*",
                "foo/bar#*",
                "foo/bar_baz#*",
                "foo/*#bar_baz"
        };
        for (String path : paths) {
            Path p = pathFromString(path);
            assertTrue(p.isRelative());
            assertFalse(p.isAbsolute());
            assertTrue(p.isPattern());
            assertFalse(p.isDefinite());
            assertTrue(p.isAttribute());
            assertFalse(p.isResource());
            assertNotNull(p.getAttributePart());
            assertEquals(path, p.toString());
        }
    }

    @Test
    public void validAppends() throws MalformedPathException {
        String[][] paths = {
                /* Base, Suffix, Expected result */
                { "/", "foo", "/foo" },
                { "foo", "bar", "foo/bar" },
                { "/foo", "#bar", "/foo#bar" },
                { "foo/**", "*", "foo/**/*" },
                { "*", "*", "*/*" },
                { "*", "#*", "*#*" }
        };
        for (String[] params : paths) {
            Path base = pathFromString(params[0]);
            Path suffix = pathFromString(params[1]);
            Path result = pathFromString(params[2]);
            assertEquals(result, base.append(suffix));
            assertEquals(result, base.append(params[1]));
        }
    }

    @Test
    public void invalidAppends() throws MalformedPathException {
        String[][] paths = {
                /* Base, Suffix */
                { "/", "/foo" },
                { "/foo#bar", "foo" },
                { "/foo#bar", "#foo" },
                { "/foo", "/bar" },
                { "/foo", "/*" },
                { "/", "#bar" }
        };
        for (String[] params : paths) {
            Path base = pathFromString(params[0]);
            Path suffix = pathFromString(params[1]);
            try {
                base.append(suffix);
                fail("Invalid append attempt should be rejected (base: " + base
                        + ", suffix: " + suffix + ".");
            } catch (MalformedPathException mpe) {
                /* Expected */
            }
            try {
                base.append(params[1]);
                fail("Invalid append attempt should be rejected (base: " + base
                        + ", suffix: " + suffix + ".");
            } catch (MalformedPathException mpe) {
                /* Expected */
            }
        }
    }
    
    @Test
    public void pathEndsWithItself() throws MalformedPathException {
        String[] paths =  { "/", "/foo", "/foo/bar", "/foo/bar#baz",
                "foo", "foo/bar", "foo#bar", "foo/bar#baz"
        };
        for (String path : paths) {
            Path p = pathFromString(path);
            assertTrue(p.endsWith(p));            
        }
    }
    
    @Test
    public void subPathFromAbsolute() throws MalformedPathException {
        Object[][] fixture = {
                /* path, start, finish, expected */ 
                { "/", 0, 1, "/"},
                { "/foo/bar#baz", 0, 1, "/"},
                { "/foo/bar#baz", 0, 2, "/foo"},
                { "/foo/bar#baz", 0, 3, "/foo/bar"},
                { "/foo/bar#baz", 0, 4, "/foo/bar#baz"},
                { "/foo/bar#baz", 1, 2, "foo"},
                { "/foo/bar#baz", 1, 3, "foo/bar"},
                { "/foo/bar#baz", 1, 4, "foo/bar#baz"},
                { "/foo/bar#baz", 2, 3, "bar"},
                { "/foo/bar#baz", 2, 4, "bar#baz"},
                { "/foo/bar#baz", 3, 4, "#baz"},
        };
        for (Object[] params : fixture) {
            Path path = pathFromString((String) params[0]);
            Path expected = pathFromString((String) params[3]);
            Path result = path.subPath((Integer) params[1], (Integer) params[2]);
            assertEquals(expected, result);
        }
    }
    
    @Test
    public void subPathFromRelative() throws MalformedPathException {
        Object[][] fixture = {
                /* path, start, finish, expected */ 
                { "foo/bar#baz", 0, 1, null},
                { "foo/bar#baz", 0, 2, "foo"},
                { "foo/bar#baz", 1, 2, "foo"},
                { "foo/bar#baz", 1, 3, "foo/bar"},
                { "foo/bar#baz", 1, 4, "foo/bar#baz"},
                { "foo/bar#baz", 2, 3, "bar"},
                { "foo/bar#baz", 2, 4, "bar#baz"},
                { "foo/bar#baz", 3, 4, "#baz"},
                { "#bar", 0, 1, null },
                { "#bar", 1, 2, "#bar" },
        };
        for (Object[] params : fixture) {
            Path path = pathFromString((String) params[0]);
            Path expected = params[3] == null ? null : pathFromString((String) params[3]);
            Path result = path.subPath((Integer) params[1], (Integer) params[2]);
            assertEquals(expected, result);
        }
    }
    
    @Test
    public void relativeTo() throws MalformedPathException {
        String[][] fixture = {
                /* path, base, expected */
                { "/cartoons/simpsons/bart", "/cartoons", "simpsons/bart" },
                { "cartoons/simpsons/bart", "cartoons", "simpsons/bart" },
                { "cartoons/simpsons#bart", "cartoons", "simpsons#bart" },
                { "cartoons/simpsons/*", "cartoons", "simpsons/*" },
                { "cartoons/simpsons#*", "cartoons", "simpsons#*" },
        };
        for (String[] params : fixture) {
            Path path = pathFromString(params[0]);
            Path base = pathFromString(params[1]);
            Path expected = pathFromString(params[2]);
            assertEquals(expected, path.relativeTo(base));
        }
    }
    
    @Test
    public void invalidRelativeTo() throws MalformedPathException {
        String[][] fixture = {
                /* path, base */
                { "foo", "foo" },
                { "foo/bar", "baz/qux"},
                { "/foo", "/bar" }
        };
        for (String[] params : fixture) {
            Path path = pathFromString(params[0]);
            Path base = pathFromString(params[1]);
            try {
                path.relativeTo(base);
                fail("Path#relativeTo() should have thrown a MalformedPathException.");
            } catch (MalformedPathException mpe) {
                // Success
            }
        }
    }
}
