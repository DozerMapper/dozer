/*
 * Copyright 2005-2019 Dozer Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.dozermapper.core;

import org.apache.commons.lang3.ClassUtils;

/**
 * This class should be extended in order to implement new Custom Converters for value transformation.
 */
public abstract class DozerConverter<A, B> implements ConfigurableCustomConverter {

    private ThreadLocal<String> parameter = new ThreadLocal<>();
    private Class<A> prototypeA;
    private Class<B> prototypeB;

    /**
     * Defines two types, which will take part transformation.
     * As Dozer supports bi-directional mapping it is not known which of the classes is source and
     * which is destination. It will be decided in runtime.
     *
     * @param prototypeA type one
     * @param prototypeB type two
     */
    public DozerConverter(Class<A> prototypeA, Class<B> prototypeB) {
        this.prototypeA = prototypeA;
        this.prototypeB = prototypeB;
    }

    // Method first checks exact type matches and only then checks for assignement
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass) {
        Class<?> wrappedDestinationClass = ClassUtils.primitiveToWrapper(destinationClass);
        Class<?> wrappedSourceClass = ClassUtils.primitiveToWrapper(sourceClass);

        if (prototypeA.equals(wrappedDestinationClass)) {
            return convertFrom((B)sourceFieldValue, (A)existingDestinationFieldValue);
        } else if (prototypeB.equals(wrappedDestinationClass)) {
            return convertTo((A)sourceFieldValue, (B)existingDestinationFieldValue);
        } else if (prototypeA.equals(wrappedSourceClass)) {
            return convertTo((A)sourceFieldValue, (B)existingDestinationFieldValue);
        } else if (prototypeB.equals(wrappedSourceClass)) {
            return convertFrom((B)sourceFieldValue, (A)existingDestinationFieldValue);
        } else if (prototypeA.isAssignableFrom(wrappedDestinationClass)) {
            return convertFrom((B)sourceFieldValue, (A)existingDestinationFieldValue);
        } else if (prototypeB.isAssignableFrom(wrappedDestinationClass)) {
            return convertTo((A)sourceFieldValue, (B)existingDestinationFieldValue);
        } else if (prototypeA.isAssignableFrom(wrappedSourceClass)) {
            return convertTo((A)sourceFieldValue, (B)existingDestinationFieldValue);
        } else if (prototypeB.isAssignableFrom(wrappedSourceClass)) {
            return convertFrom((B)sourceFieldValue, (A)existingDestinationFieldValue);
        } else {
            throw new MappingException("Destination Type (" + wrappedDestinationClass.getName()
                                       + ") is not accepted by this Custom Converter ("
                                       + this.getClass().getName() + ")!");
        }

    }

    /**
     * Converts the source field to the destination field and return the resulting destination
     * value.
     *
     * @param source      the value of the source field
     * @param destination the current value of the destination field (or null)
     * @return the resulting value for the destination field
     */
    public abstract B convertTo(A source, B destination);

    /**
     * Converts the source field to the destination field and return the resulting destination
     * value.
     *
     * @param source the value of the source field
     * @return the resulting value for the destination field
     */
    public B convertTo(A source) {
        return convertTo(source, null);
    }

    /**
     * Converts the source field to the destination field and return the resulting destination
     * value
     *
     * @param source      the value of the source field
     * @param destination the current value of the destination field (or null)
     * @return the resulting value for the destination field
     */
    public abstract A convertFrom(B source, A destination);

    /**
     * Converts the source field to the destination field and return the resulting destination
     * value
     *
     * @param source the value of the source field
     * @return the resulting value for the destination field
     */
    public A convertFrom(B source) {
        return convertFrom(source, null);
    }

    /**
     * Sets the configured parameter value for this converter instance.
     * Should be called by Dozer internally before actual mapping.
     *
     * @param parameter configured parameter value
     */
    public void setParameter(String parameter) {
        this.parameter.set(parameter);
    }

    /**
     * Retrieves the static parameter configured for this particular converter instance.
     * It is not advisable to call this method from converter constructor as the parameter is not yet there.
     *
     * @return parameter value
     * @throws IllegalStateException if parameter has not been set yet.
     */
    public String getParameter() {
        String theParameter = parameter.get();
        if (theParameter == null) {
            throw new IllegalStateException("Custom Converter Parameter has not yet been set!");
        }
        return theParameter;
    }

}
