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
package org.dozer.loader.api;

import org.dozer.loader.DozerBuilder;

/**
 * @author Dmitry Buzdin
 */
public final class TypeMappingBuilder {

  private DozerBuilder.MappingBuilder beanMappingBuilder;

  public TypeMappingBuilder(DozerBuilder.MappingBuilder beanMappingBuilder) {
    this.beanMappingBuilder = beanMappingBuilder;
  }

  public TypeMappingBuilder fields(String a, String b, FieldsMappingOption... options) {
    return fields(new FieldDefinition(a), new FieldDefinition(b), options);
  }

  public TypeMappingBuilder fields(FieldDefinition a, String b, FieldsMappingOption... options) {
    return fields(a, new FieldDefinition(b), options);
  }

  public TypeMappingBuilder fields(String a, FieldDefinition b, FieldsMappingOption... options) {
    return fields(new FieldDefinition(a), b, options);
  }  

  public TypeMappingBuilder fields(FieldDefinition a, FieldDefinition b, FieldsMappingOption... options) {
    DozerBuilder.FieldMappingBuilder builder = beanMappingBuilder.field();
    
    String aText = a.resolve();
    String bText = b.resolve();

    a.build(builder.a(aText));
    b.build(builder.b(bText));

    for (FieldsMappingOption option : options) {
      option.apply(builder);
    }

    return this;
  }

  public TypeMappingBuilder exclude(String field) {
    return exclude(new FieldDefinition(field));
  }

  public TypeMappingBuilder exclude(FieldDefinition field) {
    DozerBuilder.FieldExclusionBuilder builder = beanMappingBuilder.fieldExclude();
    builder.a(field.resolve(), null);
    builder.b(field.resolve(), null);
    return this;
  }

}
