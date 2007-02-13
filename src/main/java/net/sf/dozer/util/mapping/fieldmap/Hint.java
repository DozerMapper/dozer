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
package net.sf.dozer.util.mapping.fieldmap;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.sf.dozer.util.mapping.MappingException;

/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 * 
 */
public class Hint implements Cloneable {

  private String hintName;
  private List hints;

  public Class getHint() {
    if (getHints().size() > 1) {
      return null;
    } else {
      return (Class) getHints().get(0);
    }
  }

  public Class getHint(Class myClaz, List clazHints) {
    List hints = getHints();
    int hintsSize = hints.size();
    if (hintsSize == 1) {
      return getHint();
    }
    // validate sizes
    if (clazHints.size() != hintsSize) {
      throw new MappingException(
          "When using multiple source and destination hints there must be exactly the same number of hints on the source and the destination.");
    }
    int count = 0;
    String myClazName = myClaz.getName();
    int size = clazHints.size();
    for (int i = 0; i < size; i++) {
      Class element = (Class) clazHints.get(i);
      if (element.getName().equals(myClazName)) {
        return (Class) hints.get(count++);
      }
      count++;
    }
    return myClaz;
  }

  public boolean hasMoreThanOneHint() {
    return getHints().size() > 1 ? true : false;
  }

  public List getHints() {
    if (hints == null) {
      List list = new ArrayList();
      try {
        StringTokenizer st = new StringTokenizer(this.hintName, ",");
        while (st.hasMoreElements()) {
          String theHintName = (String) st.nextToken().trim();

          Class clazz = Thread.currentThread().getContextClassLoader().loadClass(theHintName);
          list.add(clazz);
        }
      } catch (ClassNotFoundException e) {
        throw new MappingException(e);
      }
      hints = list;
    }
    return hints;
  }

  public String getHintName() {
    return hintName;
  }

  public void setHintName(String sourceHintName) {
    this.hintName = sourceHintName;
  }
 
}
