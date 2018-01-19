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
package org.dozer.functional_tests.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.dozer.DozerBeanMapperBuilder;
import org.dozer.Mapper;
import org.dozer.MappingProcessor;
import org.dozer.classmap.RelationshipType;
import org.dozer.loader.api.BeanMappingBuilder;
import org.dozer.loader.api.FieldsMappingOptions;
import org.dozer.loader.api.TypeMappingOptions;
import org.junit.Assert;
import org.junit.Test;

/**
 * This test is to verify that the builder with the specified mapid is being used for the objects within a collection contained within an object which is being mapped.
 * 
 * There appeared to be an issue in {@link MappingProcessor} for non_cumulative Sets and Lists where mapToDestObject is
 * being called without passing the configured mapId of the object being mapped resulting in the default behaviour being
 * used rather than the configuration specific by the builder that has been defined against the mapid. 
 * 
 * @author Martin Ball
 * 
 */
public class DeepMappingWithMapIdTest {

    private static final String MAP_ID_PATENT = "mapIdParent";
    private static final String MAP_ID_CHILD = "mapIdChild";


    @Test
    public void testMappingListOfObjectsWithMapId() {
        // Our src object created with a collection of children one of which will have a null field,
        // the destination will contain a matching child which does not contain a null
        // value which we don't want to overwrite.
        Child srcChild1 = new Child(1, "F1", null);
        ParentWithChildList src = new ParentWithChildList(srcChild1, new Child(2, "F2", "L2"));
        ParentWithChildList dest = new ParentWithChildList(new Child(1, "F1Origial", "L1"), new Child(2, "F2Original", "L2Original"));       
        
        //Double check to make sure that the field is null, otherwise our test would be invalid.
        Assert.assertNull(srcChild1.getLastName());
        Assert.assertEquals(new Integer(1), srcChild1.getId());

        Mapper mapper = DozerBeanMapperBuilder.create()
                .withMappingBuilder(getParentMapping(ParentWithChildList.class))
                .withMappingBuilder(getChildMapping())
                .build();
        mapper.map(src, dest, MAP_ID_PATENT);
         
        checkResults(src, dest);
    }

    @Test
    public void testMappingSetOfObjectsWithMapId() {

        // Our src object created with a collection of children one of which will have a null field,
        // the destination will contain a matching child which does not contain a null
        // value which we don't want to overwrite.
        Child srcChild1 = new Child(1, "F1", null);
        ParentWithChildSet src = new ParentWithChildSet(srcChild1, new Child(2, "F2", "L2"));
        ParentWithChildSet dest = new ParentWithChildSet(new Child(1, "F1Origial", "L1"), new Child(2, "F2Original", "L2Original"));
        
        //Double check to make sure that the field is null, otherwise our test would be invalid.
        Assert.assertNull(srcChild1.getLastName());
        Assert.assertEquals(new Integer(1), srcChild1.getId());
                
        //Perform the mapping
        Mapper mapper = DozerBeanMapperBuilder.create()
                .withMappingBuilder(getParentMapping(ParentWithChildSet.class))
                .withMappingBuilder(getChildMapping())
                .build();
        mapper.map(src, dest, MAP_ID_PATENT);
                
        checkResults(src, dest);        
    }

    /**
     * Get the parent mapping for to use for the test
     * @param clazz
     * @return
     */
    private BeanMappingBuilder getParentMapping(final Class<?> clazz) {
        return new BeanMappingBuilder() {
            protected void configure() {
                mapping(
                        clazz, 
                        clazz, 
                        TypeMappingOptions.mapId(MAP_ID_PATENT)
                ).fields(
                        "children", 
                        "children",
                        FieldsMappingOptions.collectionStrategy(true, RelationshipType.NON_CUMULATIVE),
                        FieldsMappingOptions.useMapId(MAP_ID_CHILD)
                );
            }
        };
    }

    /**
     * Get the child mapping to use for the test, it is configured not to map nulls.
     * @return
     */
    private BeanMappingBuilder getChildMapping() {
        return new BeanMappingBuilder() {
            protected void configure() {
                mapping(
                        Child.class, 
                        Child.class,
                        TypeMappingOptions.mapId(MAP_ID_CHILD), 
                        TypeMappingOptions.mapNull(false)
                );
            }
        };
    }

    private void checkResults(Parent<? extends Collection<Child>> src, Parent<? extends Collection<Child>> dest) {
        Child srcChild1;
        Child srcChild2;
        //Because this is a non_cumulative mapping we expect that the resulting List is the same size as it was originally.
        Assert.assertEquals(dest.getChildren().size(), 2);
        
        // Because our child objects are comparable we know that either we have a list in the order we created if or a
        // sorted set so can iterate them to perform comparison.
        Iterator<Child> srcChildIter = src.getChildren().iterator();
        srcChild1 = srcChildIter.next();
        srcChild2 = srcChildIter.next();
        
        Iterator<Child> destChildIter = dest.getChildren().iterator();
        Child destChild1 = destChildIter.next();
        Child destChild2 = destChildIter.next();
        
        Assert.assertEquals(new Integer(1), destChild1.getId());
        Assert.assertEquals(srcChild1.getFirstName(), destChild1.getFirstName());
        //The lastname of the destination child should not have changed because the src had a null field and we have defined mapNull(false) in the childMapping builder.
        Assert.assertEquals("L1", destChild1.getLastName());

        Assert.assertEquals(new Integer(2), destChild2.getId());
        Assert.assertEquals(srcChild2.getFirstName(), destChild2.getFirstName());
        Assert.assertEquals(srcChild2.getLastName(), destChild2.getLastName());
    }
    
    public static class ParentWithChildList implements Parent<List<Child>>{
        /**
         * The list of children which we are expecting to be mapped with the BeanMappingBuilder defined by getChildMapping(), which should not map nulls in the child;
         */
        List<Child> children = new ArrayList<>();
        
        public ParentWithChildList(Child child1, Child child2) {
            super();
            children.add(child1);
            children.add(child2);
        }

        @Override
        public List<Child> getChildren() {
            return children;
        }

        @Override
        public void setChildren(List<Child> children) {
            this.children = children;
        }
    }

    public static class ParentWithChildSet implements Parent<Set<Child>>{
        /**
         * The set of children which we are expecting to be mapped with the BeanMappingBuilder defined by
         * getChildMapping(), which should not map nulls in the child. Using a sorted set so that we can iterate over
         * the values and know they are in id order, which means both src and desc sets can easily be compared
         */
        Set<Child> children = new TreeSet();
        
        public ParentWithChildSet(Child child1, Child child2) {
            super();
            children.add(child1);
            children.add(child2);
        }
        @Override
        public Set<Child> getChildren() {
            return children;
        }

        @Override
        public void setChildren(Set<Child> children) {
            this.children = children;
        }
    }

    public interface Parent<T extends Collection<Child>> {

        T getChildren();

        void setChildren(T children);
    }
    
    public static class Child implements Comparable<Child>{
        public Integer id;
        public String firstName;
        public String lastName;
        
        public Child() {
            super();
        }
        
        public Child(Integer id, String firstName, String lastName ) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        @Override
        public int hashCode() {
            return getId().hashCode();
        }

        /**
         * Implemented equals so that the mapping can correctly determine if the child objects in the list represent the
         * same object. Required as per the api documentation.
         */
        @Override
        public boolean equals(Object obj) {
            boolean equals = false;
            if (obj != null && this.getClass().isAssignableFrom(obj.getClass())) {
                equals = this.getId().equals(((Child) obj).getId());
            }
            return equals;
        }

        @Override
        public int compareTo(Child child) {
           return getId().compareTo(child.getId());
        }

    }

}
