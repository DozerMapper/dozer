package net.sf.dozer.functional_tests.vo.allowedexceptions;

public class TestException extends RuntimeException {
  public TestException() {
  }

  public TestException(String msg) {
    super(msg);
  }
}
