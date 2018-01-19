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

import java.util.Date;

import org.dozer.vo.BaseTestObject;

public class SomeOtherDTO extends BaseTestObject {

    private Integer otherField1;
    private SomeDTO otherField2;
    private String otherField3;
    private Date otherField4;

    public Integer getOtherField1() {
        return otherField1;
    }

    public void setOtherField1(Integer otherField1) {
        this.otherField1 = otherField1;
    }

    public SomeDTO getOtherField2() {
        return otherField2;
    }

    public void setOtherField2(SomeDTO otherField2) {
        this.otherField2 = otherField2;
    }

    public String getOtherField3() {
        return otherField3;
    }

    public void setOtherField3(String otherField3) {
        this.otherField3 = otherField3;
    }

    public Date getOtherField4() {
        return otherField4;
    }

    public void setOtherField4(Date otherField4) {
        this.otherField4 = otherField4;
    }
}
