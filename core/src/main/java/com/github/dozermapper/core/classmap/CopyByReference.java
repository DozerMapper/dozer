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
package com.github.dozermapper.core.classmap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Only intended for internal use.
 */
public class CopyByReference {

    private static final String WILDCARD = "*";

    private String mask;
    private Pattern pattern;

    public CopyByReference(String mask) {
        this.mask = mask;
        pattern = compilePattern(mask);
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
        pattern = compilePattern(mask);
    }

    private Pattern compilePattern(String mask) {
        String regexp = StringUtils.replace(mask, ".", "\\.");
        regexp = StringUtils.replace(regexp, WILDCARD, ".*?");
        return Pattern.compile(regexp);
    }

    public boolean matches(String destFieldTypeName) {
        Matcher matcher = pattern.matcher(destFieldTypeName);
        return matcher.matches();
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
