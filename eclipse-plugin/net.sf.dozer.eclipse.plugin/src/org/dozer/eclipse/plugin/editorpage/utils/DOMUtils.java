/**
 * Copyright 2005-2013 Dozer Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dozer.eclipse.plugin.editorpage.utils;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DOMUtils {

//* * * * * Taken from Angelo Zerr's DOM/SSE Example
	
	public static final Element[] EMPTY_ELEMENT = new Element[0];

	/**
	 * Return element child of <code>parent</code> element with the name
	 * <code>elementName</code>.
	 * 
	 * @param parent
	 * @param elementName
	 * @return
	 */
	public static Element getElement(Element parent, String elementName) {
		if (parent == null)
			return null;
		NodeList nodes = parent.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if (element.getNodeName().equals(elementName))
					return element;
			}
		}
		return null;
	}

	/**
	 * Return the children of <code>parent</code> element no matter
	 * what element name they have.
	 * 
	 * @param parent
	 * @param elementNames
	 * @return
	 */
	public static Element[] getElements(Element parent) {
		return getElements(parent, new String[] {});
	}
	
	/**
	 * Return the children of <code>parent</code> element with have the name
	 * <code>elementName</code>.
	 * 
	 * @param parent
	 * @param elementNames
	 * @return
	 */
	public static Element[] getElements(Element parent,
			String elementName) {
		
		return getElements(parent, new String[] {elementName});
	}
	
	/**
	 * Return the children of <code>parent</code> element with have names
	 * <code>elementNames</code>.
	 * 
	 * @param parent
	 * @param elementNames
	 * @return
	 */
	public static Element[] getElements(Element parent,
			String[] elementNames) {
		if (parent == null)
			return EMPTY_ELEMENT;

		List<Element> elements = new ArrayList<Element>();
		List<String> elementNamesList = Arrays.asList(elementNames);
		NodeList nodes = parent.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if (elementNames.length == 0 || elementNamesList.contains(element.getNodeName())) {
					elements.add(element);
				}
			}
		}
		return elements.toArray(EMPTY_ELEMENT);
	}

	/**
	 * Replace node name of <code>element</code> with new name
	 * <code>newNodeName</code>.
	 * 
	 * @param element
	 * @param newNodeName
	 * @return
	 */
	public static Element replaceNodeName(Element element, String newNodeName) {
		Document document = element.getOwnerDocument();
		// Create an element with the new name
		Element element2 = document.createElement(newNodeName);

		// Copy the attributes to the new element
		NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			Attr attr2 = (Attr) document.importNode(attrs.item(i), true);
			element2.getAttributes().setNamedItem(attr2);
		}

		// Move all the children
		while (element.hasChildNodes()) {
			element2.appendChild(element.getFirstChild());
		}

		// Replace the old node with the new node
		element.getParentNode().replaceChild(element2, element);
		return element2;

	}

	/**
	 * Remove list of <code>elements</code> from DOM Document.
	 * 
	 * @param elements
	 */
	public static void removeElements(List<Element> elements) {
		for (Element element : elements) {
			Node parent = element.getParentNode();
			parent.removeChild(element);
		}
	}

	/**
	 * Returns content of DOm Document.
	 * 
	 * @param document
	 * @return
	 */
	public static String getString(Document document) {
		StringBuffer xml = new StringBuffer("");
		parseNode(xml, document.getDocumentElement());
		return xml.toString();
	}

	private static void parseNode(StringBuffer xml, Node node) {
		switch (node.getNodeType()) {
		case Node.ELEMENT_NODE:
			parseElement(xml, (Element) node);
			break;
		case Node.TEXT_NODE:
			parseText(xml, (Text) node);
			break;
		}
	}

	private static void parseText(StringBuffer xml, Text node) {
		xml.append(node.getNodeValue());

	}

	private static void parseElement(StringBuffer xml, Element node) {
		xml.append("<");
		xml.append(node.getNodeName());

		// Parse attributes
		NamedNodeMap attributes = node.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Attr attr = (Attr) attributes.item(i);
			xml.append(" ");
			xml.append(attr.getName());
			xml.append("=\"");
			xml.append(attr.getValue());
			xml.append("\"");
		}

		if (!node.hasChildNodes()) {
			xml.append("/>");
		} else {
			xml.append(">");
			// Parse children
			NodeList children = node.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				parseNode(xml, children.item(i));
			}
			xml.append("</");
			xml.append(node.getNodeName());
			xml.append(">");
		}
	}	
	
//* * * * * *
	
	public static String nodeNameToPropertyName(String nodeName) {
		int l = nodeName.length();
		String property = "";
		boolean nextUpper = false;
		for (int i=0; i<l; i++) {
			char c = nodeName.charAt(i);
			//not an alpha char
			if (!Character.isLetter(c)) {
				//skip and next char will get uppered
				nextUpper = true;
			} else {
				String s = String.valueOf(c);
				if (nextUpper) {
					nextUpper = false;
					s = s.toUpperCase();
				}
					
				property += s;
			}
		}
		return property;
	}
	
	public static String propertyNameToNodeName(String propertyName) {
		int l = propertyName.length();
		String nodeName = "";
		for (int i=0; i<l; i++) {
			char c = propertyName.charAt(i);
			if (Character.isUpperCase(c)) {
				nodeName += "-";
			}
		
			nodeName += String.valueOf(c).toLowerCase();
		}
		return nodeName;
	}
	
}
