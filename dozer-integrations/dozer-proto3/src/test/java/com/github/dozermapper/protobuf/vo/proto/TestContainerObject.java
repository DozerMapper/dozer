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
