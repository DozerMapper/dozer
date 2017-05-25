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
package org.dozer.vo;

/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 * 
 */
public class SuperSuperSuperClass extends BaseTestObject {
  private String superSuperSuperAttr;
  private HydrateTestObject2 hydrate;
  private TestCustomConverterObject customConvert;

  public TestCustomConverterObject getCustomConvert() {
    return customConvert;
  }

  public void setCustomConvert(TestCustomConverterObject customConver) {
    this.customConvert = customConver;
  }

  public HydrateTestObject2 getHydrate() {
    return hydrate;
  }

  public void setHydrate(HydrateTestObject2 hydrate) {
    this.hydrate = hydrate;
  }

  public String getSuperSuperSuperAttr() {
    return superSuperSuperAttr;
  }

  public void setSuperSuperSuperAttr(String superSuperSuperAttr) {
    this.superSuperSuperAttr = superSuperSuperAttr;
  }
}
