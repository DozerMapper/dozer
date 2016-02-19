package org.dozer.functional_tests;

import org.dozer.CustomConverter;
import org.dozer.DozerBeanMapper;
import org.dozer.converters.DefaultValueConverterWithObjectParam;
import org.dozer.loader.api.BeanMappingBuilder;
import org.dozer.loader.api.FieldsMappingOptions;
import org.dozer.loader.api.TypeMappingBuilder;
import org.dozer.vo.Fruit;
import org.dozer.vo.InsideTestObject;
import org.dozer.vo.TestObject;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by mbruncic on 2/19/16.
 */
public class CustomConverterWithObjectParam {

    @Test
    public void testObjectInParam() {
        final InsideTestObject defaultValue = new InsideTestObject();
        defaultValue.setLabel("DEFAULT");
        BeanMappingBuilder beanMappingBuilder = new BeanMappingBuilder() {
            @Override
            protected void configure() {
                TypeMappingBuilder mapping = mapping(Fruit.class, TestObject.class);
                mapping.fields(
                        field("name").accessible(true)
                        , field("three").accessible(true)
                        , FieldsMappingOptions.customConverter(DefaultValueConverterWithObjectParam.class, defaultValue));
            }
        };
        DozerBeanMapper dozerBeanMapper = new DozerBeanMapper();
        dozerBeanMapper.addMapping(beanMappingBuilder);
        ArrayList<CustomConverter> customConverters = new ArrayList<CustomConverter>();
        customConverters.add(new DefaultValueConverterWithObjectParam());
        dozerBeanMapper.setCustomConverters(customConverters);

        Fruit sourceObject = new Fruit();
        sourceObject.setName(null);
        TestObject destinationObject1 = dozerBeanMapper.map(sourceObject, TestObject.class);
        assertNotNull(destinationObject1.getThree());
        assertEquals(destinationObject1.getThree(), defaultValue);
    }


}
