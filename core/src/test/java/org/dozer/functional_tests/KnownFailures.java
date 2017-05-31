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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dozer.vo.CustomGetDest;
import org.dozer.vo.CustomGetSource;
import org.dozer.vo.MessageHeaderDTO;
import org.dozer.vo.MessageHeaderVO;
import org.dozer.vo.MessageIdVO;
import org.dozer.vo.inheritance.Inner;
import org.dozer.vo.inheritance.Outer;
import org.dozer.vo.inheritance.Target;
import org.dozer.vo.map.House;
import org.dozer.vo.map.Room;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * This is a holding grounds for test cases that reproduce known bugs, features, or gaps discovered during development.
 * As the use cases are resolved, these tests should be moved to the live unit test classes.
 * 
 * @author tierney.matt
 */
public class KnownFailures extends AbstractFunctionalTest {

  @Override
  @Before
  public void setUp() throws Exception {

  }

  /*
   * Feature Request #1731158. Need a way to explicitly specify a mapping between a custom data object and String. Not
   * sure the best way to do this. Copy by reference doesnt seem like a good fit.
   */
  @Test
  public void testListOfCustomObjectsToStringArray() {
    mapper = getMapper(new String[] {"mappings/knownFailures.xml"});
    MessageHeaderVO vo = new MessageHeaderVO();
    List<MessageIdVO> ids = new ArrayList<MessageIdVO>();
    ids.add(new MessageIdVO("1"));
    ids.add(new MessageIdVO("2"));
    vo.setMsgIds(ids);
    MessageHeaderDTO result = mapper.map(vo, MessageHeaderDTO.class);
    assertEquals("1", result.getIdList().getMsgIdsArray()[0]);
    assertEquals("2", result.getIdList().getMsgIdsArray()[1]);
  }

  @Test
  public void testObjectField() throws Exception {
    mapper = getMapper("mappings/knownFailures.xml");
    Outer o = new Outer();
    Target t = mapper.map(o, Target.class);

    assertEquals(((Inner) o.getInner()).getString(), t.getString());
  }
  
  /*
   *  2-2009  Stumbled on this while investigating a post.  The mappingProcessor.mapCollection() appeared to return null for the dest value
   */
  @Test
  public void testMapWithList() {
    mapper = getMapper("mappings/knownFailures.xml");
    Room room = new Room();
    room.setRoomName("some room name");
    House house = new House();
    house.setHouseName("some house name");
    house.setBathrooms(new ArrayList(Arrays.asList("master", "spare")));
    house.setRoom(room);
    
    Map<String, Object> result = mapper.map(house, HashMap.class);
    assertNotNull("bathrooms should exist", result.containsKey("bathrooms"));
    assertEquals("wrong bathrooms found", house.getBathrooms(), result.get("bathrooms"));
        
  }

  /*
   Test, which shows, that dozer doesn't support indexed read property
   */
  @Test
  public void testIndexedGetFailure() {
    mapper = getMapper("mappings/knownFailures.xml");

    CustomGetSource customGetSource = new CustomGetSource();
    customGetSource.setValue("some value");

    try {
      mapper.map(customGetSource, CustomGetDest.class);
    } catch (IllegalArgumentException e) {        
    }
    Assert.fail("Feature with indexed get method is not supported");
  }

}
