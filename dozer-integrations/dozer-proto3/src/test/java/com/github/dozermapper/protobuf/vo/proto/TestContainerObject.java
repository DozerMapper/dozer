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
package com.github.dozermapper.protobuf.vo.proto;

public class TestContainerObject {
    public enum TestContainedEnum {
        VALUE1, VALUE2;
    }
    
    public static class TestContainedObject{
        private String c1;
        
        public TestContainedObject() {
            
        }
        
        public String getC1() {
            return c1;
        }
        
        public void setC1(String c1) {
            this.c1 = c1;
        }
    }
    
    private TestContainedObject obj1;
    private TestContainedEnum enum1;
    
    public TestContainerObject() {
    }
    
    public TestContainedEnum getEnum1() {
        return enum1;
    }
    
    public TestContainedObject getObj1() {
        return obj1;
    }
    
    public void setEnum1(TestContainedEnum enum1) {
        this.enum1 = enum1;
    }
    
    public void setObj1(TestContainedObject obj1) {
        this.obj1 = obj1;
    }
}
