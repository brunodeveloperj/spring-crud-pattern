package com.mds.crud.utils;

import java.util.Collection;
import java.util.List;

/**
 * Collection conversion utilities (varargs to {@link java.util.List} /
 * {@link java.util.Collection}).
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
public final class CollectionUtils {

  CollectionUtils() {
  }

  /**
   * Converts the specified elements to a list.
   *
   * @param elements The elements to convert.
   * @param <T>      The type of the elements.
   * @return The list.
   */
  @SafeVarargs
  public static <T> List<T> convertToList(T... elements) {
    return (List<T>) convertToCollection(elements);
  }

  /**
   * Converts the specified elements to a collection.
   *
   * @param elements The elements to convert.
   * @param <T>      The type of the elements.
   * @return The collection.
   */
  @SafeVarargs
  public static <T> Collection<T> convertToCollection(T... elements) {
    return List.of(elements);
  }
}
