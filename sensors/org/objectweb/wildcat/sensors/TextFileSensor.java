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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.Format;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.wildcat.providers.Sampler;

/**
 * Helper class to ease the creation of sensors taking their informations from
 * simple text files.
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public abstract class TextFileSensor implements Sampler {
    protected TextFileSensor(String fileName) {
        assert (fileName != null);
        this.fileName = fileName;
    }

    /**
     * This default implementation of <code>sample()</code> simply creates an
     * <code>ArrayList</code> to store the samples and then calls
     * <code>fillSamples()</code>.
     */
    public Map<String, Object> sample() {
        Map<String, Object> samples = new HashMap<String, Object>();
        fillSamples(samples);
        return samples;
    }

    protected void fillSamples(Map<String, Object> samples) {
        fillSamples(samples, getLines());
    }

    protected abstract void fillSamples(Map<String, Object> samples, String[] lines);

    protected String[] getLines() {
        ArrayList<String> lines = new ArrayList<String>();
        try {
            LineNumberReader reader = null;
            try {
                reader = new LineNumberReader(new FileReader(fileName));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            } catch (FileNotFoundException e) {
                throw new IOException("Unable to open " + fileName + " file: " + e.getMessage());
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        } catch (IOException e) {
            lines.clear();
        }
        return lines.toArray(new String[lines.size()]);
    }

    private String fileName;

    /**
     * @author Pierre-Charles David <pcdavid@gmail.com>
     */
    protected static final class LineFormat {
        public LineFormat(String property, String format) {
            this(property, new MessageFormat(format));
        }

        public LineFormat(String property, Format format) {
            this.property = property;
            this.format = format;
        }

        public Format getFormat() {
            return format;
        }

        public String getProperty() {
            return property;
        }

        private Format format;
        private String property;
    }
}