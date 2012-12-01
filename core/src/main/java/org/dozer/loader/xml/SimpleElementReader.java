package org.dozer.loader.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Dmitry Buzdin
 */
public class SimpleElementReader implements ElementReader {

  public String getAttribute(Element element, String attribute) {
    return element.getAttribute(attribute).trim();
  }

  public String getNodeValue(Element element) {
    Node child = element.getFirstChild();
    if (child == null) {
      return "";
    }
    String nodeValue = child.getNodeValue();
    if (nodeValue != null) {
      return nodeValue.trim();
    } else {
      return "";
    }
  }

}
