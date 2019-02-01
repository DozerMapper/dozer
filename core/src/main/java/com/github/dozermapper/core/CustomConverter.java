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

/**
 * Public custom converter interface.
 * <p>
 * In the Dozer mapping file(s), you can add some XML to tell Dozer to use a custom converter for certain class A and
 * class B types. When a custom converter is specified for a class A and class B combination, Dozer will invoke the
 * custom converter to perform the data mapping instead of the standard mapping logic.
 * <p>
 * <a
 * href="https://dozermapper.github.io/gitbook/documentation/customconverter.html">
 * https://dozermapper.github.io/gitbook/documentation/customconverter.html</a>
 */
public interface CustomConverter {

    Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass);

}
