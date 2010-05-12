package org.dozer.loader.xml;

import org.w3c.dom.Element;

/**
 * @author Dmitry Buzdin
 */
public class SimpleElementReader implements ElementReader {

  public String getAttribute(Element element, String attribute) {
    return element.getAttribute(attribute).trim();
  }

  public String getNodeValue(Element element) {
    String nodeValue = element.getFirstChild().getNodeValue();
    if (nodeValue != null) {
      return nodeValue.trim();
    } else {
      return "";
    }
  }

}
