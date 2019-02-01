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
package com.github.dozermapper.core.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.dozermapper.core.OptionValue;

/**
 * Override mapping options at class level.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface MappingOptions {

    /**
     * The wildcard policy for the class mapping
     *
     * @return option is on or off
     */
    OptionValue wildCard() default OptionValue.INHERITED;

    /**
     * The wildcard case sensitivity policy for the class mapping.
     * If this policy is ON, wildcard fields will be matched ignoring the case.
     *
     * @return option is on or off
     */
    OptionValue wildCardCaseInsensitive() default OptionValue.INHERITED;

    /**
     * The error handling policy for the class mapping
     *
     * @return option is on or off
     */
    OptionValue stopOnErrors() default OptionValue.INHERITED;

    /**
     * The bypass null policy for the class mapping.
     * If this policy is OFF, the dest field mapping is bypassed at runtime and the destination value
     * setter method will not be called if the src value is null.
     *
     * @return option is on or off
     */
    OptionValue mapNull() default OptionValue.INHERITED;

    /**
     * The bypass empty string policy for the class mapping.
     * If this policy is OFF, the dest field mapping is bypassed at runtime and the destination value
     * setter method will not be called if the src value is an empty String.
     *
     * @return option is on or off
     */
    OptionValue mapEmptyString() default OptionValue.INHERITED;

    /**
     * The date format for the class mapping.
     *
     * @return option is on or off
     */
    String dateFormat() default "";
}
