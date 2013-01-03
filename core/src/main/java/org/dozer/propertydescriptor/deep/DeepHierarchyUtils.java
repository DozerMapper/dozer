package org.dozer.propertydescriptor.deep;

import org.dozer.fieldmap.HintContainer;
import org.dozer.propertydescriptor.DozerPropertyDescriptor;
import org.dozer.propertydescriptor.PropertyDescriptorFactory;
import org.dozer.util.DozerConstants;
import org.dozer.util.MappingUtils;

import java.util.Collection;
import java.util.StringTokenizer;

/**
 * @author Dmitry Spikhalskiy
 * @since 04.01.13
 */
public class DeepHierarchyUtils {
  // Copy-paste from GetterSetterPropertyDescriptor
  public static Object getDeepSrcFieldValue(Object srcObj, String fieldName, boolean isIndexed, int index, HintContainer srcDeepIndexHintContainer) {
    // follow deep field hierarchy. If any values are null along the way, then return null
    Object parentObj = srcObj;
    Object hierarchyValue = parentObj;
    DozerDeepHierarchyElement[] hierarchy = getDeepFieldHierarchy(srcObj.getClass(), fieldName, srcDeepIndexHintContainer);
    int size = hierarchy.length;
    for (int i = 0; i < size; i++) {
      DozerDeepHierarchyElement hierarchyElement = hierarchy[i];
      DozerPropertyDescriptor pd = hierarchyElement.getPropDescriptor();
      // If any fields in the deep hierarchy are indexed, get actual value within the collection at the specified index
      if (hierarchyElement.getIndex() > -1) {
        hierarchyValue = MappingUtils.getIndexedValue(pd.getPropertyValue(hierarchyValue), hierarchyElement.getIndex());
      } else {
        hierarchyValue = pd.getPropertyValue(parentObj);
      }
      parentObj = hierarchyValue;
      if (hierarchyValue == null) {
        break;
      }

      // If dest field is indexed, get actual value within the collection at the specified index
      if (isIndexed) {
        hierarchyValue = MappingUtils.getIndexedValue(hierarchyValue, index);
      }
    }

    return hierarchyValue;
  }

  private static DozerDeepHierarchyElement[] getDeepFieldHierarchy(Class<?> parentClass, String field, HintContainer deepIndexHintContainer) {
    if (!MappingUtils.isDeepMapping(field)) {
      MappingUtils.throwMappingException("Field does not contain deep field delimitor");
    }

    StringTokenizer toks = new StringTokenizer(field, DozerConstants.DEEP_FIELD_DELIMITER);
    Class<?> latestClass = parentClass;
    DozerDeepHierarchyElement[] hierarchy = new DozerDeepHierarchyElement[toks.countTokens()];
    int index = 0;
    int hintIndex = 0;
    while (toks.hasMoreTokens()) {
      String aFieldName = toks.nextToken();
      String theFieldName = aFieldName;
      int collectionIndex = -1;

      if (aFieldName.contains("[")) {
        theFieldName = aFieldName.substring(0, aFieldName.indexOf("["));
        collectionIndex = Integer.parseInt(aFieldName.substring(aFieldName.indexOf("[") + 1, aFieldName.indexOf("]")));
      }

      DozerPropertyDescriptor propDescriptor = PropertyDescriptorFactory.getPropertyDescriptor(latestClass, null, null, null, null, false,
              collectionIndex != -1, collectionIndex, theFieldName, null,
              false, null, null, null, null); //null as hint containers now

      DozerDeepHierarchyElement r = new DozerDeepHierarchyElement(propDescriptor, collectionIndex);

      if (propDescriptor == null) {
        MappingUtils.throwMappingException("Exception occurred determining deep field hierarchy for Class --> "
                + parentClass.getName() + ", Field --> " + field + ".  Unable to determine property descriptor for Class --> "
                + latestClass.getName() + ", Field Name: " + aFieldName);
      }

      latestClass = propDescriptor.getPropertyType();
      if (toks.hasMoreTokens()) {
        if (latestClass.isArray()) {
          latestClass = latestClass.getComponentType();
        } else if (Collection.class.isAssignableFrom(latestClass)) {
          Class<?> genericType = determineGenericsType(propDescriptor);

          if (genericType == null && deepIndexHintContainer == null) {
            MappingUtils
                    .throwMappingException("Hint(s) or Generics not specified.  Hint(s) or Generics must be specified for deep mapping with indexed field(s). Exception occurred determining deep field hierarchy for Class --> "
                            + parentClass.getName()
                            + ", Field --> "
                            + field
                            + ".  Unable to determine property descriptor for Class --> "
                            + latestClass.getName() + ", Field Name: " + aFieldName);
          }
          if (genericType != null) {
            latestClass = genericType;
          } else {
            latestClass = deepIndexHintContainer.getHint(hintIndex);
            hintIndex += 1;
          }
        }
      }
      hierarchy[index++] = r;
    }

    return hierarchy;
  }
}
