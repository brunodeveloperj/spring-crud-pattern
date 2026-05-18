package com.mds.crud.enumerator;


import com.mds.crud.interfaces.EnumerationPattern;

/**
 * Enumerates the copy directions used by
 * {@link com.mds.crud.annotation.IgnoreProperties} to control which
 * properties are skipped during entity ↔ DTO mapping.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
public enum TypeEnum implements EnumerationPattern {

  /**
   * The DTO type.
   */
  DTO(1, "DTO"),

  /**
   * The entity type.
   */
  ENTITY(2, "Entity");

  /**
   * The order of the type.
   */
  private final int order;

  /**
   * The description of the type.
   */
  private final String description;

  /**
   * Creates a new type entry.
   *
   * @param order       the ordinal position
   * @param description the human-readable label
   */
  TypeEnum(int order, String description) {
    this.order = order;
    this.description = description;
  }

  /** {@inheritDoc} */
  @Override
  public int getOrder() {
    return order;
  }

  /** {@inheritDoc} */
  @Override
  public String getDescription() {
    return description;
  }

}