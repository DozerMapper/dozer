package org.dozer.loader.xml;

import org.dozer.MappingException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 *
 * Factory class for providing valid XML parsers. Dozer uses DOM approach for XML processing.
 *
 * @author dmitry.buzdin
 */
public final class XMLParserFactory {

  private static final String SCHEMA_FEATURE = "http://apache.org/xml/features/validation/schema";

  private static final XMLParserFactory instance = new XMLParserFactory();

  public static XMLParserFactory getInstance() {
    return instance;
  }

  private XMLParserFactory() {
  }

  public DocumentBuilder createParser() {
    DocumentBuilderFactory factory = createDocumentBuilderFactory();
    try {
      return createDocumentBuilder(factory);
    } catch (ParserConfigurationException e) {
      throw new MappingException("Failed to create XML Parser !", e);
    }
  }

  /**
   * Create a JAXP DocumentBuilderFactory that this bean definition reader will use for parsing XML documents. Can be
   * overridden in subclasses, adding further initialization of the factory.
   *
   * @return the JAXP DocumentBuilderFactory
   * @throws javax.xml.parsers.ParserConfigurationException
   *          if thrown by JAXP methods
   */
  private DocumentBuilderFactory createDocumentBuilderFactory() {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(true);
    factory.setNamespaceAware(true);
    factory.setIgnoringElementContentWhitespace(true);
    factory.setAttribute(SCHEMA_FEATURE, true); // For Xerces implementation
    return factory;
  }

  /**
   * Create a JAXP DocumentBuilder that this bean definition reader will use for parsing XML documents. Can be
   * overridden in subclasses, adding further initialization of the builder.
   *
   * @param factory the JAXP DocumentBuilderFactory that the DocumentBuilder should be created with
   * @return the JAXP DocumentBuilder
   * @throws javax.xml.parsers.ParserConfigurationException
   *          if thrown by JAXP methods
   */
  private DocumentBuilder createDocumentBuilder(DocumentBuilderFactory factory) throws ParserConfigurationException {
    DocumentBuilder docBuilder = factory.newDocumentBuilder();
    docBuilder.setErrorHandler(new DozerDefaultHandler());
    docBuilder.setEntityResolver(new DozerResolver());
    return docBuilder;
  }

  private static class DozerDefaultHandler extends DefaultHandler {
    private static final Logger log = LoggerFactory.getLogger(DozerDefaultHandler.class);

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      log.debug("tag: {}", qName);
    }

    @Override
    public void warning(SAXParseException e) throws SAXException {
      // you can choose not to handle it
      throw new SAXException(getMessage("Warning", e));
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
      throw new SAXException(getMessage("Error", e));
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
      throw new SAXException(getMessage("Fatal Error", e));
    }

    private String getMessage(String level, SAXParseException e) {
      return ("Parsing " + level + "\n" + "Line:    " + e.getLineNumber() + "\n" + "URI:     " + e.getSystemId() + "\n"
              + "Message: " + e.getMessage());
    }
  }


}
