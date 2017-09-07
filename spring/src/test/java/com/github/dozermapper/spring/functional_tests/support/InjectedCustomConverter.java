/*
 * Copyright 2005-2017 Dozer Project
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
package com.github.dozermapper.spring.functional_tests.support;

import com.github.dozermapper.spring.vo.Destination;
import com.github.dozermapper.spring.vo.Source;

import org.dozer.DozerConverter;

public class InjectedCustomConverter extends DozerConverter<Source, Destination> {

    private String injectedName;

    public InjectedCustomConverter() {
        super(Source.class, Destination.class);
    }

    @Override
    public Destination convertTo(Source source, Destination destination) {
        Destination result = new Destination();
        result.setValue(injectedName);
        return result;
    }

    @Override
    public Source convertFrom(Destination source, Source destination) {
        return null;
    }

    public String getInjectedName() {
        return injectedName;
    }

    public void setInjectedName(String injectedName) {
        this.injectedName = injectedName;
    }
}
