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
import org.dozer.vo.inheritance.A;
import org.dozer.vo.inheritance.B;
import org.dozer.vo.inheritance.C;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterfaceABCTest extends AbstractFunctionalTest {
    private static Logger log = LoggerFactory.getLogger(InterfaceABCTest.class);

    @Test
    public void testInterface() throws Exception {
        log.info("Starting");
        mapper = getMapper("mappings/interfaceMapping2.xml");

        A a = new  A();
        a.setField1("Field with error?");
        a.setFieldA("Field to test");
        B b = new  B();
        C c = new  C();

        mapper.map(a, c);
        assert a.getFieldA().equals(c.getFieldA());
        mapper.map(a, b);
        assert a.getField1().equals(b.getField1());
    }
}
