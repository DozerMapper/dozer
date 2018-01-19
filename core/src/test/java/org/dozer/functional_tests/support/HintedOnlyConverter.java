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
package org.dozer.functional_tests.support;

import org.dozer.CustomConverter;
import org.dozer.vo.HintedOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author garsombke.franz
 */
public class HintedOnlyConverter implements CustomConverter {

  private static final Logger log = LoggerFactory.getLogger(HintedOnlyConverter.class);

  public Object convert(Object destination, Object source, Class<?> destClass, Class<?> sourceClass) {
    log.debug("Source Class is:" + sourceClass.getName());
    log.debug("Dest Class is:" + destClass.getName());
    if (source != null) {
      log.debug("Source Obj is:" + source.getClass().getName());
    }
    if (destination != null) {
      log.debug("Dest Obj is:" + destination.getClass().getName());
    }
    if (source instanceof HintedOnly) {
      return ((HintedOnly) source).getStr();
    }

    HintedOnly hint;
    if (destination == null) {
      hint = new HintedOnly();
    } else {
      hint = (HintedOnly) destination;
    }
    hint.setStr((String) source);
    return hint;
  }
}
