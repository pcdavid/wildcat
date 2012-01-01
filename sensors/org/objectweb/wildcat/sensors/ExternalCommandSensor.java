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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.objectweb.wildcat.providers.Sampler;

/**
 * Helper class to ease the creation of sensors taking their information from the textual
 * output of external commands.
 * 
 * @see XMLCommandSensor
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public abstract class ExternalCommandSensor implements Sampler {
    protected ExternalCommandSensor(String[] cmdarray, String[] envp, File dir) {
        assert (cmdarray != null && cmdarray.length > 0);
        this.command = cmdarray;
        this.env = envp;
        this.wdir = dir;
    }

    protected InputStream getCommandOutput() throws IOException {
        Process proc = Runtime.getRuntime().exec(command, env, wdir);
        return proc.getInputStream();
    }

    protected String[] getLines() throws IOException {
        ArrayList<String> lines = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                getCommandOutput()));
        try {
            String line = null;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            return lines.toArray(new String[lines.size()]);
        } finally {
            reader.close();
        }
    }

    protected String[] command;

    protected String[] env;

    protected File wdir;
}