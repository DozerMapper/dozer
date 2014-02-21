/**
 * Copyright 2005-2013 Dozer Project
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
package org.dozer.functional_tests.annotation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.dozer.Mapping;
import org.dozer.functional_tests.AbstractFunctionalTest;
import org.junit.Test;

/**
 * @author mwyraz
 */
public class AnnotationMappingMapsTest extends AbstractFunctionalTest
{
    public static class AnnotatedObjectWithFields
    {
        @Mapping(key="prop1")
        public String prop1;
        
        @Mapping(key="otherNameForProp2")
        public String prop2;
    }

    public static class AnnotatedObjectWithProperties
    {
        private String prop1;
        
        private String prop2;
        
        @Mapping(key="prop1")
        public String getProp1()
        {
            return prop1;
        }
        public void setProp1(String prop1)
        {
            this.prop1 = prop1;
        }
        @Mapping(key="otherNameForProp2")
        public String getProp2()
        {
            return prop2;
        }
        public void setProp2(String prop2)
        {
            this.prop2 = prop2;
        }
        
    }
    
    @Test
    public void testMapForwardMapping_Fields()
    {
        AnnotatedObjectWithFields source=new AnnotatedObjectWithFields();
        source.prop1="value1";
        source.prop2="value2";
        
        @SuppressWarnings("unchecked")
        Map<String,String> result=mapper.map(source, HashMap.class);
        
        assertThat(result.get("prop1"), equalTo("value1"));
        assertThat(result.get("otherNameForProp2"), equalTo("value2"));
        assertThat(result.size(), equalTo(2));
    }

    @Test
    public void testMapReverseMapping_Fields()
    {
        Map<String,String> source=new HashMap<String, String>();
        source.put("prop1", "value1");
        source.put("otherNameForProp2", "value2");
        source.put("prop2", "value3");
        
        AnnotatedObjectWithFields result=mapper.map(source, AnnotatedObjectWithFields.class);
        assertThat(result.prop1, equalTo("value1"));
        assertThat(result.prop2, equalTo("value2"));
    }
    
    
    @Test
    public void testMapForwardMapping_Properties()
    {
        AnnotatedObjectWithProperties source=new AnnotatedObjectWithProperties();
        source.prop1="value1";
        source.prop2="value2";
        
        @SuppressWarnings("unchecked")
        Map<String,String> result=mapper.map(source, HashMap.class);
        
        assertThat(result.get("prop1"), equalTo("value1"));
        assertThat(result.get("otherNameForProp2"), equalTo("value2"));
        assertThat(result.size(), equalTo(2));
    }

    @Test
    public void testMapReverseMapping_Properties()
    {
        Map<String,String> source=new HashMap<String, String>();
        source.put("prop1", "value1");
        source.put("otherNameForProp2", "value2");
        source.put("prop2", "value3");
        
        AnnotatedObjectWithProperties result=mapper.map(source, AnnotatedObjectWithProperties.class);
        assertThat(result.getProp1(), equalTo("value1"));
        assertThat(result.getProp2(), equalTo("value2"));
    }
    
    
}
