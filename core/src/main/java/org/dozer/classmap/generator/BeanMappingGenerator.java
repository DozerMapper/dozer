package org.dozer.classmap.generator;

import org.dozer.classmap.ClassMap;
import org.dozer.classmap.ClassMapBuilder;
import org.dozer.classmap.Configuration;
import org.dozer.util.CollectionUtils;
import org.dozer.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

/**
* @author Dmitry Spikhalskiy
* @since 27.11.12
*/
public class BeanMappingGenerator implements ClassMapBuilder.ClassMappingGenerator {

  public boolean accepts(ClassMap classMap) {
    return true;
  }

  public boolean apply(ClassMap classMap, Configuration configuration) {
    Class<?> srcClass = classMap.getSrcClassToMap();
    Class<?> destClass = classMap.getDestClassToMap();

    Set<String> destFieldNames = new HashSet<String>();
    PropertyDescriptor[] destProperties = ReflectionUtils.getPropertyDescriptors(destClass);
    for (PropertyDescriptor destPropertyDescriptor : destProperties) {
      String fieldName = destPropertyDescriptor.getName();

      // If destination field does not have a write method, then skip
      if (destPropertyDescriptor.getWriteMethod() == null && ReflectionUtils.getNonVoidSetter(destClass, fieldName) == null) {
        continue;
      }

      destFieldNames.add(fieldName);
    }

    Set<String> srcFieldNames = new HashSet<String>();
    PropertyDescriptor[] srcProperties = ReflectionUtils.getPropertyDescriptors(srcClass);
    for (PropertyDescriptor srcPropertyDescriptor : srcProperties) {
      String fieldName = srcPropertyDescriptor.getName();

      if (srcPropertyDescriptor.getReadMethod() == null) {
        continue;
      }

      srcFieldNames.add(fieldName);
    }

    Set<String> commonFieldNames = CollectionUtils.intersection(srcFieldNames, destFieldNames);

    for (String fieldName : commonFieldNames) {
      if (GeneratorUtils.shouldIgnoreField(fieldName, srcClass, destClass)) {
        continue;
      }

      // If field has already been accounted for, then skip
      if (classMap.getFieldMapUsingDest(fieldName) != null || classMap.getFieldMapUsingSrc(fieldName) != null) {
        continue;
      }

      GeneratorUtils.addGenericMapping(classMap, configuration, fieldName, fieldName);
    }
    return false;
  }
}
