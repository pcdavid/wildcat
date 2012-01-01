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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.objectweb.wildcat.ContextProvider;
import org.objectweb.wildcat.Path;
import org.objectweb.wildcat.dependencies.DependencyGraph;
import org.objectweb.wildcat.events.EventListener;
import org.objectweb.wildcat.events.PathEvent;

/**
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class FileSystemProvider implements ContextProvider {
    private Path path;
    private File rootFolder;
    private DependencyGraph<Path> dependencyGraph;
    private EventListener listener;
    private static final List<String> SUPPORTED_ATTRIBUTES = new ArrayList<String>() {
        {
            add("size");
            add("last_modified");
            add("readable");
            add("writable");
            add("type");
        }
    };

    public FileSystemProvider(File rootFolder) {
        this.rootFolder = rootFolder;
    }
    
    /* (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#getDependencyGraph()
     */
    public DependencyGraph<Path> getDependencyGraph() {
        return dependencyGraph;
    }
    
    /* (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#setDependencyGraph(org.objectweb.wildcat.dependencies.DependencyGraph)
     */
    public void setDependencyGraph(DependencyGraph<Path> dg) {
        this.dependencyGraph = dg;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.ContextProvider#getEventListener()
     */
    public EventListener getEventListener() {
        return listener;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.ContextProvider#getPath()
     */
    public Path getPath() {
        return path;
    }
    
    /* (non-Javadoc)
     * @see org.objectweb.wildcat.ContextProvider#update(org.objectweb.wildcat.Path, org.objectweb.wildcat.events.PathEvent)
     */
    public void update(Path path, PathEvent cause) {
        // Ignore
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.ContextProvider#lookup(org.objectweb.wildcat.Path)
     */
    public Collection<Path> lookup(Path query) {
        Collection<File> current = new ArrayList<File>();
        current.add(rootFolder);
        for (String step : query.getResourcesPart()) {
            Collection<File> next = new ArrayList<File>();
            for (File f : current) {
                File[] matches = f.listFiles();
                for (File file : matches) {
                    if ("*".equals(step) || file.getName().equals(step)) {
                        next.add(file);
                    }
                }
            }
            current = next;
        }
        Collection<Path> result = new ArrayList<Path>();
        for (File f : current) {
            Path fullPath = null;
            try {
                fullPath = path.append(f.getCanonicalPath().substring(1));
            } catch (IOException e) {
                break;
            }
            if (query.isResource()) {
                result.add(fullPath);
            } else if ("*".equals(query.getAttributePart())) {
                for (String attr : SUPPORTED_ATTRIBUTES) {
                    result.add(fullPath.append("#" + attr));
                }
            } else if (SUPPORTED_ATTRIBUTES.contains(query.getAttributePart())) {
                result.add(fullPath.append("#" + query.getAttributePart()));
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.ContextProvider#lookupAttribute(org.objectweb.wildcat.Path)
     */
    public Object lookupAttribute(Path attribute) {
        Collection<Path> entries = lookup(attribute.getParent());
        if (entries.isEmpty()) {
            return null;
        } else {
            Path entryPath = entries.iterator().next().relativeTo(path);
            try {
                File f = new File(rootFolder.getCanonicalPath()
                        + System.getProperty("file.separator") + entryPath.toString());
                String attr = attribute.getAttributePart();
                if ("size".equals(attr)) {
                    return f.length();
                } else if ("last_modified".equals(attr)) {
                    return f.lastModified();
                } else if ("readable".equals(attr)) {
                    return f.canRead();
                } else if ("writable".equals(attr)) {
                    return f.canWrite();
                } else if ("type".equals(attr)) {
                    return f.isDirectory() ? "directory" : "file";
                } else {
                    return null;
                }
            } catch (IOException e) {
                return null;
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.ContextProvider#mounted(org.objectweb.wildcat.Path)
     */
    public void mounted(Path path) {
        if (this.path == null) {
            this.path = path;
        } else {
            throw new IllegalStateException("FS provider already mounted.");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.ContextProvider#setEventListener(org.objectweb.wildcat.events.EventListener)
     */
    public void setEventListener(EventListener listener) {
        this.listener = listener;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.ContextProvider#unmounted()
     */
    public void unmounted() {
        if (this.path != null) {
            this.path = null;
        } else {
            throw new IllegalStateException("FS provider not mounted.");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String str = "Filesystem provider for " + rootFolder;
        if (path == null) {
            return str + ", unmounted.";
        } else {
            return str + ", mounted at " + path + ".";
        }
    }
}
