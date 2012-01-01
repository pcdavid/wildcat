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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.objectweb.wildcat.providers.Sampler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class XMLCommandSensor implements Sampler {
	public XMLCommandSensor(Element config) throws ParserConfigurationException {
		List<Element> params = getChildrenByName(config, "parameter");
		this.command = new String[params.size() + 1];
		this.command[0] = getChildrenByName(config, "command").get(0)
				.getTextContent();
		int i = 1;
        for (Element param : params) {
			this.command[i++] = param.getTextContent();
		}
		this.env = new String[0];
		this.wdir = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		builder = factory.newDocumentBuilder();
	}

	public Map<String, Object> sample() {
		try {
			return readSamples(builder.parse(getCommandOutput()));
		} catch (Exception e) {
			return new HashMap<String, Object>();
		}
	}

	protected InputStream getCommandOutput() throws IOException {
		Process proc = Runtime.getRuntime().exec(command, env, wdir);
		return new BufferedInputStream(proc.getInputStream());
	}

	private Map<String, Object> readSamples(Document doc) {
		Element root = doc.getDocumentElement();
		assert ("sample-set".equals(root.getNodeName()));
        Map<String, Object> samples = new HashMap<String, Object>();
		List sampleNodes = getChildrenByName(root, "sample");
		for (Iterator iter = sampleNodes.iterator(); iter.hasNext();) {
			Element sampleElt = (Element) iter.next();
			String name = sampleElt.getAttribute("name");
			addSample(name, sampleElt, samples);
		}
		return samples;
	}

	private void addSample(String name, Element elt, Map<String, Object> samples) {
		String valueType = elt.getAttribute("type");
		String valueText = elt.getTextContent();
		if (valueType.equals("string")) {
			samples.put(name, valueText);
		} else if (valueType.equals("integer")) {
			samples.put(name, new Integer(valueText));
		} else if (valueType.equals("boolean")) {
			samples.put(name, Boolean.valueOf(valueText));
		} else if (valueType.equals("float")) {
			samples.put(name, new Double(valueText));
		} else {
			throw new RuntimeException("Invalid XML format.");
		}
	}

	private List<Element> getChildrenByName(Element elt, String name) {
		List<Element> kids = new ArrayList<Element>();
		for (Node n = elt.getFirstChild(); n != null; n = n.getNextSibling()) {
			if (n.getNodeType() == Node.ELEMENT_NODE
					&& n.getNodeName().equals(name)) {
				kids.add((Element) n);
			}
		}
		return kids;
	}

	private String[] command;

	private String[] env;

	private File wdir;

	private DocumentBuilder builder;
}