package org.dozer;

import org.dozer.classmap.ClassMap;
import org.dozer.fieldmap.FieldMap;

/**
 * Public custom field validator interface. A custom field validator is usually in conjunction with a CustomFieldMapper
 * when implementing the CustomFieldMapperAndValidator interface.
 * Use this when you want logic to prevent source field values being fetched
 * e.g. when checking lazy loaded relationships in an ORM
 *
 * <p>
 * If a custom field validator is specified, Dozer will invoke this class when performing all field mappings. If false is
 * returned from the call the source field value will never be fetched, and subsequently the field will never be mapped.
 *
 * @author Gilbert Grant
 */
public interface CustomFieldValidator {
    boolean canMapField(Object source, Object destination, ClassMap classMap, FieldMap fieldMapping);
}
