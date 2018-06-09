/*
 * Copyright 2005-2018 Dozer Project
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
package com.github.dozermapper.core.loader.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.github.dozermapper.core.classmap.MappingDirection;
import com.github.dozermapper.core.classmap.MappingFileData;
import com.github.dozermapper.core.classmap.RelationshipType;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.el.ELEngine;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.loader.DozerBuilder;
import com.github.dozermapper.core.loader.MappingsSource;
import com.github.dozermapper.core.propertydescriptor.PropertyDescriptorFactory;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Internal class that parses a raw custom xml mapping file into ClassMap objects.
 * <p>
 * Only intended for internal use.
 */
@Deprecated
public class XMLParser implements MappingsSource<Document> {

    private final Logger log = LoggerFactory.getLogger(XMLParser.class);

    // Common Elements/Attributes
    private static final String WILDCARD = "wildcard";
    private static final String WILDCARD_CASE_INSENSITIVE = "wildcard-case-insensitive";
    private static final String TRIM_STRINGS = "trim-strings";
    private static final String BEAN_FACTORY = "bean-factory";
    private static final String DATE_FORMAT = "date-format";
    private static final String RELATIONSHIP_TYPE = "relationship-type";
    private static final String REMOVE_ORPHANS = "remove-orphans";
    private static final String MAP_NULL = "map-null";
    private static final String MAP_EMPTY_STRING = "map-empty-string";

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
    private static final String VARIABLES_ELEMENT = "variables";
    private static final String VARIABLE_ELEMENT = "variable";

    // Parsing Attributes
    private static final String TYPE_ATTRIBUTE = "type";
    private static final String NAME_ATTRIBUTE = "name";
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
    private static final String SKIP_CONSTRUCTOR_ATTRIBUTE = "skip-constructor";
    private static final String MAP_NULL_ATTRIBUTE = "map-null";
    private static final String MAP_EMPTY_STRING_ATTRIBUTE = "map-empty-string";
    private static final String CUSTOM_CONVERTER_ATTRIBUTE = "custom-converter";
    private static final String CUSTOM_CONVERTER_ID_ATTRIBUTE = "custom-converter-id";
    private static final String CUSTOM_CONVERTER_PARAM_ATTRIBUTE = "custom-converter-param";

    private final BeanContainer beanContainer;
    private final DestBeanCreator destBeanCreator;
    private final PropertyDescriptorFactory propertyDescriptorFactory;

    public XMLParser(BeanContainer beanContainer, DestBeanCreator destBeanCreator, PropertyDescriptorFactory propertyDescriptorFactory) {
        this.beanContainer = beanContainer;
        this.destBeanCreator = destBeanCreator;
        this.propertyDescriptorFactory = propertyDescriptorFactory;
    }

    private String getAttribute(Element element, String attribute) {
        return beanContainer.getElementReader().getAttribute(element, attribute);
    }

    private String getNodeValue(Element element) {
        return beanContainer.getElementReader().getNodeValue(element);
    }

    private void debugElement(Element element) {
        log.debug("config name: {}", element.getNodeName());
        log.debug("  value: {}", element.getFirstChild().getNodeValue());
    }

    /**
     * Builds object representation of mappings based on content of Xml document
     *
     * @return mapping container
     */
    public MappingFileData read(Document document) {
        DozerBuilder builder = new DozerBuilder(beanContainer, destBeanCreator, propertyDescriptorFactory);

        Element theRoot = document.getDocumentElement();
        NodeList nl = theRoot.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element) {
                Element ele = (Element)node;
                log.debug("name: {}", ele.getNodeName());
                if (CONFIGURATION_ELEMENT.equals(ele.getNodeName())) {
                    parseConfiguration(ele, builder);
                } else if (MAPPING_ELEMENT.equals(ele.getNodeName())) {
                    parseMapping(ele, builder);
                }
            }
        }

        return builder.build();
    }

    private void parseMapping(Element ele, DozerBuilder builder) {
        DozerBuilder.MappingBuilder definitionBuilder = builder.mapping();

        if (StringUtils.isNotEmpty(getAttribute(ele, DATE_FORMAT))) {
            definitionBuilder.dateFormat(getAttribute(ele, DATE_FORMAT));
        }
        if (StringUtils.isNotEmpty(getAttribute(ele, MAP_NULL_ATTRIBUTE))) {
            definitionBuilder.mapNull(BooleanUtils.toBoolean(getAttribute(ele, MAP_NULL_ATTRIBUTE)));
        }
        if (StringUtils.isNotEmpty(getAttribute(ele, MAP_EMPTY_STRING_ATTRIBUTE))) {
            definitionBuilder.mapEmptyString(BooleanUtils.toBoolean(getAttribute(ele, MAP_EMPTY_STRING_ATTRIBUTE)));
        }
        if (StringUtils.isNotEmpty(getAttribute(ele, BEAN_FACTORY))) {
            definitionBuilder.beanFactory(getAttribute(ele, BEAN_FACTORY));
        }
        if (StringUtils.isNotEmpty(getAttribute(ele, RELATIONSHIP_TYPE))) {
            String relationshipTypeValue = getAttribute(ele, RELATIONSHIP_TYPE);
            RelationshipType relationshipType = RelationshipType.valueOf(relationshipTypeValue);
            definitionBuilder.relationshipType(relationshipType);
        }
        if (StringUtils.isNotEmpty(getAttribute(ele, WILDCARD))) {
            definitionBuilder.wildcard(Boolean.valueOf(getAttribute(ele, WILDCARD)));
        }
        if (StringUtils.isNotEmpty(getAttribute(ele, WILDCARD_CASE_INSENSITIVE))) {
            definitionBuilder.wildcardCaseInsensitive(Boolean.valueOf(getAttribute(ele, WILDCARD_CASE_INSENSITIVE)));
        }
        if (StringUtils.isNotEmpty(getAttribute(ele, TRIM_STRINGS))) {
            definitionBuilder.trimStrings(Boolean.valueOf(getAttribute(ele, TRIM_STRINGS)));
        }
        if (StringUtils.isNotEmpty(getAttribute(ele, STOP_ON_ERRORS_ATTRIBUTE))) {
            definitionBuilder.stopOnErrors(Boolean.valueOf(getAttribute(ele, STOP_ON_ERRORS_ATTRIBUTE)));
        }
        if (StringUtils.isNotEmpty(getAttribute(ele, MAPID_ATTRIBUTE))) {
            definitionBuilder.mapId(getAttribute(ele, MAPID_ATTRIBUTE));
        }
        if (StringUtils.isNotEmpty(getAttribute(ele, TYPE_ATTRIBUTE))) {
            String mappingDirection = getAttribute(ele, TYPE_ATTRIBUTE);
            MappingDirection direction = MappingDirection.valueOf(mappingDirection);
            definitionBuilder.type(direction);
        }
        NodeList nl = ele.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element) {
                Element element = (Element)node;
                debugElement(element);
                if (CLASS_A_ELEMENT.equals(element.getNodeName())) {
                    String typeName = getNodeValue(element);
                    DozerBuilder.ClassDefinitionBuilder classBuilder = definitionBuilder.classA(typeName);
                    parseClass(element, classBuilder);
                }
                if (CLASS_B_ELEMENT.equals(element.getNodeName())) {
                    String typeName = getNodeValue(element);
                    DozerBuilder.ClassDefinitionBuilder classBuilder = definitionBuilder.classB(typeName);
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

    private void parseClass(Element element, DozerBuilder.ClassDefinitionBuilder classBuilder) {
        if (StringUtils.isNotEmpty(getAttribute(element, MAP_GET_METHOD_ATTRIBUTE))) {
            classBuilder.mapGetMethod(getAttribute(element, MAP_GET_METHOD_ATTRIBUTE));
        }
        if (StringUtils.isNotEmpty(getAttribute(element, MAP_SET_METHOD_ATTRIBUTE))) {
            classBuilder.mapSetMethod(getAttribute(element, MAP_SET_METHOD_ATTRIBUTE));
        }
        if (StringUtils.isNotEmpty(getAttribute(element, BEAN_FACTORY))) {
            classBuilder.beanFactory(getAttribute(element, BEAN_FACTORY));
        }
        if (StringUtils.isNotEmpty(getAttribute(element, FACTORY_BEANID_ATTRIBUTE))) {
            classBuilder.factoryBeanId(getAttribute(element, FACTORY_BEANID_ATTRIBUTE));
        }
        if (StringUtils.isNotEmpty(getAttribute(element, CREATE_METHOD_ATTRIBUTE))) {
            classBuilder.createMethod(getAttribute(element, CREATE_METHOD_ATTRIBUTE));
        }
        if (StringUtils.isNotEmpty(getAttribute(element, MAP_NULL_ATTRIBUTE))) {
            classBuilder.mapNull(Boolean.valueOf(getAttribute(element, MAP_NULL_ATTRIBUTE)));
        }
        if (StringUtils.isNotEmpty(getAttribute(element, MAP_EMPTY_STRING_ATTRIBUTE))) {
            classBuilder.mapEmptyString(Boolean.valueOf(getAttribute(element, MAP_EMPTY_STRING_ATTRIBUTE)));
        }
        if (StringUtils.isNotEmpty(getAttribute(element, IS_ACCESSIBLE_ATTRIBUTE))) {
            classBuilder.isAccessible(Boolean.valueOf(getAttribute(element, IS_ACCESSIBLE_ATTRIBUTE)));
        }
        if (StringUtils.isNotEmpty(getAttribute(element, SKIP_CONSTRUCTOR_ATTRIBUTE))) {
            classBuilder.skipConstructor(Boolean.valueOf(getAttribute(element, SKIP_CONSTRUCTOR_ATTRIBUTE)));
        }
    }

    private void parseFieldExcludeMap(Element ele, DozerBuilder.MappingBuilder definitionBuilder) {
        DozerBuilder.FieldExclusionBuilder fieldMapBuilder = definitionBuilder.fieldExclude();
        if (StringUtils.isNotEmpty(getAttribute(ele, TYPE_ATTRIBUTE))) {
            String mappingDirection = getAttribute(ele, TYPE_ATTRIBUTE);
            MappingDirection direction = MappingDirection.valueOf(mappingDirection);
            fieldMapBuilder.type(direction);
        }
        NodeList nodeList = ele.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node instanceof Element) {
                Element element = (Element)node;
                debugElement(element);
                parseFieldElements(element, fieldMapBuilder);
            }
        }
    }

    private void parseFieldElements(Element element, DozerBuilder.FieldBuider fieldMapBuilder) {
        if (A_ELEMENT.equals(element.getNodeName())) {
            String name = getNodeValue(element);
            String type = getAttribute(element, TYPE_ATTRIBUTE);
            DozerBuilder.FieldDefinitionBuilder fieldBuilder = fieldMapBuilder.a(name, type);
            parseField(element, fieldBuilder);
        }
        if (B_ELEMENT.equals(element.getNodeName())) {
            String name = getNodeValue(element);
            String type = getAttribute(element, TYPE_ATTRIBUTE);
            DozerBuilder.FieldDefinitionBuilder fieldBuilder = fieldMapBuilder.b(name, type);
            parseField(element, fieldBuilder);
        }
    }

    private void parseGenericFieldMap(Element ele, DozerBuilder.MappingBuilder definitionBuilder) {
        DozerBuilder.FieldMappingBuilder fieldMapBuilder = determineFieldMap(definitionBuilder, ele);

        if (StringUtils.isNotEmpty(getAttribute(ele, COPY_BY_REFERENCE_ATTRIBUTE))) {
            fieldMapBuilder.copyByReference(BooleanUtils.toBoolean(getAttribute(ele, COPY_BY_REFERENCE_ATTRIBUTE)));
        }
        if (StringUtils.isNotEmpty(getAttribute(ele, MAPID_ATTRIBUTE))) {
            fieldMapBuilder.mapId(getAttribute(ele, MAPID_ATTRIBUTE));
        }
        if (StringUtils.isNotEmpty(getAttribute(ele, TYPE_ATTRIBUTE))) {
            String mappingDirection = getAttribute(ele, TYPE_ATTRIBUTE);
            MappingDirection direction = MappingDirection.valueOf(mappingDirection);
            fieldMapBuilder.type(direction);
        }
        if (StringUtils.isNotEmpty(getAttribute(ele, CUSTOM_CONVERTER_ATTRIBUTE))) {
            fieldMapBuilder.customConverter(getAttribute(ele, CUSTOM_CONVERTER_ATTRIBUTE));
        }
        if (StringUtils.isNotEmpty(getAttribute(ele, CUSTOM_CONVERTER_ID_ATTRIBUTE))) {
            fieldMapBuilder.customConverterId(getAttribute(ele, CUSTOM_CONVERTER_ID_ATTRIBUTE));
        }
        if (StringUtils.isNotEmpty(getAttribute(ele, CUSTOM_CONVERTER_PARAM_ATTRIBUTE))) {
            fieldMapBuilder.customConverterParam(getAttribute(ele, CUSTOM_CONVERTER_PARAM_ATTRIBUTE));
        }

        parseFieldMap(ele, fieldMapBuilder);
    }

    private DozerBuilder.FieldMappingBuilder determineFieldMap(DozerBuilder.MappingBuilder definitionBuilder, Element ele) {
        DozerBuilder.FieldMappingBuilder fieldMapBuilder = definitionBuilder.field();

        NodeList nl = ele.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element) {
                Element element = (Element)node;

                if (A_ELEMENT.equals(element.getNodeName())) {
                    String name = getNodeValue(element);
                    String type = getAttribute(element, TYPE_ATTRIBUTE);
                    DozerBuilder.FieldDefinitionBuilder builder = fieldMapBuilder.a(name, type);
                    parseField(element, builder);
                }
                if (B_ELEMENT.equals(element.getNodeName())) {
                    String name = getNodeValue(element);
                    String type = getAttribute(element, TYPE_ATTRIBUTE);
                    DozerBuilder.FieldDefinitionBuilder builder = fieldMapBuilder.b(name, type);
                    parseField(element, builder);
                }
            }
        }

        return fieldMapBuilder;
    }

    private void parseFieldMap(Element ele, DozerBuilder.FieldMappingBuilder fieldMapBuilder) {
        setRelationshipType(ele, fieldMapBuilder);

        if (StringUtils.isNotEmpty(getAttribute(ele, REMOVE_ORPHANS))) {
            fieldMapBuilder.removeOrphans(BooleanUtils.toBoolean(getAttribute(ele, REMOVE_ORPHANS)));
        }

        NodeList nl = ele.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element) {
                Element element = (Element)node;

                debugElement(element);

                parseFieldElements(element, fieldMapBuilder);
                if (SRC_TYPE_HINT_ELEMENT.equals(element.getNodeName())) {
                    String hint = getNodeValue(element);
                    fieldMapBuilder.srcHintContainer(hint);
                }
                if (DEST_TYPE_HINT_ELEMENT.equals(element.getNodeName())) {
                    String hint = getNodeValue(element);
                    fieldMapBuilder.destHintContainer(hint);
                }
                if (SRC_TYPE_DEEP_INDEX_HINT_ELEMENT.equals(element.getNodeName())) {
                    String hint = getNodeValue(element);
                    fieldMapBuilder.srcDeepIndexHintContainer(hint);
                }
                if (DEST_TYPE_DEEP_INDEX_HINT_ELEMENT.equals(element.getNodeName())) {
                    String hint = getNodeValue(element);
                    fieldMapBuilder.destDeepIndexHintContainer(hint);
                }
            }
        }
    }

    private void setRelationshipType(Element ele, DozerBuilder.FieldMappingBuilder definitionBuilder) {
        RelationshipType relationshipType = null;
        if (StringUtils.isNotEmpty(getAttribute(ele, RELATIONSHIP_TYPE))) {
            String relationshipTypeValue = getAttribute(ele, RELATIONSHIP_TYPE);
            relationshipType = RelationshipType.valueOf(relationshipTypeValue);
        }
        definitionBuilder.relationshipType(relationshipType);
    }

    private void parseField(Element ele, DozerBuilder.FieldDefinitionBuilder fieldBuilder) {
        if (StringUtils.isNotEmpty(getAttribute(ele, DATE_FORMAT))) {
            fieldBuilder.dateFormat(getAttribute(ele, DATE_FORMAT));
        }
        if (StringUtils.isNotEmpty(getAttribute(ele, THE_GET_METHOD_ATTRIBUTE))) {
            fieldBuilder.theGetMethod(getAttribute(ele, THE_GET_METHOD_ATTRIBUTE));
        }
        if (StringUtils.isNotEmpty(getAttribute(ele, THE_SET_METHOD_ATTRIBUTE))) {
            fieldBuilder.theSetMethod(getAttribute(ele, THE_SET_METHOD_ATTRIBUTE));
        }
        if (StringUtils.isNotEmpty(getAttribute(ele, MAP_GET_METHOD_ATTRIBUTE))) {
            fieldBuilder.mapGetMethod(getAttribute(ele, MAP_GET_METHOD_ATTRIBUTE));
        }
        if (StringUtils.isNotEmpty(getAttribute(ele, MAP_SET_METHOD_ATTRIBUTE))) {
            fieldBuilder.mapSetMethod(getAttribute(ele, MAP_SET_METHOD_ATTRIBUTE));
        }
        if (StringUtils.isNotEmpty(getAttribute(ele, KEY_ATTRIBUTE))) {
            fieldBuilder.key(getAttribute(ele, KEY_ATTRIBUTE));
        }
        if (StringUtils.isNotEmpty(getAttribute(ele, CREATE_METHOD_ATTRIBUTE))) {
            fieldBuilder.createMethod(getAttribute(ele, CREATE_METHOD_ATTRIBUTE));
        }
        if (StringUtils.isNotEmpty(getAttribute(ele, IS_ACCESSIBLE_ATTRIBUTE))) {
            fieldBuilder.accessible(BooleanUtils.toBoolean(getAttribute(ele, IS_ACCESSIBLE_ATTRIBUTE)));
        }
    }

    private void parseConfiguration(Element ele, DozerBuilder builder) {
        DozerBuilder.ConfigurationBuilder configBuilder = builder.configuration();
        NodeList nl = ele.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element) {
                Element element = (Element)node;

                debugElement(element);

                final String nodeValue = getNodeValue(element);

                if (STOP_ON_ERRORS_ELEMENT.equals(element.getNodeName())) {
                    configBuilder.stopOnErrors(Boolean.valueOf(nodeValue));
                } else if (DATE_FORMAT.equals(element.getNodeName())) {
                    configBuilder.dateFormat(nodeValue);
                } else if (WILDCARD.equals(element.getNodeName())) {
                    configBuilder.wildcard(Boolean.valueOf(nodeValue));
                } else if (WILDCARD_CASE_INSENSITIVE.equals(element.getNodeName())) {
                    configBuilder.wildcardCaseInsensitive(Boolean.valueOf(nodeValue));
                } else if (TRIM_STRINGS.equals(element.getNodeName())) {
                    configBuilder.trimStrings(Boolean.valueOf(nodeValue));
                } else if (MAP_NULL.equals(element.getNodeName())) {
                    configBuilder.mapNull(Boolean.valueOf(nodeValue));
                } else if (MAP_EMPTY_STRING.equals(element.getNodeName())) {
                    configBuilder.mapEmptyString(Boolean.valueOf(nodeValue));
                } else if (RELATIONSHIP_TYPE.equals(element.getNodeName())) {
                    RelationshipType relationshipType = RelationshipType.valueOf(nodeValue);
                    configBuilder.relationshipType(relationshipType);
                } else if (BEAN_FACTORY.equals(element.getNodeName())) {
                    configBuilder.beanFactory(nodeValue);
                } else if (CUSTOM_CONVERTERS_ELEMENT.equals(element.getNodeName())) {
                    parseCustomConverters(element, configBuilder);
                } else if (COPY_BY_REFERENCES_ELEMENT.equals(element.getNodeName())) {
                    parseCopyByReferences(element, configBuilder);
                } else if (ALLOWED_EXCEPTIONS_ELEMENT.equals(element.getNodeName())) {
                    parseAllowedExceptions(element, configBuilder);
                } else if (VARIABLES_ELEMENT.equals(element.getNodeName())) {
                    parseVariables(element);
                }
            }
        }
    }

    private void parseVariables(Element element) {
        NodeList nl = element.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element) {
                Element ele = (Element)node;

                debugElement(ele);

                if (VARIABLE_ELEMENT.equals(ele.getNodeName())) {
                    ELEngine engine = beanContainer.getElEngine();
                    if (engine != null) {
                        String name = getAttribute(ele, NAME_ATTRIBUTE);
                        String value = getNodeValue(ele);

                        engine.setVariable(name, value);
                    }
                }
            }
        }

    }

    private void parseCustomConverters(Element ele, DozerBuilder.ConfigurationBuilder config) {
        NodeList nl = ele.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element) {
                Element element = (Element)node;

                debugElement(element);

                if (CONVERTER_ELEMENT.equals(element.getNodeName())) {
                    String converterType = getAttribute(element, TYPE_ATTRIBUTE);
                    DozerBuilder.CustomConverterBuilder customConverterBuilder = config.customConverter(converterType);

                    NodeList list = element.getChildNodes();
                    for (int x = 0; x < list.getLength(); x++) {
                        Node node1 = list.item(x);
                        if (node1 instanceof Element) {
                            Element element1 = (Element)node1;
                            if (CLASS_A_ELEMENT.equals(element1.getNodeName())) {
                                customConverterBuilder.classA(getNodeValue(element1));
                            } else if (CLASS_B_ELEMENT.equals(element1.getNodeName())) {
                                customConverterBuilder.classB(getNodeValue(element1));
                            }
                        }
                    }
                }
            }
        }
    }

    private void parseCopyByReferences(Element ele, DozerBuilder.ConfigurationBuilder config) {
        NodeList nl = ele.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element) {
                Element element = (Element)node;

                debugElement(element);

                if (COPY_BY_REFERENCE.equals(element.getNodeName())) {
                    String typeMask = getNodeValue(element);
                    config.copyByReference(typeMask);
                }
            }
        }
    }

    private void parseAllowedExceptions(Element ele, DozerBuilder.ConfigurationBuilder config) {
        NodeList nl = ele.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element) {
                Element element = (Element)node;

                debugElement(element);

                if (ALLOWED_EXCEPTION_ELEMENT.equals(element.getNodeName())) {
                    String exceptionType = getNodeValue(element);
                    config.allowedException(exceptionType);
                }
            }
        }
    }

}
