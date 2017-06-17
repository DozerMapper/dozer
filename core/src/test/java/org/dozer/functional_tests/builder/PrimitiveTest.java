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

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;

import org.dozer.DozerBeanMapperBuilder;
import org.dozer.Mapper;
import org.dozer.loader.api.BeanMappingBuilder;
import org.dozer.loader.api.TypeMappingOptions;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;

public class PrimitiveTest extends Assert {

  @Test
  public void shouldMapPrimitiveTypes() throws Exception {
    Source source = new Source();
    source.file = "a";
    source.url = "http://a";
    source.type = "java.lang.String";
    source.bigDecimal = new BigDecimal("1");
    source.myDouble = new Double("1");

    Destination result = DozerBeanMapperBuilder.buildDefault().map(source, Destination.class);

    assertThat(result.file, equalTo(new File("a")));
    assertThat(result.url, equalTo(new URL("http://a")));
    assertThat(result.type, sameInstance(String.class));
    assertThat(result.bigDecimal, equalTo(new Double("1")));
    assertThat(result.myDouble, equalTo(new BigDecimal("1.0")));
  }

  @Test
  public void shouldMapOneWayOnly() {
    Mapper mapper = DozerBeanMapperBuilder.create()
            .withMappingBuilder(new BeanMappingBuilder() {
              @Override
              protected void configure() {
                mapping(type(Source.class),
                        type(Destination.class)
                        , TypeMappingOptions.oneWay()
                );

                mapping(type(Destination.class),
                        type(Source.class)
                        , TypeMappingOptions.oneWay(), TypeMappingOptions.wildcard(false)
                );

              }
            })
            .build();

    {
      Source source = new Source();
      source.bigDecimal = new BigDecimal(1);
      Destination result = mapper.map(source, Destination.class);
      assertThat(result.bigDecimal, equalTo(new Double(1)));
    }

    {
      Destination destination = new Destination();
      destination.bigDecimal = new Double(1);
      Source result = mapper.map(destination, Source.class);
      assertThat(result.bigDecimal, equalTo(null));
    }
  }

  public static class Source {
    String url;
    String type;
    String file;
    BigDecimal bigDecimal;
    Double myDouble;

    public void setUrl(String url) {
      this.url = url;
    }

    public void setType(String type) {
      this.type = type;
    }

    public void setFile(String file) {
      this.file = file;
    }

    public void setBigDecimal(BigDecimal bigDecimal) {
      this.bigDecimal = bigDecimal;
    }

    public void setMyDouble(Double myDouble) {
      this.myDouble = myDouble;
    }

    public String getUrl() {
      return url;
    }

    public String getType() {
      return type;
    }

    public String getFile() {
      return file;
    }

    public BigDecimal getBigDecimal() {
      return bigDecimal;
    }

    public Double getMyDouble() {
      return myDouble;
    }
  }

  public static class Destination {
    URL url;
    Class<String> type;
    File file;
    Double bigDecimal;
    BigDecimal myDouble;

    public URL getUrl() {
      return url;
    }

    public Class<String> getType() {
      return type;
    }

    public File getFile() {
      return file;
    }

    public Double getBigDecimal() {
      return bigDecimal;
    }

    public BigDecimal getMyDouble() {
      return myDouble;
    }

    public void setUrl(URL url) {
      this.url = url;
    }

    public void setType(Class<String> type) {
      this.type = type;
    }

    public void setFile(File file) {
      this.file = file;
    }

    public void setBigDecimal(Double bigDecimal) {
      this.bigDecimal = bigDecimal;
    }

    public void setMyDouble(BigDecimal myDouble) {
      this.myDouble = myDouble;
    }
  }

}
