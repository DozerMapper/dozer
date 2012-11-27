package org.dozer.classmap.generator;

import org.dozer.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Dmitry Spikhalskiy
 * @since 27.11.12
 */
public class JavaBeanFieldsDetector implements BeanMappingGenerator.BeanFieldsDetector {
  public boolean accepts(Class<?> clazz) {
    return true;
  }

  public Set<String> getReadableFieldNames(Class<?> clazz) {
    Set<String> srcFieldNames = new HashSet<String>();
    PropertyDescriptor[] srcProperties = ReflectionUtils.getPropertyDescriptors(clazz);
    for (PropertyDescriptor srcPropertyDescriptor : srcProperties) {
      String fieldName = srcPropertyDescriptor.getName();

      if (srcPropertyDescriptor.getReadMethod() == null) {
        continue;
      }

      srcFieldNames.add(fieldName);
    }
    return srcFieldNames;
  }

  public Set<String> getWritableFieldNames(Class<?> clazz) {
    Set<String> destFieldNames = new HashSet<String>();
    PropertyDescriptor[] destProperties = ReflectionUtils.getPropertyDescriptors(clazz);
    for (PropertyDescriptor destPropertyDescriptor : destProperties) {
      String fieldName = destPropertyDescriptor.getName();

      // If destination field does not have a write method, then skip
      if (destPropertyDescriptor.getWriteMethod() == null && ReflectionUtils.getNonVoidSetter(clazz, fieldName) == null) {
        continue;
      }

      destFieldNames.add(fieldName);
    }
    return destFieldNames;
  }
}
