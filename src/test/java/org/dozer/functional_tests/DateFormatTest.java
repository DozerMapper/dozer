/*
 * Copyright 2005-2009 the original author or authors.
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

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import org.dozer.vo.DateContainer;

import java.util.Calendar;

/**
 * @author dmitry.buzdin
 */
public class DateFormatTest extends AbstractFunctionalTest {

  @Override
  @Before
  public void setUp() throws Exception {
    mapper = getMapper("dateFormat.xml");
  }

  @Test
  public void testConversion() {
    Calendar calendar = Calendar.getInstance();
    calendar.set(2009, 6, 3, 12, 20, 10);
    DateContainer result = mapper.map(calendar.getTime(), DateContainer.class);
    assertNotNull(result);
    assertEquals("2009-07-03 12:20:10", result.getDate());
    assertEquals("2009-07-03 12:20:10", result.getSet().iterator().next());
  }

  @Override
  protected DataObjectInstantiator getDataObjectInstantiator() {
    return NoProxyDataObjectInstantiator.INSTANCE;
  }
}
