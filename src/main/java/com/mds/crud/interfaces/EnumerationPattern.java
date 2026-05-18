package com.mds.crud.interfaces;

/**
 * Contract for domain enumerations that expose an ordinal and a
 * human-readable description.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
public interface EnumerationPattern {

  /**
   * Returns the ordinal position.
   *
   * @return the order
   */
  int getOrder();

  /**
   * Returns the human-readable description.
   *
   * @return the description
   */
  String getDescription();

}
