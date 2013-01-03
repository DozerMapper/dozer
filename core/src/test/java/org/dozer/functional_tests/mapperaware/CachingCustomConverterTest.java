/*
 * Copyright 2005-2010 the original author or authors.
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
package org.dozer.functional_tests.mapperaware;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dozer.CustomConverter;
import org.dozer.DozerBeanMapper;
import org.dozer.DozerConverter;
import org.dozer.Mapper;
import org.dozer.MapperAware;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Fuzail Sarang
 */
public class CachingCustomConverterTest extends Assert {

  DozerBeanMapper mapper;

  @Before
  public void setup() {
    mapper = new DozerBeanMapper();
    List<String> mappingFileUrls = new ArrayList<String>();
    mappingFileUrls.add("mapper-aware.xml");

    Map<String, CustomConverter> customConvertersWithId = new HashMap<String, CustomConverter>();
    customConvertersWithId.put("associationConverter", new AssociatedEntityConverter());
    customConvertersWithId.put("collectionConverter", new CollectionConverter());

    mapper.setCustomConvertersWithId(customConvertersWithId);
    mapper.setMappingFiles(mappingFileUrls);
  }

  @Test
  public void shouldNotExitWithStackOverFlow() {

    BidirectionalOne one = new BidirectionalOne();
    BidirectionalMany source = new BidirectionalMany();
    source.setOne(one);
    Set<BidirectionalMany> many = new HashSet<BidirectionalMany>();

    many.add(source);
    one.setMany(many);

    BidirectionalManyConvert destination = mapper.map(source, BidirectionalManyConvert.class);

    assertNotNull(destination);
    assertNotNull(destination.getOne());
    assertNotNull(destination.getOne().getMany());
    assertFalse(destination.getOne().getMany().isEmpty());
    for (BidirectionalManyConvert guiAccessObject : destination.getOne().getMany()) {
      assertNotNull(guiAccessObject.getOne());
    }

  }

  class AssociatedEntityConverter extends DozerConverter<BidirectionalOneConvert, BidirectionalOne>
          implements MapperAware {

    private Mapper mapper;

    public AssociatedEntityConverter() {
      super(BidirectionalOneConvert.class, BidirectionalOne.class);
    }

    public void setMapper(Mapper mapper) {
      this.mapper = mapper;
    }

    @Override
    public BidirectionalOneConvert convertFrom(BidirectionalOne arg0, BidirectionalOneConvert arg1) {
      return mapper.map(arg0, BidirectionalOneConvert.class);
    }

    @Override
    public BidirectionalOne convertTo(BidirectionalOneConvert arg0, BidirectionalOne arg1) {
      return mapper.map(arg0, BidirectionalOne.class);
    }

  }

  class CollectionConverter extends DozerConverter<Set, Set> implements MapperAware {

    private Mapper mapper;

    public CollectionConverter() {
      super(Set.class, Set.class);
    }

    public void setMapper(Mapper mapper) {
      this.mapper = mapper;
    }

    public Set convertTo(Set source, Set destination) {
      return convert(source, destination);
    }

    private Set convert(Set source, Set destination) {
      Set result = new HashSet();
      for (Object object : source) {
        if (object instanceof BidirectionalManyConvert) {
          result.add(mapper.map(object, BidirectionalMany.class));

        } else {
          result.add(mapper.map(object, BidirectionalManyConvert.class));
        }
      }
      return result;
    }

    @Override
    public Set convertFrom(Set source, Set destination) {
      return convert(source, destination);
    }

  }

}
