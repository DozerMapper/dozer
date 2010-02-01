package org.dozer.vo.generics.parameterized;

import java.io.Serializable;

public interface Dto<I extends Serializable>
  
  extends Serializable {

  I getId();

  void setId(final I id);

}
