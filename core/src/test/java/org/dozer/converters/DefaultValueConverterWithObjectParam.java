package org.dozer.converters;

import org.dozer.ConfigurableCustomConverter;
import org.dozer.vo.InsideTestObject;

/**
 * Created by mbruncic on 2/19/16.
 */
public class DefaultValueConverterWithObjectParam implements ConfigurableCustomConverter {
    private Object defaultValue;

    @Override
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass) {
        if (sourceFieldValue != null && sourceClass.equals(String.class)) {
            InsideTestObject insideTestObject = new InsideTestObject();
            insideTestObject.setLabel((String) sourceFieldValue);
            return insideTestObject;
        }else {
            return defaultValue;
        }
    }

    @Override
    public void setParameter(Object parameter) {
        this.defaultValue = parameter;
    }
}
