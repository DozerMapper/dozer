package org.dozer.classmap.generator;

/**
 * Defines the mapping style to use between the source and target objects.
 */
public enum MappingType {
    /** Map from source field to target field */
    FIELD_TO_FIELD,
    /** Map from source field to target setter */
    FIELD_TO_SETTER,
    /** Map from source getter to target field */
    GETTER_TO_FIELD,
    /** Map from source getter to target setter */
    GETTER_TO_SETTER
}
