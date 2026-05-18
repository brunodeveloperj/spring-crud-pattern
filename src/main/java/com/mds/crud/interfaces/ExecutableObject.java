package com.mds.crud.interfaces;

/**
 * Functional interface for an operation that returns a result and may throw
 * a checked exception.
 *
 * @param <T> the result type
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
public interface ExecutableObject<T> {

  /**
   * Executes the object.
   *
   * @return The result of the execution.
   * @throws Throwable If an error occurs during the execution.
   */
  T execute() throws Throwable;

}
