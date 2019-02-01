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
package com.github.dozermapper.core.vo;

public class InsideTestObjectPrime extends BaseTestObject {

    private String labelPrime;
    private MyPrimitiveWrapper wrapper;
    private MyPrimitiveWrapper anotherWrapper;
    private String createdByFactoryName;
    private NoExtendBaseObject copyByReference;
    private String deepInterfaceString;
    private String myField;

    public InsideTestObjectPrime() {
        super();
    }

    public MyPrimitiveWrapper getWrapper() {
        return wrapper;
    }

    public void setWrapper(MyPrimitiveWrapper wrapper) {
        this.wrapper = wrapper;
    }

    public String getLabelPrime() {
        return labelPrime;
    }

    public void setLabelPrime(String labelPrime) {
        this.labelPrime = labelPrime;
    }

    public MyPrimitiveWrapper getAnotherWrapper() {
        return anotherWrapper;
    }

    public void setAnotherWrapper(MyPrimitiveWrapper anotherWrapper) {
        this.anotherWrapper = anotherWrapper;
    }

    public String getCreatedByFactoryName() {
        return createdByFactoryName;
    }

    public void setCreatedByFactoryName(String createdByFactoryName) {
        this.createdByFactoryName = createdByFactoryName;
    }

    public NoExtendBaseObject getCopyByReference() {
        return copyByReference;
    }

    public void setCopyByReference(NoExtendBaseObject copyByReference) {
        this.copyByReference = copyByReference;
    }

    public static InsideTestObjectPrime createMethod() {
        InsideTestObjectPrime itop = new InsideTestObjectPrime();
        itop.setMyField("myField");
        return itop;
    }

    public String getMyField() {
        return myField;
    }

    public void setMyField(String myField) {
        this.myField = myField;
    }

    public String getDeepInterfaceString() {
        return deepInterfaceString;
    }

    public void setDeepInterfaceString(String deepInterfaceString) {
        this.deepInterfaceString = deepInterfaceString;
    }
}
