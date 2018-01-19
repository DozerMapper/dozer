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
package org.dozer.functional_tests.runner;

import org.dozer.functional_tests.DataObjectInstantiator;

/**
 * @author Dmitry Buzdin
 */
public final class InstantiatorHolder {

    private InstantiatorHolder() {

    }

    private static final ThreadLocal<DataObjectInstantiator> value = new ThreadLocal<DataObjectInstantiator>() {{
        set(NoProxyDataObjectInstantiator.INSTANCE);
    }};

    public static void set(DataObjectInstantiator instance) {
        value.set(instance);
    }

    public static DataObjectInstantiator get() {
        return value.get();
    }
}
