/*
 * Copyright 2005-2007 the original author or authors.
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
package net.sf.dozer.util.mapping.util;

import net.sf.dozer.util.mapping.AbstractDozerTest;
import net.sf.dozer.util.mapping.config.GlobalSettings;
import net.sf.dozer.util.mapping.vo.enumtest.DestType;
import net.sf.dozer.util.mapping.vo.enumtest.DestTypeWithOverride;
import net.sf.dozer.util.mapping.vo.enumtest.SrcType;
import net.sf.dozer.util.mapping.vo.enumtest.SrcTypeWithOverride;

/**
 * Unit Test for MappingUtils's methods executing with JDK 1.5 or above.
 * @author cchou.hung
 *
 */
public class MappingUtilsTest_JDK15 extends AbstractDozerTest {

  protected void setUp() throws Exception {
    super.setUp();
  }
  
  /**
   * Test for isEnumType(Class srcFieldClass, Class destFieldType) defined in MappingUtils
   */
  public void testIsEnum() {
    if (GlobalSettings.getInstance().isJava5()){
      assertTrue(MappingUtils.isEnumType(SrcType.class, DestType.class));
      assertTrue(MappingUtils.isEnumType(SrcType.FOO.getClass(), DestType.FOO.getClass()));
      assertTrue(MappingUtils.isEnumType(SrcTypeWithOverride.FOO.getClass(), 
          DestType.FOO.getClass()));
      assertTrue(MappingUtils.isEnumType(SrcTypeWithOverride.FOO.getClass(), 
          DestTypeWithOverride.FOO.getClass()));
      assertFalse(MappingUtils.isEnumType(SrcType.class, String.class));
      assertFalse(MappingUtils.isEnumType(String.class, SrcType.class));
    }
  }

}
