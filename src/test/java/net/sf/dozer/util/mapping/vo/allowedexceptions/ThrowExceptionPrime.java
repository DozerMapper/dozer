package net.sf.dozer.util.mapping.vo.allowedexceptions;

import net.sf.dozer.util.mapping.exception.DozerRuntimeException;


public class ThrowExceptionPrime {
  private String throwAllowedException;
  private String throwNotAllowedException;
  
  public String getThrowAllowedException() {
    return throwAllowedException;
  }
  
  public void setThrowAllowedException(String throwAllowedException) {
    throw new DozerRuntimeException("Checking Allowed Exceptions");
  }
  
  public String getThrowNotAllowedException() {
    return throwNotAllowedException;
  }
  
  public void setThrowNotAllowedException(String throwNotAllowedException) {
    throw new RuntimeException("Checking Allowed Exceptions");
  }
}
