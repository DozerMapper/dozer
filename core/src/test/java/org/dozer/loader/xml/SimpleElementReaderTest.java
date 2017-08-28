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
package org.dozer.loader.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.dozer.AbstractDozerTest;
import org.dozer.el.NoopELEngine;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Buzdin
 */
public class SimpleElementReaderTest extends AbstractDozerTest {
  
  private SimpleElementReader reader;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    reader = new ExpressionElementReader(new NoopELEngine());
  }

  @Test
  public void testGetAttribute() {
    Element element = mock(Element.class);
    when(element.getAttribute("A")).thenReturn(" B ");

    String result = reader.getAttribute(element, "A");

    assertEquals("B", result);
  }

  @Test
  public void testGetNodeValue() {
    Element element = mock(Element.class);
    Node node = mock(Node.class);
    when(node.getNodeValue()).thenReturn(" B ");
    when(element.getFirstChild()).thenReturn(node);

    String result = reader.getNodeValue(element);

    assertEquals("B", result);
  }

  @Test
  public void testNodeValueIsNull() {
    Element element = mock(Element.class);
    Node node = mock(Node.class);
    when(node.getNodeValue()).thenReturn(null);
    when(element.getFirstChild()).thenReturn(node);

    assertEquals("", reader.getNodeValue(element));
  }

}
