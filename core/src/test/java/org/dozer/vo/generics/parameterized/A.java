package org.dozer.vo.generics.parameterized;


public class A extends AbstractDto<Long> {

  /** serialVersionUID */
  private static final long serialVersionUID = 7848180510225889535L;

  private String comment;

  /**                                                                                      z
   * @return the comment
   */
  public String getComment() {
    return comment;
  }

  /**
   * @param comment the comment to set
   */
  public void setComment(final String comment) {
    this.comment = comment;
  }
  
  
}
