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
package org.dozer.vo.set;

import org.dozer.vo.BaseTestObject;

public class SomeDTO extends BaseTestObject {

    private Integer field1;

    private SomeOtherDTO[] field2;

    public Integer getField1() {
        return field1;
    }

    public void setField1(Integer field1) {
        this.field1 = field1;
    }

    public SomeOtherDTO[] getField2() {
        return field2;
    }

    public void setField2(SomeOtherDTO[] field2) {
        this.field2 = field2;
    }
}
