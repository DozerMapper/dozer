/*
 * Copyright 2005-2010 the original author or authors.
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
package org.dozer.loader.xml;


import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.classmap.MappingDirection;
import org.dozer.classmap.MappingFileData;
import org.dozer.classmap.RelationshipType;
import org.dozer.loader.MappingBuilder;
import org.dozer.util.DozerConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Internal class that parses a raw custom xml mapping file into raw ClassMap objects. Only intended for internal use.
 *
 * @author garsombke.franz
 * @author johnsen.knut-erik
 * @author dmitry.buzdin
 */
public class XMLParser {

  private static final Log log = LogFactory.getLog(XMLParser.class);

  // Common Elements/Attributes
  private static final String WILDCARD = "wildcard";
  private static final String TRIM_STRINGS = "trim-strings";
  private static final String BEAN_FACTORY = "bean-factory";
  private static final String DATE_FORMAT = "date-format";
  private static final String RELATIONSHIP_TYPE = "relationship-type";
  private static final String REMOVE_ORPHANS = "remove-orphans";

  // Parsing Elements
  private static final String CONFIGURATION_ELEMENT = "configuration";
  private static final String STOP_ON_ERRORS_ELEMENT = "stop-on-errors";
  private static final String CUSTOM_CONVERTERS_ELEMENT = "custom-converters";
  private static final String COPY_BY_REFERENCES_ELEMENT = "copy-by-references";
  private static final String COPY_BY_REFERENCE = "copy-by-reference";
  private static final String CONVERTER_ELEMENT = "converter";
  private static final String CLASS_A_ELEMENT = "class-a";
  private static final String CLASS_B_ELEMENT = "class-b";
  private static final String MAPPING_ELEMENT = "mapping";
  private static final String FIELD_ELEMENT = "field";
  private static final String FIELD_EXCLUDE_ELEMENT = "field-exclude";
  private static final String A_ELEMENT = "a";
  private static final String B_ELEMENT = "b";
  private static final String SRC_TYPE_HINT_ELEMENT = "a-hint";
  private static final String DEST_TYPE_HINT_ELEMENT = "b-hint";
  private static final String SRC_TYPE_DEEP_INDEX_HINT_ELEMENT = "a-deep-index-hint";
  private static final String DEST_TYPE_DEEP_INDEX_HINT_ELEMENT = "b-deep-index-hint";
  private static final String ALLOWED_EXCEPTIONS_ELEMENT = "allowed-exceptions";
  private static final String ALLOWED_EXCEPTION_ELEMENT = "exception";

  // Parsing Attributes
  private static final String TYPE_ATTRIBUTE = "type";
  private static final String COPY_BY_REFERENCE_ATTRIBUTE = "copy-by-reference";
  private static final String THE_SET_METHOD_ATTRIBUTE = "set-method";
  private static final String THE_GET_METHOD_ATTRIBUTE = "get-method";
  private static final String STOP_ON_ERRORS_ATTRIBUTE = "stop-on-errors";
  private static final String MAPID_ATTRIBUTE = "map-id";
  private static final String MAP_SET_METHOD_ATTRIBUTE = "map-set-method";
  private static final String MAP_GET_METHOD_ATTRIBUTE = "map-get-method";
  private static final String KEY_ATTRIBUTE = "key";
  private static final String FACTORY_BEANID_ATTRIBUTE = "factory-bean-id";
  private static final String IS_ACCESSIBLE_ATTRIBUTE = "is-accessible";
  private static final String CREATE_METHOD_ATTRIBUTE = "create-method";
  private static final String MAP_NULL_ATTRIBUTE = "map-null";
  private static final String MAP_EMPTY_STRING_ATTRIBUTE = "map-empty-string";
  private static final String CUSTOM_CONVERTER_ATTRIBUTE = "custom-converter";
  private static final String CUSTOM_CONVERTER_ID_ATTRIBUTE = "custom-converter-id";
  private static final String CUSTOM_CONVERTER_PARAM_ATTRIBUTE = "custom-converter-param";

  /**
   * Builds object representation of mappings based on content of Xml document
   *
   * @param document Xml document containing valid mappings definition
   * @return mapping container
   */
  public MappingFileData parse(Document document) {
    MappingBuilder builder = new MappingBuilder();

    Element theRoot = document.getDocumentElement();
    NodeList nl = theRoot.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      Node node = nl.item(i);
      if (node instanceof Element) {
        Element ele = (Element) node;
        if (log.isDebugEnabled()) {
          log.debug("name: " + ele.getNodeName());
        }
        if (CONFIGURATION_ELEMENT.equals(ele.getNodeName())) {
          parseConfiguration(ele, builder);
        } else if (MAPPING_ELEMENT.equals(ele.getNodeName())) {
          parseMapping(ele, builder);
        }
      }
    }

    return builder.build();
  }

  private void parseMapping(Element ele, MappingBuilder builder) {
    MappingBuilder.MappingDefinitionBuilder definitionBuilder = builder.mapping();

    if (StringUtils.isNotEmpty(ele.getAttribute(DATE_FORMAT))) {
      definitionBuilder.dateFormat(ele.getAttribute(DATE_FORMAT));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(MAP_NULL_ATTRIBUTE))) {
      definitionBuilder.mapNull(BooleanUtils.toBoolean(ele.getAttribute(MAP_NULL_ATTRIBUTE)));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(MAP_EMPTY_STRING_ATTRIBUTE))) {
      definitionBuilder.mapEmptyString(BooleanUtils.toBoolean(ele.getAttribute(MAP_EMPTY_STRING_ATTRIBUTE)));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(BEAN_FACTORY))) {
      definitionBuilder.beanFactory(ele.getAttribute(BEAN_FACTORY));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(RELATIONSHIP_TYPE))) {
      String relationshipTypeValue = ele.getAttribute(RELATIONSHIP_TYPE);
      RelationshipType relationshipType = RelationshipType.valueOf(relationshipTypeValue);
      definitionBuilder.relationshipType(relationshipType);
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(WILDCARD))) {
      definitionBuilder.wildcard(Boolean.valueOf(ele.getAttribute(WILDCARD)));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(TRIM_STRINGS))) {
      definitionBuilder.trimStrings(Boolean.valueOf(ele.getAttribute(TRIM_STRINGS)));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(STOP_ON_ERRORS_ATTRIBUTE))) {
      definitionBuilder.stopOnErrors(Boolean.valueOf(ele.getAttribute(STOP_ON_ERRORS_ATTRIBUTE)));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(MAPID_ATTRIBUTE))) {
      definitionBuilder.mapId(ele.getAttribute(MAPID_ATTRIBUTE));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(TYPE_ATTRIBUTE))) {
      String mappingDirection = ele.getAttribute(TYPE_ATTRIBUTE);
      MappingDirection direction = MappingDirection.valueOf(mappingDirection);
      definitionBuilder.type(direction);
    }
    NodeList nl = ele.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      Node node = nl.item(i);
      if (node instanceof Element) {
        Element element = (Element) node;
        debugElement(element);
        if (CLASS_A_ELEMENT.equals(element.getNodeName())) {
          String typeName = element.getFirstChild().getNodeValue().trim();
          MappingBuilder.ClassDefinitionBuilder classBuilder = definitionBuilder.classA(typeName);
          parseClass(element, classBuilder);
        }
        if (CLASS_B_ELEMENT.equals(element.getNodeName())) {
          String typeName = element.getFirstChild().getNodeValue().trim();
          MappingBuilder.ClassDefinitionBuilder classBuilder = definitionBuilder.classB(typeName);
          parseClass(element, classBuilder);
        }
        if (FIELD_ELEMENT.equals(element.getNodeName())) {
          parseGenericFieldMap(element, definitionBuilder);
        } else if (FIELD_EXCLUDE_ELEMENT.equals(element.getNodeName())) {
          parseFieldExcludeMap(element, definitionBuilder);
        }
      }
    }
  }

  private void parseClass(Element element, MappingBuilder.ClassDefinitionBuilder classBuilder) {
    if (StringUtils.isNotEmpty(element.getAttribute(MAP_GET_METHOD_ATTRIBUTE))) {
      classBuilder.mapGetMethod(element.getAttribute(MAP_GET_METHOD_ATTRIBUTE));
    }
    if (StringUtils.isNotEmpty(element.getAttribute(MAP_SET_METHOD_ATTRIBUTE))) {
      classBuilder.mapSetMethod(element.getAttribute(MAP_SET_METHOD_ATTRIBUTE));
    }
    if (StringUtils.isNotEmpty(element.getAttribute(BEAN_FACTORY))) {
      classBuilder.beanFactory(element.getAttribute(BEAN_FACTORY));
    }
    if (StringUtils.isNotEmpty(element.getAttribute(FACTORY_BEANID_ATTRIBUTE))) {
      classBuilder.factoryBeanId(element.getAttribute(FACTORY_BEANID_ATTRIBUTE));
    }
    if (StringUtils.isNotEmpty(element.getAttribute(CREATE_METHOD_ATTRIBUTE))) {
      classBuilder.createMethod(element.getAttribute(CREATE_METHOD_ATTRIBUTE));
    }
    if (StringUtils.isNotEmpty(element.getAttribute(MAP_NULL_ATTRIBUTE))) {
      classBuilder.mapNull(Boolean.valueOf(element.getAttribute(MAP_NULL_ATTRIBUTE)));
    }
    if (StringUtils.isNotEmpty(element.getAttribute(MAP_EMPTY_STRING_ATTRIBUTE))) {
      classBuilder.mapEmptyString(Boolean.valueOf(element.getAttribute(MAP_EMPTY_STRING_ATTRIBUTE)));
    }
  }

  private void parseFieldExcludeMap(Element ele, MappingBuilder.MappingDefinitionBuilder definitionBuilder) {
    MappingBuilder.ExcludeFieldBuilder fieldMapBuilder = definitionBuilder.fieldExclude();
    if (StringUtils.isNotEmpty(ele.getAttribute(TYPE_ATTRIBUTE))) {
      String mappingDirection = ele.getAttribute(TYPE_ATTRIBUTE);
      MappingDirection direction = MappingDirection.valueOf(mappingDirection);
      fieldMapBuilder.type(direction);
    }
    NodeList nodeList = ele.getChildNodes();
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (node instanceof Element) {
        Element element = (Element) node;
        debugElement(element);
        parseFieldElements(element, fieldMapBuilder);
      }
    }
  }

  private void parseFieldElements(Element element, MappingBuilder.FieldContainerBuider fieldMapBuilder) {
    if (A_ELEMENT.equals(element.getNodeName())) {
      String name = element.getFirstChild().getNodeValue().trim();
      String type = element.getAttribute(TYPE_ATTRIBUTE);
      MappingBuilder.FieldBuilder fieldBuilder = fieldMapBuilder.a(name, type);
      parseField(element, fieldBuilder);
    }
    if (B_ELEMENT.equals(element.getNodeName())) {
      String name = element.getFirstChild().getNodeValue().trim();
      String type = element.getAttribute(TYPE_ATTRIBUTE);
      MappingBuilder.FieldBuilder fieldBuilder = fieldMapBuilder.b(name, type);
      parseField(element, fieldBuilder);
    }
  }

  private void parseGenericFieldMap(Element ele, MappingBuilder.MappingDefinitionBuilder definitionBuilder) {
    MappingBuilder.FieldMapBuilder fieldMapBuilder = determineFieldMap(definitionBuilder, ele);

    if (StringUtils.isNotEmpty(ele.getAttribute(COPY_BY_REFERENCE_ATTRIBUTE))) {
      fieldMapBuilder.copyByReference(BooleanUtils.toBoolean(ele.getAttribute(COPY_BY_REFERENCE_ATTRIBUTE)));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(MAPID_ATTRIBUTE))) {
      fieldMapBuilder.mapId(ele.getAttribute(MAPID_ATTRIBUTE));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(TYPE_ATTRIBUTE))) {
      String mappingDirection = ele.getAttribute(TYPE_ATTRIBUTE);
      MappingDirection direction = MappingDirection.valueOf(mappingDirection);
      fieldMapBuilder.type(direction);
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(CUSTOM_CONVERTER_ATTRIBUTE))) {
      fieldMapBuilder.customConverter(ele.getAttribute(CUSTOM_CONVERTER_ATTRIBUTE));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(CUSTOM_CONVERTER_ID_ATTRIBUTE))) {
      fieldMapBuilder.customConverterId(ele.getAttribute(CUSTOM_CONVERTER_ID_ATTRIBUTE));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(CUSTOM_CONVERTER_PARAM_ATTRIBUTE))) {
      fieldMapBuilder.customConverterParam(ele.getAttribute(CUSTOM_CONVERTER_PARAM_ATTRIBUTE));
    }

    parseFieldMap(ele, fieldMapBuilder);
  }

  private MappingBuilder.FieldMapBuilder determineFieldMap(MappingBuilder.MappingDefinitionBuilder definitionBuilder, Element ele) {
    MappingBuilder.FieldMapBuilder fieldMapBuilder = definitionBuilder.field();

    NodeList nl = ele.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      Node node = nl.item(i);
      if (node instanceof Element) {
        Element element = (Element) node;

        if (A_ELEMENT.equals(element.getNodeName())) {
          String name = element.getFirstChild().getNodeValue().trim();
          String type = element.getAttribute(TYPE_ATTRIBUTE);
          MappingBuilder.FieldBuilder builder = fieldMapBuilder.a(name, type);
          parseField(element, builder);
        }
        if (B_ELEMENT.equals(element.getNodeName())) {
          String name = element.getFirstChild().getNodeValue().trim();
          String type = element.getAttribute(TYPE_ATTRIBUTE);
          MappingBuilder.FieldBuilder builder = fieldMapBuilder.b(name, type);
          parseField(element, builder);
        }
      }
    }

    return fieldMapBuilder;
  }

  private void parseFieldMap(Element ele, MappingBuilder.FieldMapBuilder fieldMapBuilder) {
    setRelationshipType(ele, fieldMapBuilder);

    if (StringUtils.isNotEmpty(ele.getAttribute(REMOVE_ORPHANS))) {
      fieldMapBuilder.removeOrphans(BooleanUtils.toBoolean(ele.getAttribute(REMOVE_ORPHANS)));
    }
    NodeList nl = ele.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      Node node = nl.item(i);
      if (node instanceof Element) {
        Element element = (Element) node;

        debugElement(element);

        parseFieldElements(element, fieldMapBuilder);
        if (SRC_TYPE_HINT_ELEMENT.equals(element.getNodeName())) {
          String hint = element.getFirstChild().getNodeValue().trim();
          fieldMapBuilder.srcHintContainer(hint);
        }
        if (DEST_TYPE_HINT_ELEMENT.equals(element.getNodeName())) {
          String hint = element.getFirstChild().getNodeValue().trim();
          fieldMapBuilder.destHintContainer(hint);
        }
        if (SRC_TYPE_DEEP_INDEX_HINT_ELEMENT.equals(element.getNodeName())) {
          String hint = element.getFirstChild().getNodeValue().trim();
          fieldMapBuilder.srcDeepIndexHintContainer(hint);
        }
        if (DEST_TYPE_DEEP_INDEX_HINT_ELEMENT.equals(element.getNodeName())) {
          String hint = element.getFirstChild().getNodeValue().trim();
          fieldMapBuilder.destDeepIndexHintContainer(hint);
        }
      }
    }
  }

  private void setRelationshipType(Element ele, MappingBuilder.FieldMapBuilder definitionBuilder) {
    RelationshipType relationshipType = null;
    if (StringUtils.isNotEmpty(ele.getAttribute(RELATIONSHIP_TYPE))) {
      String relationshipTypeValue = ele.getAttribute(RELATIONSHIP_TYPE);
      relationshipType = RelationshipType.valueOf(relationshipTypeValue);
    }
    definitionBuilder.relationshipType(relationshipType);
  }

  private void parseField(Element ele, MappingBuilder.FieldBuilder fieldBuilder) {
    if (StringUtils.isNotEmpty(ele.getAttribute(DATE_FORMAT))) {
      fieldBuilder.dateFormat(ele.getAttribute(DATE_FORMAT));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(THE_GET_METHOD_ATTRIBUTE))) {
      fieldBuilder.theGetMethod(ele.getAttribute(THE_GET_METHOD_ATTRIBUTE));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(THE_SET_METHOD_ATTRIBUTE))) {
      fieldBuilder.theSetMethod(ele.getAttribute(THE_SET_METHOD_ATTRIBUTE));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(MAP_GET_METHOD_ATTRIBUTE))) {
      fieldBuilder.mapGetMethod(ele.getAttribute(MAP_GET_METHOD_ATTRIBUTE));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(MAP_SET_METHOD_ATTRIBUTE))) {
      fieldBuilder.mapSetMethod(ele.getAttribute(MAP_SET_METHOD_ATTRIBUTE));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(KEY_ATTRIBUTE))) {
      fieldBuilder.key(ele.getAttribute(KEY_ATTRIBUTE));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(CREATE_METHOD_ATTRIBUTE))) {
      fieldBuilder.createMethod(ele.getAttribute(CREATE_METHOD_ATTRIBUTE));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(IS_ACCESSIBLE_ATTRIBUTE))) {
      fieldBuilder.accessible(BooleanUtils.toBoolean(ele.getAttribute(IS_ACCESSIBLE_ATTRIBUTE)));
    }
  }

  private void parseConfiguration(Element ele, MappingBuilder builder) {
    MappingBuilder.MappingConfigurationBuilder configBuilder = builder.configuration();
    NodeList nl = ele.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      Node node = nl.item(i);
      if (node instanceof Element) {
        Element element = (Element) node;

        debugElement(element);

        final String nodeValue = element.getFirstChild().getNodeValue();

        if (STOP_ON_ERRORS_ELEMENT.equals(element.getNodeName())) {
          configBuilder.stopOnErrors(Boolean.valueOf(nodeValue.trim()));
        } else if (DATE_FORMAT.equals(element.getNodeName())) {
          configBuilder.dateFormat(nodeValue.trim());
        } else if (WILDCARD.equals(element.getNodeName())) {
          configBuilder.wildcard(Boolean.valueOf(nodeValue.trim()));
        } else if (TRIM_STRINGS.equals(element.getNodeName())) {
          configBuilder.trimStrings(Boolean.valueOf(nodeValue.trim()));
        } else if (RELATIONSHIP_TYPE.equals(element.getNodeName())) {
          RelationshipType relationshipType = RelationshipType.valueOf(nodeValue.trim());
          if (relationshipType == null) {
            relationshipType = DozerConstants.DEFAULT_RELATIONSHIP_TYPE_POLICY;
          }
          configBuilder.relationshipType(relationshipType);
        } else if (BEAN_FACTORY.equals(element.getNodeName())) {
          configBuilder.beanFactory(nodeValue.trim());
        } else if (CUSTOM_CONVERTERS_ELEMENT.equals(element.getNodeName())) {
          parseCustomConverters(element, configBuilder);
        } else if (COPY_BY_REFERENCES_ELEMENT.equals(element.getNodeName())) {
          parseCopyByReferences(element, configBuilder);
        } else if (ALLOWED_EXCEPTIONS_ELEMENT.equals(element.getNodeName())) {
          parseAllowedExceptions(element, configBuilder);
        }
      }
    }
  }

  private void parseCustomConverters(Element ele, MappingBuilder.MappingConfigurationBuilder config) {
    NodeList nl = ele.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      Node node = nl.item(i);
      if (node instanceof Element) {
        Element element = (Element) node;

        debugElement(element);

        if (CONVERTER_ELEMENT.equals(element.getNodeName())) {
          String converterType = element.getAttribute(TYPE_ATTRIBUTE).trim();
          MappingBuilder.CustomConverterBuilder customConverterBuilder = config.customConverter(converterType);

          NodeList list = element.getChildNodes();
          for (int x = 0; x < list.getLength(); x++) {
            Node node1 = list.item(x);
            if (node1 instanceof Element) {
              Element element1 = (Element) node1;
              if (CLASS_A_ELEMENT.equals(element1.getNodeName())) {
                customConverterBuilder.classA(element1.getFirstChild().getNodeValue().trim());
              } else if (CLASS_B_ELEMENT.equals(element1.getNodeName())) {
                customConverterBuilder.classB(element1.getFirstChild().getNodeValue().trim());
              }
            }
          }
        }
      }
    }
  }

  private void debugElement(Element element) {
    if (log.isDebugEnabled()) {
      log.debug("config name: " + element.getNodeName());
      log.debug("  value: " + element.getFirstChild().getNodeValue());
    }
  }

  private void parseCopyByReferences(Element ele, MappingBuilder.MappingConfigurationBuilder config) {
    NodeList nl = ele.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      Node node = nl.item(i);
      if (node instanceof Element) {
        Element element = (Element) node;

        debugElement(element);

        if (COPY_BY_REFERENCE.equals(element.getNodeName())) {
          String typeMask = element.getFirstChild().getNodeValue().trim();
          config.copyByReference(typeMask);
        }
      }
    }
  }

  private void parseAllowedExceptions(Element ele, MappingBuilder.MappingConfigurationBuilder config) {
    NodeList nl = ele.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      Node node = nl.item(i);
      if (node instanceof Element) {
        Element element = (Element) node;

        debugElement(element);

        if (ALLOWED_EXCEPTION_ELEMENT.equals(element.getNodeName())) {
          String exceptionType = element.getFirstChild().getNodeValue().trim();
          config.allowedException(exceptionType);
        }
      }
    }
  }

}
