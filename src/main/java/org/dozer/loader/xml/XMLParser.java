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
import org.dozer.classmap.AllowedExceptionContainer;
import org.dozer.classmap.ClassMap;
import org.dozer.classmap.Configuration;
import org.dozer.classmap.CopyByReference;
import org.dozer.classmap.CopyByReferenceContainer;
import org.dozer.classmap.DozerClass;
import org.dozer.classmap.MappingFileData;
import org.dozer.classmap.RelationshipType;
import org.dozer.converters.CustomConverterContainer;
import org.dozer.converters.CustomConverterDescription;
import org.dozer.fieldmap.CustomGetSetMethodFieldMap;
import org.dozer.fieldmap.DozerField;
import org.dozer.fieldmap.ExcludeFieldMap;
import org.dozer.fieldmap.FieldMap;
import org.dozer.fieldmap.GenericFieldMap;
import org.dozer.fieldmap.HintContainer;
import org.dozer.fieldmap.MapFieldMap;
import org.dozer.util.DozerConstants;
import org.dozer.util.MappingUtils;
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

  private final MappingFileData mappings = new MappingFileData();

  public MappingFileData parse(Document document) {
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
          parseConfiguration(ele);
        } else if (MAPPING_ELEMENT.equals(ele.getNodeName())) {
          parseMapping(ele);
        }
      }
    }
    return mappings;
  }

  private void parseMapping(Element ele) {
    ClassMap classMap = new ClassMap(mappings.getConfiguration());
    mappings.getClassMaps().add(classMap);
    if (StringUtils.isNotEmpty(ele.getAttribute(DATE_FORMAT))) {
      classMap.setDateFormat(ele.getAttribute(DATE_FORMAT));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(MAP_NULL_ATTRIBUTE))) {
      classMap.setMapNull(BooleanUtils.toBoolean(ele.getAttribute(MAP_NULL_ATTRIBUTE)));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(MAP_EMPTY_STRING_ATTRIBUTE))) {
      classMap.setMapEmptyString(BooleanUtils.toBoolean(ele.getAttribute(MAP_EMPTY_STRING_ATTRIBUTE)));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(BEAN_FACTORY))) {
      classMap.setBeanFactory(ele.getAttribute(BEAN_FACTORY));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(RELATIONSHIP_TYPE))) {
      String relationshipTypeValue = ele.getAttribute(RELATIONSHIP_TYPE);
      RelationshipType relationshipType = RelationshipType.valueOf(relationshipTypeValue);
      classMap.setRelationshipType(relationshipType);
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(WILDCARD))) {
      classMap.setWildcard(Boolean.valueOf(ele.getAttribute(WILDCARD)));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(TRIM_STRINGS))) {
      classMap.setTrimStrings(Boolean.valueOf(ele.getAttribute(TRIM_STRINGS)));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(STOP_ON_ERRORS_ATTRIBUTE))) {
      classMap.setStopOnErrors(Boolean.valueOf(ele.getAttribute(STOP_ON_ERRORS_ATTRIBUTE)));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(MAPID_ATTRIBUTE))) {
      classMap.setMapId(ele.getAttribute(MAPID_ATTRIBUTE));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(TYPE_ATTRIBUTE))) {
      classMap.setType(ele.getAttribute(TYPE_ATTRIBUTE));
    }
    NodeList nl = ele.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      Node node = nl.item(i);
      if (node instanceof Element) {
        Element element = (Element) node;
        if (log.isDebugEnabled()) {
          log.debug("config name: " + element.getNodeName());
          log.debug("  value: " + element.getFirstChild().getNodeValue());
        }
        if (CLASS_A_ELEMENT.equals(element.getNodeName())) {
          DozerClass source = new DozerClass();
          source.setName(element.getFirstChild().getNodeValue().trim());
          if (StringUtils.isNotEmpty(element.getAttribute(MAP_GET_METHOD_ATTRIBUTE))) {
            source.setMapGetMethod(element.getAttribute(MAP_GET_METHOD_ATTRIBUTE));
          }
          if (StringUtils.isNotEmpty(element.getAttribute(MAP_SET_METHOD_ATTRIBUTE))) {
            source.setMapSetMethod(element.getAttribute(MAP_SET_METHOD_ATTRIBUTE));
          }
          if (StringUtils.isNotEmpty(element.getAttribute(BEAN_FACTORY))) {
            source.setBeanFactory(element.getAttribute(BEAN_FACTORY));
          }
          if (StringUtils.isNotEmpty(element.getAttribute(FACTORY_BEANID_ATTRIBUTE))) {
            source.setFactoryBeanId(element.getAttribute(FACTORY_BEANID_ATTRIBUTE));
          }
          if (StringUtils.isNotEmpty(element.getAttribute(CREATE_METHOD_ATTRIBUTE))) {
            source.setCreateMethod(element.getAttribute(CREATE_METHOD_ATTRIBUTE));
          }
          if (StringUtils.isNotEmpty(element.getAttribute(MAP_NULL_ATTRIBUTE))) {
            source.setMapNull(Boolean.valueOf(element.getAttribute(MAP_NULL_ATTRIBUTE)));
          }
          if (StringUtils.isNotEmpty(element.getAttribute(MAP_EMPTY_STRING_ATTRIBUTE))) {
            source.setMapEmptyString(Boolean.valueOf(element.getAttribute(MAP_EMPTY_STRING_ATTRIBUTE)));
          }
          classMap.setSrcClass(source);
        }
        if (CLASS_B_ELEMENT.equals(element.getNodeName())) {
          DozerClass dest = new DozerClass();
          dest.setName(element.getFirstChild().getNodeValue().trim());
          if (StringUtils.isNotEmpty(element.getAttribute(MAP_GET_METHOD_ATTRIBUTE))) {
            dest.setMapGetMethod(element.getAttribute(MAP_GET_METHOD_ATTRIBUTE));
          }
          if (StringUtils.isNotEmpty(element.getAttribute(MAP_SET_METHOD_ATTRIBUTE))) {
            dest.setMapSetMethod(element.getAttribute(MAP_SET_METHOD_ATTRIBUTE));
          }
          if (StringUtils.isNotEmpty(element.getAttribute(BEAN_FACTORY))) {
            dest.setBeanFactory(element.getAttribute(BEAN_FACTORY));
          }
          if (StringUtils.isNotEmpty(element.getAttribute(FACTORY_BEANID_ATTRIBUTE))) {
            dest.setFactoryBeanId(element.getAttribute(FACTORY_BEANID_ATTRIBUTE));
          }
          if (StringUtils.isNotEmpty(element.getAttribute(CREATE_METHOD_ATTRIBUTE))) {
            dest.setCreateMethod(element.getAttribute(CREATE_METHOD_ATTRIBUTE));
          }
          if (StringUtils.isNotEmpty(element.getAttribute(MAP_NULL_ATTRIBUTE))) {
            dest.setMapNull(Boolean.valueOf(element.getAttribute(MAP_NULL_ATTRIBUTE)));
          }
          if (StringUtils.isNotEmpty(element.getAttribute(MAP_EMPTY_STRING_ATTRIBUTE))) {
            dest.setMapEmptyString(Boolean.valueOf(element.getAttribute(MAP_EMPTY_STRING_ATTRIBUTE)));
          }
          classMap.setDestClass(dest);
        }
        if (FIELD_ELEMENT.equals(element.getNodeName())) {
          parseGenericFieldMap(element, classMap);
        } else if (FIELD_EXCLUDE_ELEMENT.equals(element.getNodeName())) {
          parseFieldExcludeMap(element, classMap);
        }
      }
    }
  }

  private void parseFieldExcludeMap(Element ele, ClassMap classMap) {
    ExcludeFieldMap excludeFieldMap = new ExcludeFieldMap(classMap);
    if (StringUtils.isNotEmpty(ele.getAttribute(TYPE_ATTRIBUTE))) {
      excludeFieldMap.setType(ele.getAttribute(TYPE_ATTRIBUTE));
    }
    classMap.addFieldMapping(excludeFieldMap);
    NodeList nodeList = ele.getChildNodes();
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (node instanceof Element) {
        Element element = (Element) node;
        if (log.isDebugEnabled()) {
          log.debug("config name: " + element.getNodeName());
          log.debug("  value: " + element.getFirstChild().getNodeValue());
        }
        parseFieldElements(element, excludeFieldMap);
      }
    }
  }

  private void parseFieldElements(Element element, FieldMap fieldMap) {
    if (A_ELEMENT.equals(element.getNodeName())) {
      fieldMap.setSrcField(parseField(element));
    }
    if (B_ELEMENT.equals(element.getNodeName())) {
      fieldMap.setDestField(parseField(element));
    }
  }

  private void parseGenericFieldMap(Element ele, ClassMap classMap) {
    FieldMap fm = determineFieldMap(classMap, ele);
    classMap.addFieldMapping(fm);
    if (StringUtils.isNotEmpty(ele.getAttribute(COPY_BY_REFERENCE_ATTRIBUTE))) {
      fm.setCopyByReference(BooleanUtils.toBoolean(ele.getAttribute(COPY_BY_REFERENCE_ATTRIBUTE)));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(MAPID_ATTRIBUTE))) {
      fm.setMapId(ele.getAttribute(MAPID_ATTRIBUTE));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(TYPE_ATTRIBUTE))) {
      fm.setType(ele.getAttribute(TYPE_ATTRIBUTE));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(CUSTOM_CONVERTER_ATTRIBUTE))) {
      fm.setCustomConverter(ele.getAttribute(CUSTOM_CONVERTER_ATTRIBUTE));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(CUSTOM_CONVERTER_ID_ATTRIBUTE))) {
      fm.setCustomConverterId(ele.getAttribute(CUSTOM_CONVERTER_ID_ATTRIBUTE));
    }

    if (StringUtils.isNotEmpty(ele.getAttribute(CUSTOM_CONVERTER_PARAM_ATTRIBUTE))) {
      fm.setCustomConverterParam(ele.getAttribute(CUSTOM_CONVERTER_PARAM_ATTRIBUTE));
    }

    parseFieldMap(ele, fm);
  }

  private FieldMap determineFieldMap(ClassMap classMap, Element ele) {
    DozerField srcField = null;
    DozerField destField = null;
    FieldMap result;
    NodeList nl = ele.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      Node node = nl.item(i);
      if (node instanceof Element) {
        Element element = (Element) node;

        if (A_ELEMENT.equals(element.getNodeName())) {
          srcField = parseField(element);
        }
        if (B_ELEMENT.equals(element.getNodeName())) {
          destField = parseField(element);
        }
      }
    }

    if (srcField.isMapTypeCustomGetterSetterField() || destField.isMapTypeCustomGetterSetterField()
            || classMap.isSrcClassMapTypeCustomGetterSetter() || classMap.isDestClassMapTypeCustomGetterSetter()) {
      result = new MapFieldMap(classMap);
    } else if (srcField.isCustomGetterSetterField() || destField.isCustomGetterSetterField()) {
      result = new CustomGetSetMethodFieldMap(classMap);
    } else {
      result = new GenericFieldMap(classMap);
    }

    return result;
  }

  private void parseFieldMap(Element ele, FieldMap fieldMap) {
    setRelationshipType(ele, fieldMap);

    if (StringUtils.isNotEmpty(ele.getAttribute(REMOVE_ORPHANS))) {
      fieldMap.setRemoveOrphans(BooleanUtils.toBoolean(ele.getAttribute(REMOVE_ORPHANS)));
    }
    NodeList nl = ele.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      Node node = nl.item(i);
      if (node instanceof Element) {
        Element element = (Element) node;

        if (log.isDebugEnabled()) {
          log.debug("config name: " + element.getNodeName());
          log.debug("  value: " + element.getFirstChild().getNodeValue());
        }

        parseFieldElements(element, fieldMap);
        if (SRC_TYPE_HINT_ELEMENT.equals(element.getNodeName())) {
          HintContainer hintContainer = new HintContainer();
          hintContainer.setHintName(element.getFirstChild().getNodeValue().trim());
          fieldMap.setSrcHintContainer(hintContainer);
        }
        if (DEST_TYPE_HINT_ELEMENT.equals(element.getNodeName())) {
          HintContainer hintContainer = new HintContainer();
          hintContainer.setHintName(element.getFirstChild().getNodeValue().trim());
          fieldMap.setDestHintContainer(hintContainer);
        }
        if (SRC_TYPE_DEEP_INDEX_HINT_ELEMENT.equals(element.getNodeName())) {
          HintContainer hintContainer = new HintContainer();
          hintContainer.setHintName(element.getFirstChild().getNodeValue().trim());
          fieldMap.setSrcDeepIndexHintContainer(hintContainer);
        }
        if (DEST_TYPE_DEEP_INDEX_HINT_ELEMENT.equals(element.getNodeName())) {
          HintContainer hintContainer = new HintContainer();
          hintContainer.setHintName(element.getFirstChild().getNodeValue().trim());
          fieldMap.setDestDeepIndexHintContainer(hintContainer);
        }
      }
    }
  }

  private void setRelationshipType(Element ele, FieldMap fieldMap) {
    RelationshipType relationshipType = null;
    if (StringUtils.isNotEmpty(ele.getAttribute(RELATIONSHIP_TYPE))) {
      String relationshipTypeValue = ele.getAttribute(RELATIONSHIP_TYPE);
      relationshipType = RelationshipType.valueOf(relationshipTypeValue);
    }
    fieldMap.setRelationshipType(relationshipType);
  }

  private boolean isIndexed(String fieldName) {
    return (fieldName != null) && (fieldName.matches(".+\\[\\d+\\]"));
  }

  String getFieldNameOfIndexedField(String fieldName) {
    return fieldName == null ? null : fieldName.replaceAll("\\[\\d+\\]$", "");
  }

  private int getIndexOfIndexedField(String fieldName) {
    return Integer.parseInt(fieldName.replaceAll(".*\\[", "").replaceAll("\\]", ""));
  }

  private DozerField parseField(Element ele) {
    DozerField rvalue;
    String type = null;
    String fieldName;
    String name = (ele.getFirstChild().getNodeValue().trim());
    if (isIndexed(name)) {
      fieldName = getFieldNameOfIndexedField(name);
    } else {
      fieldName = name;
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(TYPE_ATTRIBUTE))) {
      type = ele.getAttribute(TYPE_ATTRIBUTE);
    }
    rvalue = new DozerField(fieldName, type);
    if (isIndexed(name)) {
      rvalue.setIndexed(true);
      rvalue.setIndex(getIndexOfIndexedField(name));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(DATE_FORMAT))) {
      rvalue.setDateFormat(ele.getAttribute(DATE_FORMAT));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(THE_GET_METHOD_ATTRIBUTE))) {
      rvalue.setTheGetMethod(ele.getAttribute(THE_GET_METHOD_ATTRIBUTE));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(THE_SET_METHOD_ATTRIBUTE))) {
      rvalue.setTheSetMethod(ele.getAttribute(THE_SET_METHOD_ATTRIBUTE));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(MAP_GET_METHOD_ATTRIBUTE))) {
      rvalue.setMapGetMethod(ele.getAttribute(MAP_GET_METHOD_ATTRIBUTE));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(MAP_SET_METHOD_ATTRIBUTE))) {
      rvalue.setMapSetMethod(ele.getAttribute(MAP_SET_METHOD_ATTRIBUTE));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(KEY_ATTRIBUTE))) {
      rvalue.setKey(ele.getAttribute(KEY_ATTRIBUTE));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(CREATE_METHOD_ATTRIBUTE))) {
      rvalue.setCreateMethod(ele.getAttribute(CREATE_METHOD_ATTRIBUTE));
    }
    if (StringUtils.isNotEmpty(ele.getAttribute(IS_ACCESSIBLE_ATTRIBUTE))) {
      rvalue.setAccessible(BooleanUtils.toBoolean(ele.getAttribute(IS_ACCESSIBLE_ATTRIBUTE)));
    }
    return rvalue;
  }

  private void parseConfiguration(Element ele) {
    Configuration config = new Configuration();
    mappings.setConfiguration(config);
    NodeList nl = ele.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      Node node = nl.item(i);
      if (node instanceof Element) {
        Element element = (Element) node;

        if (log.isDebugEnabled()) {
          log.debug("config name: " + element.getNodeName());
          log.debug("  value: " + element.getFirstChild().getNodeValue());
        }

        final String nodeValue = element.getFirstChild().getNodeValue();

        if (STOP_ON_ERRORS_ELEMENT.equals(element.getNodeName())) {
          config.setStopOnErrors(Boolean.valueOf(nodeValue.trim()));
        } else if (DATE_FORMAT.equals(element.getNodeName())) {
          config.setDateFormat(nodeValue.trim());
        } else if (WILDCARD.equals(element.getNodeName())) {
          config.setWildcard(Boolean.valueOf(nodeValue.trim()));
        } else if (TRIM_STRINGS.equals(element.getNodeName())) {
          config.setTrimStrings(Boolean.valueOf(nodeValue.trim()));
        } else if (RELATIONSHIP_TYPE.equals(element.getNodeName())) {
          RelationshipType relationshipType = RelationshipType.valueOf(nodeValue.trim());
          if (relationshipType == null) {
            relationshipType = DozerConstants.DEFAULT_RELATIONSHIP_TYPE_POLICY;
          }
          config.setRelationshipType(relationshipType);
        } else if (BEAN_FACTORY.equals(element.getNodeName())) {
          config.setBeanFactory(nodeValue.trim());
        } else if (CUSTOM_CONVERTERS_ELEMENT.equals(element.getNodeName())) {
          parseCustomConverters(element, config);
        } else if (COPY_BY_REFERENCES_ELEMENT.equals(element.getNodeName())) {
          parseCopyByReferences(element, config);
        } else if (ALLOWED_EXCEPTIONS_ELEMENT.equals(element.getNodeName())) {
          parseAllowedExceptions(element, config);
        }
      }
    }
  }

  private void parseCustomConverters(Element ele, Configuration config) {
    CustomConverterContainer container = new CustomConverterContainer();
    config.setCustomConverters(container);
    NodeList nl = ele.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      Node node = nl.item(i);
      if (node instanceof Element) {
        Element element = (Element) node;

        if (log.isDebugEnabled()) {
          log.debug("config name: " + element.getNodeName());
          log.debug("  value: " + element.getFirstChild().getNodeValue());
        }

        if (CONVERTER_ELEMENT.equals(element.getNodeName())) {
          CustomConverterDescription customConverter = new CustomConverterDescription();
          container.addConverter(customConverter);
          customConverter.setType(MappingUtils.loadClass(element.getAttribute(TYPE_ATTRIBUTE)));
          NodeList list = element.getChildNodes();
          for (int x = 0; x < list.getLength(); x++) {
            Node node1 = list.item(x);
            if (node1 instanceof Element) {
              Element element1 = (Element) node1;
              if (CLASS_A_ELEMENT.equals(element1.getNodeName())) {
                customConverter.setClassA(MappingUtils.loadClass(element1.getFirstChild().getNodeValue().trim()));
              } else if (CLASS_B_ELEMENT.equals(element1.getNodeName())) {
                customConverter.setClassB(MappingUtils.loadClass(element1.getFirstChild().getNodeValue().trim()));
              }
            }
          }
        }
      }
    }
  }

  private void parseCopyByReferences(Element ele, Configuration config) {
    CopyByReferenceContainer container = new CopyByReferenceContainer();
    config.setCopyByReferences(container);
    NodeList nl = ele.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      Node node = nl.item(i);
      if (node instanceof Element) {
        Element element = (Element) node;

        if (log.isDebugEnabled()) {
          log.debug("config name: " + element.getNodeName());
          log.debug("  value: " + element.getFirstChild().getNodeValue());
        }

        if (COPY_BY_REFERENCE.equals(element.getNodeName())) {
          String typeMask = element.getFirstChild().getNodeValue().trim();
          CopyByReference copyByReference = new CopyByReference(typeMask);
          container.add(copyByReference);
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void parseAllowedExceptions(Element ele, Configuration config) {
    AllowedExceptionContainer container = new AllowedExceptionContainer();
    config.setAllowedExceptions(container);
    NodeList nl = ele.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      Node node = nl.item(i);
      if (node instanceof Element) {
        Element element = (Element) node;

        if (log.isDebugEnabled()) {
          log.debug("config name: " + element.getNodeName());
          log.debug("  value: " + element.getFirstChild().getNodeValue());
        }

        if (ALLOWED_EXCEPTION_ELEMENT.equals(element.getNodeName())) {
          Class<?> ex = MappingUtils.loadClass(element.getFirstChild().getNodeValue());
          if (!RuntimeException.class.isAssignableFrom(ex)) {
            MappingUtils.throwMappingException("allowed-exception Class must extend RuntimeException: "
                    + element.getFirstChild().getNodeValue());
          }
          container.getExceptions().add((Class<RuntimeException>) ex);
        }
      }
    }
  }

}
