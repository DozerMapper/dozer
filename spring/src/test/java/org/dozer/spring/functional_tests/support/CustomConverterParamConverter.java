package org.dozer.spring.functional_tests.support;

import org.dozer.ConfigurableCustomConverter;

/**
 * @author Dmitry Buzdin
 */
public class CustomConverterParamConverter implements ConfigurableCustomConverter {

  private String param;

  public Object convert(Object existingDestinationFieldValue,
                        Object sourceFieldValue,
                        Class<?> destinationClass,
                        Class<?> sourceClass) {
    String source = null;
    Object dest = null;
    if (String.class.isAssignableFrom(sourceClass)) {
      source = (String) sourceFieldValue;
    }

    if (String.class.isAssignableFrom(destinationClass)) {
      dest = source + "-" + param;
    }

    return dest;
  }

  public void setParameter(String parameter) {
    param = parameter;
  }

}