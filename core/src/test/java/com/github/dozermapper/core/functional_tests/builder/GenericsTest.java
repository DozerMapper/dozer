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
package com.github.dozermapper.core.functional_tests.builder;

import java.util.ArrayList;
import java.util.List;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.loader.api.BeanMappingBuilder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class GenericsTest {

    @Test
    public void shouldDetermineCollectionTypeViaFieldGenericType() {
        Mapper mapper = DozerBeanMapperBuilder.create()
                .withMappingBuilder(new BeanMappingBuilder() {
                    @Override
                    protected void configure() {
                        mapping(Container.class, ContainerDTO.class)
                                .fields(field("items").accessible(true), "items");
                    }
                })
                .build();

        Container container = prepareContainer();

        Container containerSpy = spy(container);
        when(containerSpy.getItems()).thenThrow(new IllegalStateException());

        ContainerDTO result = mapper.map(containerSpy, ContainerDTO.class);

        assertDto(result);
    }

    @Test
    public void shouldDetermineCollectionTypeViaGetter() {
        Mapper mapper = DozerBeanMapperBuilder.create()
                .withMappingBuilder(new BeanMappingBuilder() {
                    @Override
                    protected void configure() {
                        mapping(Container.class, ContainerDTO.class)
                                .fields("items", "items");
                    }
                })
                .build();

        Container container = prepareContainer();

        ContainerDTO result = mapper.map(container, ContainerDTO.class);

        assertDto(result);
    }

    private void assertDto(ContainerDTO result) {
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals("A", result.getItems().get(0).getId());
    }

    private Container prepareContainer() {
        Container container = new Container();
        ArrayList<Item> items = new ArrayList<>();
        Item item = new Item();
        item.setId("A");
        items.add(item);
        container.items = items;
        return container;
    }

    public static class Container {
        List<Item> items;

        public List<Item> getItems() {
            return items;
        }

    }

    public static class ContainerDTO {
        private List<ItemDTO> items;

        public List<ItemDTO> getItems() {
            return items;
        }

        public void setItems(List<ItemDTO> items) {
            this.items = items;
        }
    }

    public static class Item {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class ItemDTO {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
