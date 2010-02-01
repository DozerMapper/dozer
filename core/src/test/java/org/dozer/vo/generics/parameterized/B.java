package org.dozer.vo.generics.parameterized;


public class B implements Dto<Integer> {

  /** serialVersionUID */
  private static final long serialVersionUID = -1783044929807936401L;

  /** attributes */
  private Integer id;
  
  /** {@inheritDoc} */
  public Integer getId() {
    return id;
  }

  /** {@inheritDoc} */
  public void setId(final Integer id) {
    this.id = id;
  }

}
