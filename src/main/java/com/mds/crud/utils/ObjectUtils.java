package com.mds.crud.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * General-purpose object utilities — null validation, string formatting
 * with named placeholders, and null-safe supplier invocation.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
public final class ObjectUtils {

  ObjectUtils() {
  }

  /**
   * Validates that the specified value is not null.
   *
   * @param value   The value to validate.
   * @param message The error message to throw if the value is null.
   * @param <T>     The type of the value.
   */
  public static <T> void isValidNull(T value, String message) {
    if (value == null) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Formats a string using a map of values.
   * <br>
   * <br>
   * Example:<br> FormatTextUtil.format("Hello ${0}", Map.of("0", "World!")); Result: "Hello World!"
   *
   * @param format The format string.
   * @param values The map of values.
   * @return The formatted string.
   */
  public static String format(String format, Map<String, Object> values) {
    // Create a StringBuilder to hold the formatted string.
    StringBuilder formatter = new StringBuilder(format);

    // Create a list to hold the values.
    List<Object> valueList = new ArrayList<>();

    // Create a matcher to find the placeholders in the format string.
    Matcher matcher = Pattern.compile("\\$\\{(\\w+)}").matcher(format);

    // Iterate over the placeholders and replace them with the corresponding values.
    while (matcher.find()) {
      // Get the name of the placeholder.
      String key = matcher.group(1);

      // Create a format string for the placeholder.
      String formatKey = String.format("${%s}", key);

      // Get the index of the placeholder in the format string.
      int index = formatter.indexOf(formatKey);

      // If the placeholder is found, replace it with the corresponding value.
      if (index != -1) {
        formatter.replace(index, index + formatKey.length(), "%s");
        valueList.add(values.get(key));
      }
    }

    // Return the formatted string.
    return String.format(formatter.toString(), valueList.toArray());
  }

  /**
   * Gets the value of the supplier, or null if the supplier is null.
   *
   * @param messageSupplier The supplier to get the value from.
   * @return The value of the supplier, or null.
   */
  public static <T> T nullSafeSupplierGet(Supplier<T> messageSupplier) {
    return (messageSupplier != null ? messageSupplier.get() : null);
  }

}
