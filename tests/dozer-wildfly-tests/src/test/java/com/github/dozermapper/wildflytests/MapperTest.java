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
package com.github.dozermapper.wildflytests;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.classmap.MappingDirection;
import com.github.dozermapper.core.loader.api.BeanMappingBuilder;
import com.github.dozermapper.osgitestsmodel.Person;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class MapperTest {

    @Deployment
    public static Archive<?> createDeployment() {
        File[] deps = Maven.resolver()
                .loadPomFromFile("pom.xml")
                .importRuntimeAndTestDependencies()
                .resolve()
                .withTransitivity()
                .asFile();

        return ShrinkWrap.create(WebArchive.class)
                .addAsLibraries(deps);
    }

    @Test
    public void canConstructDozerBeanMapper() {
        Mapper mapper = DozerBeanMapperBuilder.create()
                .withMappingFiles("mappings/mapping.xml")
                .build();

        assertNotNull(mapper);
    }

    @Test
    public void canMapUsingXML() {
        Mapper mapper = DozerBeanMapperBuilder.create()
                .withMappingFiles("mappings/mapping.xml")
                .build();

        Person answer = mapper.map(new Person("bob"), Person.class);

        assertNotNull(answer);
        assertNotNull(answer.getName());
        assertEquals("bob", answer.getName());
    }

    @Test
    public void testOneWayExcludeViaApi() {
        Mapper mapper = DozerBeanMapperBuilder.create()
                .withMappingFiles("mappings/mapping.xml")
                .withMappingBuilder(new PersonMappingProfile())
                .build();

        PersonDTO dto = mapper.map(new Person("bob", "000-000-01"), PersonDTO.class);

        assertEquals(dto.getName(), null);

        assertEquals(dto.getId(), null);

        Person entity = mapper.map(new PersonDTO("bob", "000-000-01"), Person.class);

        assertEquals(entity.getName(), null);

        assertEquals(entity.getId(), "000-000-01");
    }

    public static class PersonDTO {
        private String name;
        private String id;

        PersonDTO() {
        }

        PersonDTO(String name, String id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    class PersonMappingProfile extends BeanMappingBuilder {
        @Override
        protected void configure() {
            mapping(Person.class, PersonDTO.class)
                    .exclude("name")
                    .exclude("id", MappingDirection.ONE_WAY);
        }
    }
}
