package com.mds.crud.utils;

import com.mds.crud.exception.FunctionPatternException;
import com.mds.crud.interfaces.ExecutableObject;
import com.mds.crud.interfaces.ExecutableVoid;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Functional execution utilities that wrap {@link ExecutableObject} and
 * {@link ExecutableVoid} calls with null-safety, exception customisation
 * and optional fallback suppliers.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
public final class FunctionUtils {

  private static final String OPTIONAL_RESPONSE_SUPPLIER_PARAM = "optionalResponseSupplier";
  private static final String OBJECT_PARAM = "exeObj";
  private static final String PARAM_EXCEPTION_MESSAGE = "The ${0} parameter must not be null.";
  private static final String VOID_PARAM = "exeVoid";

  FunctionUtils() {
  }

  /**
   * Executes the specified executable object and returns the result.
   *
   * @param exeObj The executable object to execute.
   * @return The result of the execution.
   * @see #executableObject(ExecutableObject, boolean)
   */
  public static <T> T executableObject(ExecutableObject<? extends T> exeObj) {
    // Execute the executable object.
    return executableObject(exeObj, true);
  }

  /**
   * Executes the specified executable object and returns the result.
   *
   * @param exeObj        The executable object to execute.
   * @param showException Whether to show the exception.
   * @return The result of the execution.
   * @throws IllegalArgumentException If the exeObj parameter is null.
   */
  public static <T> T executableObject(ExecutableObject<? extends T> exeObj, boolean showException) {
    // Check if the exeObj parameter is null.
    ObjectUtils.isValidNull(exeObj, generateMessage(PARAM_EXCEPTION_MESSAGE, OBJECT_PARAM));

    // Execute the executable object.
    return executable(null, exeObj, null, showException, null);
  }

  /**
   * Executes the specified executable object and returns the result.
   *
   * @param exeObj                   The executable object to execute.
   * @param optionalResponseSupplier The supplier of the optional response to return if the executable object throws an exception.
   * @return The result of the execution.
   * @throws IllegalArgumentException If the exeObj or optionalResponseSupplier parameter is null.
   */
  public static <T> T executableObjectNullSafe(ExecutableObject<? extends T> exeObj, Supplier<? extends T> optionalResponseSupplier) {
    // Check if the exeObj parameter is null.
    ObjectUtils.isValidNull(exeObj, generateMessage(PARAM_EXCEPTION_MESSAGE, OBJECT_PARAM));

    // Check if the optionalResponseSupplier parameter is null.
    ObjectUtils.isValidNull(optionalResponseSupplier, generateMessage(PARAM_EXCEPTION_MESSAGE, OPTIONAL_RESPONSE_SUPPLIER_PARAM));

    // Execute the executable object.
    return executable(null, exeObj, null, false, optionalResponseSupplier);
  }

  //----------------------------------------------------------------------

  /**
   * Executes the specified executable and returns the result.
   *
   * @param exeVoid                  The executable to execute.
   * @param exeObj                   The executable to execute and return the result.
   * @param customizedException      The customized exception to throw if the executable throws an exception.
   * @param showException            Whether to show the exception.
   * @param optionalResponseSupplier The optional response to return if the executable throws an exception.
   * @return The result of the execution, or the optional response if the executable throws an exception.
   * @throws RuntimeException If the executable throws an exception and the customized exception is not null, or if the showException parameter is true.
   */
  private static <T> T executable(ExecutableVoid exeVoid, ExecutableObject<? extends T> exeObj, Throwable customizedException, boolean showException, Supplier<? extends T> optionalResponseSupplier) {
    // Execute the executable object.
    T result = null;
    try {
      if (exeVoid != null) {
        exeVoid.execute();
      }
      if (exeObj != null) {
        result = exeObj.execute();
      }
    } catch (Throwable e) {
      if (showException) {
        throw new FunctionPatternException(customizedException == null ? e : customizedException);
      } else {
        result = ObjectUtils.nullSafeSupplierGet(optionalResponseSupplier);
      }
    }
    return result;
  }

  /**
   * Executes the specified executable object and returns the result.
   *
   * @param exeVoid The executable void to execute.
   * @param exeObj  The executable object to execute.
   * @return The result of the execution.
   * @see #executable(ExecutableVoid, ExecutableObject, boolean)
   */
  public static <T> T executable(ExecutableVoid exeVoid, ExecutableObject<? extends T> exeObj) {
    // Execute the executable object.
    return executable(exeVoid, exeObj, true);
  }

  /**
   * Executes the specified executable object and returns the result.
   *
   * @param exeVoid       The executable void to execute.
   * @param exeObj        The executable object to execute.
   * @param showException Whether to show the exception.
   * @return The result of the execution.
   * @throws IllegalArgumentException If the exeVoid or exeObj parameter is null.
   */
  public static <T> T executable(ExecutableVoid exeVoid, ExecutableObject<? extends T> exeObj, boolean showException) {
    // Check if the exeVoid parameter is null.
    ObjectUtils.isValidNull(exeVoid, generateMessage(PARAM_EXCEPTION_MESSAGE, VOID_PARAM));

    // Check if the exeObj parameter is null.
    ObjectUtils.isValidNull(exeObj, generateMessage(PARAM_EXCEPTION_MESSAGE, OBJECT_PARAM));

    // Execute the executable object.
    return executable(exeVoid, exeObj, null, showException, null);
  }

  //----------------------------------------------------------------------

  /**
   * Generates a message with the specified format and parameter.
   *
   * @param format The format of the message.
   * @param param  The parameter to be substituted into the format string.
   * @return The generated message.
   */
  private static String generateMessage(String format, String param) {
    return ObjectUtils.format(format, Map.of("0", param));
  }
}
