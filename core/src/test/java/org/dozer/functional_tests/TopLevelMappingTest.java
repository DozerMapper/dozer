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
package org.dozer.functional_tests;

import java.util.ArrayList;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author dmitry.buzdin
 */
public class TopLevelMappingTest extends AbstractFunctionalTest {

  @Test
  public void testListToListMapping_Explicit() {
    mapper = getMapper("mappings/topLevelMapping.xml");
    MyList source = new MyList();
    source.add("100");

    ArrayList result = mapper.map(source, ArrayList.class);

    assertEquals(1, result.size());
    assertEquals(100, result.get(0));
  }

  @Test
  public void testListToListMapping_Implicit() {
    mapper = getMapper();
    MyList source = new MyList();
    source.add("100");

    ArrayList result = mapper.map(source, ArrayList.class);

    assertEquals(1, result.size());
    assertEquals("100", result.get(0));
  }

  @Test
  public void testListToListMapping_ImplicitItems() {
    mapper = getMapper();
    ArrayList source = new ArrayList();
    ItemA itemA = new ItemA();
    itemA.setA("test");
    source.add(itemA);

    ArrayList result = mapper.map(source, ArrayList.class);

    assertEquals(1, result.size());
    assertEquals(itemA.getA(), ((ItemA) result.get(0)).getA());
  }

  @Test
  public void testListToListMapping_ExplicitItems() {
    mapper = getMapper("mappings/topLevelMapping.xml");
    MyList source = new MyList();
    ItemA itemA = new ItemA();
    itemA.setA("test");
    source.add(itemA);

    ArrayList result = mapper.map(source, ArrayList.class, "2");

    assertEquals(1, result.size());
    assertTrue(result.get(0) instanceof ItemB);
    assertEquals(itemA.getA(), ((ItemB) result.get(0)).getA());
  }

  public static class MyList extends ArrayList {
  }

  public static class ItemA {

    private String a;

    public String getA() {
      return a;
    }

    public void setA(String a) {
      this.a = a;
    }
  }

  public static class ItemB {
    private String a;

    public String getA() {
      return a;
    }

    public void setA(String a) {
      this.a = a;
    }
  }

}
