package com.mds.crud.utils;

import static com.mds.crud.utils.FunctionUtils.executableObject;
import static com.mds.crud.utils.ObjectUtils.isValidNull;

/**
 * Reflection helpers for instantiating classes via their no-arg constructor.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
public final class ReflectionUtils {

  private static final String CLASS_EXCEPTION_MESSAGE = "Class must not be null.";

  ReflectionUtils() {
  }

  /**
   * Generates an instance of the specified class.
   *
   * @param clazz The class to instantiate.
   * @param <T>   The type of the class.
   * @return The generated instance.
   */
  public static <T> T generateInstance(Class<? extends T> clazz) {
    // Validate the class parameter.
    isValidNull(clazz, CLASS_EXCEPTION_MESSAGE);

    // Execute the executable method.
    return executableObject(() -> clazz.getDeclaredConstructor().newInstance());
  }
}
