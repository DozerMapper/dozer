package org.dozer.loader.xml;

import org.w3c.dom.Element;

/**
 * @author Dmitry Buzdin
 */
public interface ElementReader {

  String getAttribute(Element element, String attribute);

  String getNodeValue(Element element);

}
