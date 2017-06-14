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
package org.dozer.functional_tests;

import org.dozer.vo.iface.ApplicationUser;
import org.dozer.vo.iface.Subscriber;
import org.dozer.vo.iface.UpdateMember;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterfacePerformanceTest extends AbstractFunctionalTest {
    private static Logger log = LoggerFactory.getLogger(InterfacePerformanceTest.class);

    @Test
    public void testInterface() throws Exception {
        log.info("Starting");
        mapper = getMapper("mappings/interfaceMapping.xml");
        { // warm up to load the config
            ApplicationUser source = new ApplicationUser();
            UpdateMember target = new UpdateMember();
            mapper.map(source, target);
        }

        for (int j = 1; j <= 16384; j += j) {
            long start = System.currentTimeMillis();
            for (int i = 0; i < j; i++) {
                ApplicationUser source = new ApplicationUser();
                UpdateMember target = new UpdateMember();
                mapper.map(source, target);
            }

            long applicationUserTime = System.currentTimeMillis() - start;
            start = System.currentTimeMillis();
            for (int i = 0; i < j; i++) {
                Subscriber source = new Subscriber();
                UpdateMember target = new UpdateMember();
                mapper.map(source, target);
            }

            long subscriberTime = System.currentTimeMillis() - start;
            log.debug("Execution of "
                      + j
                      + " iterations times ApplicationUser = "
                      + applicationUserTime + " Subscriber = "
                      + subscriberTime);
        }
    }
}
