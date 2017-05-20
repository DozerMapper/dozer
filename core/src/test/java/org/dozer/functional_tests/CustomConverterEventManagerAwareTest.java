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

import java.util.Collections;

import org.dozer.DozerBeanMapper;
import org.dozer.DozerConverter;
import org.dozer.EventManagerAware;
import org.dozer.event.DozerEvent;
import org.dozer.event.DozerEventType;
import org.dozer.event.EventManager;
import org.dozer.functional_tests.event.EventTestListener;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author dmitry.buzdin
 */
public class CustomConverterEventManagerAwareTest extends AbstractFunctionalTest {

  private EventTestListener eventTestListener;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    eventTestListener = new EventTestListener();
    mapper = getMapper("customConverterEventManagerAware.xml");
    ((DozerBeanMapper) mapper).setEventListeners(Collections.singletonList(eventTestListener));
  }

  @Test
  public void test_convert_beanAToBeanB() {
    BeanA beanA = new BeanA("1");
    BeanC beanC = new BeanC();
    beanC.setC(2);
    beanA.setBeanC(beanC);


    BeanB beanB = mapper.map(beanA, BeanB.class);
    
    assertNotNull(beanB);

    assertThat(eventTestListener.getFiredEvents().size(), CoreMatchers.is(4));
    int i = 0;
    assertThat(eventTestListener.getFiredEvents().get(i++).getType(), CoreMatchers.is(DozerEventType.MAPPING_STARTED));
    assertThat(eventTestListener.getFiredEvents().get(i++).getType(), CoreMatchers.is(DozerEventType.MAPPING_PRE_WRITING_DEST_VALUE));
    assertThat(eventTestListener.getFiredEvents().get(i++).getType(), CoreMatchers.is(DozerEventType.MAPPING_POST_WRITING_DEST_VALUE));
    assertThat(eventTestListener.getFiredEvents().get(i++).getType(), CoreMatchers.is(DozerEventType.MAPPING_FINISHED));
    
  }

  public static class Converter extends DozerConverter <BeanA, BeanB> implements EventManagerAware {

    private EventManager eventManager;

    public Converter() {
      super(BeanA.class, BeanB.class);
    }

    @Override
    public BeanB convertTo(BeanA source, BeanB destination) {
      BeanB beanB = new BeanB();
      
      // set destination Value manual
      Integer destValue = Integer.valueOf(source.getA());
      firePreWritingDestValue(source, destination, destValue);
      beanB.setA(destValue);
      firePostWritingDestValue(source, destination, destValue);
      
      // fire event finished (will not be fired in case of custom converters!?)
      fireEventFinished(source, beanB);
      return beanB;
    }

    /** {@inheritDoc} */
    @Override
    public BeanA convertFrom(BeanB source, BeanA destination) {
      BeanA beanA = new BeanA();

      // set destination Value manual
      String destValue = source.getA().toString();
      firePreWritingDestValue(source, destination, destValue);
      beanA.setA(destValue);
      firePostWritingDestValue(source, destination, destValue);

      // fire event finished (will not be fired in case of custom converters!?)
      fireEventFinished(source, beanA);
      return beanA;
    }

    private void firePreWritingDestValue(Object source, Object destination, Object destValue) {
      final DozerEvent dozerEvent = new DozerEvent(//
        DozerEventType.MAPPING_PRE_WRITING_DEST_VALUE, null, null, source, destination, destValue);
      this.eventManager.fireEvent(dozerEvent);
    }

    private void firePostWritingDestValue(Object source, Object destination, Object destValue) {
      final DozerEvent dozerEvent = new DozerEvent(//
        DozerEventType.MAPPING_POST_WRITING_DEST_VALUE, null, null, source, destination, destValue);
      this.eventManager.fireEvent(dozerEvent);
    }


    /**
     * @param sourceObject the Source Object
     * @param destinationObject the Destination Object
     */
    public void fireEventFinished(final Object sourceObject, final Object destinationObject) {
        final DozerEvent dozerEvent = new DozerEvent(//
                DozerEventType.MAPPING_FINISHED, null, null, sourceObject, destinationObject, null);
        this.eventManager.fireEvent(dozerEvent);
    }
    @Override
    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    
  }

  public static class BeanA {

    public BeanA() {
    }

    public BeanA(String a) {
      this.a = a;
    }

    private BeanC beanC;
    private String a;

    public String getA() {
      return a;
    }

    public void setA(String a) {
      this.a = a;
    }

    public BeanC getBeanC() {
      return beanC;
    }

    public void setBeanC(BeanC beanC) {
      this.beanC = beanC;
    }
  }

  public static class BeanB {

    private Integer valueC;
    private Integer a;

    public Integer getA() {
      return a;
    }

    public void setA(Integer a) {
      this.a = a;
    }

    public Integer getValueC() {
      return valueC;
    }

    public void setValueC(Integer valueC) {
      this.valueC = valueC;
    }
  }

  public static class BeanC {

    private Integer c;

    public Integer getC() {
      return c;
    }

    public void setC(Integer c) {
      this.c = c;
    }
  }

}
