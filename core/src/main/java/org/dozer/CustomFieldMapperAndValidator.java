package org.dozer;

/**
 * Public custom field validator and mapper interface.
 * Implement this interface on your custom field mapper if you want to implement both CustomFieldMapper and CustomFieldValidator functions
 *
 * @author Gilbert Grant
 */
public interface CustomFieldMapperAndValidator extends CustomFieldMapper, CustomFieldValidator {}
