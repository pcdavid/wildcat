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
package org.objectweb.wildcat.dependencies;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.wildcat.dependencies.DependencyGraph;
import org.objectweb.wildcat.dependencies.DependencyGraphImpl;


/**
 * @author Pierre-Charles David <pcdavid@gmail.com>
 *
 */
public class DependencyGraphTests {
    private DependencyGraph<String> graph;
    
    @Before
    public void setUp() {
        graph = new DependencyGraphImpl<String>();
    }
    
    @Test
    public void addNoCycle() {
        graph.addDependency("foo", "bar");
        assertTrue(graph.getAllDependingOn("bar").contains("foo"));
        assertFalse(graph.getAllDependingOn("foo").contains("bar"));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void addDirectCycle() {
        graph.addDependency("foo", "bar");
        graph.addDependency("bar", "foo");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void addIndirectCycle() {
        graph.addDependency("foo", "bar");
        graph.addDependency("bar", "baz");
        graph.addDependency("baz", "foo");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void addIndirectCycle2() {
        String[][] deps = new String[][] {
                /* X depends on Y */
                { "x2", "x1" },
                { "x3", "x1" },
                { "x4", "x1" },
                { "x5", "x3" },
                { "x5", "x4" },
                { "x6", "x4" },
                { "x7", "x6" }
        };
        for (String[] dep : deps) {
            graph.addDependency(dep[0], dep[1]);
        }
        graph.addDependency("x1", "x7");
    }
}
