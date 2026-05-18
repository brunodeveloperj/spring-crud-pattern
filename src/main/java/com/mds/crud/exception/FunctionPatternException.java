package com.mds.crud.exception;

/**
 * Runtime exception thrown by the functional execution utilities when an
 * underlying operation fails.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
public class FunctionPatternException extends RuntimeException {

  public FunctionPatternException() {
    super();
  }

  public FunctionPatternException(String message) {
    super(message);
  }

  public FunctionPatternException(String message, Throwable tx) {
    super(message, tx);
  }

  public FunctionPatternException(Throwable tx) {
    super(tx);
  }

}
