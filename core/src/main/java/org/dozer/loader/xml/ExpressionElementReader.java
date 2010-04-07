package org.dozer.loader.xml;

import org.w3c.dom.Element;

/**
 * @author Dmitry Buzdin
 */
public class ExpressionElementReader extends SimpleElementReader {

  private ELEngine elEngine;

  public ExpressionElementReader(ELEngine elEngine) {
    this.elEngine = elEngine;
  }

  @Override
  public String getAttribute(Element element, String attribute) {
    String expression = super.getAttribute(element, attribute);
    return elEngine.resolve(expression);
  }

  @Override
  public String getNodeValue(Element element) {
    String expression = super.getNodeValue(element);
    return elEngine.resolve(expression);
  }

}
