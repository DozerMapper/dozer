/*
 * Copyright (c) 2015, COSMOS N.V. All Rights Reserved.
 *
 */
package org.dozer.converters;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.dozer.CustomConverter;
import org.dozer.DozerBeanMapper;
import org.dozer.DozerConverter;
import org.dozer.Mapper;
import org.junit.Test;

public class CustomConverterConfigurationTest {
    public static class IntegerToBooleanConverter extends DozerConverter<Integer, Boolean> {
        public IntegerToBooleanConverter() {
            super(Integer.class, Boolean.class);
        }

        @Override
        public Boolean convertTo(Integer source, Boolean destination) {
            return source != null && source > 0;
        }

        @Override
        public Integer convertFrom(Boolean source, Integer destination) {
            return source ? 1 : 0;
        }
    }

    static class intHolder {
        private Integer value;

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }
    }

    static class BooleanHolder {
        private Boolean value;

        public Boolean getValue() {
            return value;
        }

        public void setValue(Boolean value) {
            this.value = value;
        }
    }

    @Test
    public void testConvertUsingApiConfiguration() {
        DozerBeanMapper mapper = new DozerBeanMapper();
        mapper.setCustomConverters(Arrays.<CustomConverter> asList(new IntegerToBooleanConverter()));
        testConverter(mapper);
    }

    @Test
    public void testConvertUsingXmlConfiguration() {
        DozerBeanMapper mapper = new DozerBeanMapper(Arrays.asList("customConverterConfigurationTest.xml"));
        testConverter(mapper);
    }

    private void testConverter(Mapper mapper) {
        intHolder i = new intHolder();
        i.setValue(5);
        BooleanHolder booleanHolder = mapper.map(i, BooleanHolder.class);
        assertTrue(booleanHolder.getValue());

    }
}
