package com.mds.crud.interfaces;

/**
 * Functional interface for a void operation that may throw a checked
 * exception.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
public interface ExecutableVoid {

  /**
   * Executes the object.
   *
   * @throws Throwable If an error occurs during the execution.
   */
  void execute() throws Throwable;

}
