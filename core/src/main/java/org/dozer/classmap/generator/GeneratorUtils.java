package org.dozer.classmap.generator;

import org.dozer.classmap.ClassMap;
import org.dozer.classmap.Configuration;
import org.dozer.fieldmap.DozerField;
import org.dozer.fieldmap.FieldMap;
import org.dozer.fieldmap.GenericFieldMap;
import org.dozer.util.MappingUtils;

/**
 * @author Dmitry Spikhalskiy
 * @since 27.11.12
 */
public final class GeneratorUtils {
  private static final String CLASS = "class";
  private static final String CALLBACK = "callback";
  private static final String CALLBACKS = "callbacks";

  private GeneratorUtils() {}

  public static boolean shouldIgnoreField(String fieldName, Class<?> srcType, Class<?> destType) {
    if (CLASS.equals(fieldName)) {
      return true;
    }
    if ((CALLBACK.equals(fieldName) || CALLBACKS.equals(fieldName))
            && (MappingUtils.isProxy(srcType) || MappingUtils.isProxy(destType))) {
      return true;
    }
    return false;
  }

  public static void addGenericMapping(ClassMap classMap, Configuration configuration, String srcName, String destName) {
    FieldMap fieldMap = new GenericFieldMap(classMap);

    fieldMap.setSrcField(new DozerField(srcName, null));
    fieldMap.setDestField(new DozerField(destName, null));

    // add CopyByReferences per defect #1728159
    MappingUtils.applyGlobalCopyByReference(configuration, fieldMap, classMap);
    classMap.addFieldMapping(fieldMap);
  }
}
