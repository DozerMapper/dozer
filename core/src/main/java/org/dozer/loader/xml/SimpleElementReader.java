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
    return element.getFirstChild().getNodeValue().trim();
  }

}
