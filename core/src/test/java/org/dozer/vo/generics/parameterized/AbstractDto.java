package org.dozer.vo.generics.parameterized;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class AbstractDto<T extends Serializable> implements Dto<T> {

  /** serialVersionUID */
  private static final long serialVersionUID = 8391790466366203602L;

  /** the identifier of this domain object */
  private T id;

  public T getId() {
    return id;
  }

  public void setId(final T id) {
    this.id = id;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
  }
}
