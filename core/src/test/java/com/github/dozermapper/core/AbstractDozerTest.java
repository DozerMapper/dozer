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

import java.util.Random;

import com.github.dozermapper.core.config.SettingsDefaults;
import com.github.dozermapper.core.config.SettingsKeys;
import com.github.dozermapper.core.util.DozerConstants;

import org.junit.Before;

public abstract class AbstractDozerTest {

    private static Random rand = new Random(System.currentTimeMillis());

    @Before
    public void setUp() throws Exception {
        System.setProperty("log4j.debug", "true");
        System.setProperty(DozerConstants.DEBUG_SYS_PROP, "true");
        System.setProperty(SettingsKeys.CONFIG_FILE_SYS_PROP, SettingsDefaults.LEGACY_PROPERTIES_FILE);
    }

    protected String getRandomString() {
        return String.valueOf(rand.nextInt());
    }
}
