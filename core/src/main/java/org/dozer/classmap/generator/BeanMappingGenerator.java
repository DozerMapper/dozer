package org.dozer.classmap.generator;

import org.dozer.classmap.ClassMap;
import org.dozer.classmap.ClassMapBuilder;
import org.dozer.classmap.Configuration;
import org.dozer.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
* @author Dmitry Spikhalskiy
* @since 27.11.12
*/
public class BeanMappingGenerator implements ClassMapBuilder.ClassMappingGenerator {

  static final List<BeanFieldsDetector> fieldDetectors = new ArrayList<BeanFieldsDetector>() {{
    add(new JavaBeanFieldsDetector());
  }};


  public boolean accepts(ClassMap classMap) {
    return true;
  }

  public boolean apply(ClassMap classMap, Configuration configuration) {
    Class<?> srcClass = classMap.getSrcClassToMap();
    Class<?> destClass = classMap.getDestClassToMap();

    Set<String> destFieldNames = getAcceptsFieldsDetector(destClass).getWritableFieldNames(destClass);
    Set<String> srcFieldNames = getAcceptsFieldsDetector(srcClass).getReadableFieldNames(srcClass);
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

  private static BeanFieldsDetector getAcceptsFieldsDetector(Class<?> clazz) {
    for (BeanFieldsDetector detector : fieldDetectors) {
      if (detector.accepts(clazz)) return detector;
    }
    return null;
  }

  protected interface BeanFieldsDetector {
    boolean accepts(Class<?> clazz);
    Set<String> getReadableFieldNames(Class<?> clazz);
    Set<String> getWritableFieldNames(Class<?> clazz);
  }
}
