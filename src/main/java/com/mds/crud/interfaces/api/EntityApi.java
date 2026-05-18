package com.mds.crud.interfaces.api;

import com.mds.crud.keys.CrudKeys;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mds.crud.annotation.IgnoreProperties;

import java.io.Serializable;

/**
 * Bidirectional property-mapping contract between an entity and its DTO.
 *
 * <p>Implementations provide {@code copyPropertiesToDTO} and
 * {@code copyPropertiesToEntity} methods to transfer field values in
 * both directions, with optional property exclusion.
 *
 * @param <E> the entity type
 * @param <D> the DTO type
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
public interface EntityApi<E, D> extends Serializable {

  /**
   * Gets the entity instance.
   *
   * @return The entity instance.
   */
  @JsonIgnore
  E getInstance();

  /**
   * Gets the id of the entity.
   *
   * @return The id of the entity.
   */
  Long getId();

  /**
   * Checks if the entity has an id.
   *
   * @return True if the entity has an id, false otherwise.
   */
  @JsonIgnore
  default boolean isExistId() {
    return (this.getId() != null);
  }

  /**
   * Validates the properties that are copied to a DTO.
   *
   * @param dtoCopy The DTO to copy the properties to.
   */
  default void validateCopyPropertiesToDTO(D dtoCopy) {
    // Does not perform validation on this class
  }

  /**
   * Validates the properties that are copied to a DTO.
   *
   * @param dtoCopy The DTO to copy the properties to.
   * @param source  The entity to copy the properties from.
   */
  default void validateCopyPropertiesToDTO(D dtoCopy, E source) {
    validateCopyPropertiesToDTO(dtoCopy);
  }

  /**
   * Validates the properties that are copied to an entity.
   *
   * @param entityCopy The entity to copy the properties to.
   */
  default void validateCopyPropertiesToEntity(E entityCopy) {
    // Does not perform validation on this class
  }

  /**
   * Validates the properties that are copied to an entity.
   *
   * @param entityCopy The entity to copy the properties to.
   * @param source     The DTO to copy the properties from.
   */
  default void validateCopyPropertiesToEntity(E entityCopy, D source) {
    validateCopyPropertiesToEntity(entityCopy);
  }

  /**
   * Copies the properties from an entity to a DTO.
   *
   * @return The DTO with the copied properties.
   */
  D copyPropertiesToDTO();

  /**
   * Copies the properties from an entity to a DTO.
   *
   * @param target The DTO to copy the properties to.
   * @return The DTO with the copied properties.
   */
  D copyPropertiesToDTO(D target);

  /**
   * Copies the properties from an entity to a DTO.
   *
   * @param source The entity to copy the properties from.
   * @param target The DTO to copy the properties to. {@link IgnoreProperties} The properties to ignore.
   * @return The DTO with the copied properties.
   */
  default D copyPropertiesToDTO(E source, D target) {
    return copyPropertiesToDTO(source, target, CrudKeys.STR_EMPTY);
  }

  /**
   * Copies the properties from an entity to a DTO.
   *
   * @param target           The DTO to copy the properties to.
   * @param ignoreProperties The properties to ignore.
   * @return The DTO with the copied properties.
   */
  default D copyPropertiesToDTO(D target, String... ignoreProperties) {
    return copyPropertiesToDTO(getInstance(), target, ignoreProperties);
  }

  /**
   * Copies properties from an entity to a DTO.
   *
   * @param source           The entity to copy from.
   * @param target           The DTO to copy to.
   * @param ignoreProperties The properties to ignore.
   * @return The DTO with the copied properties.
   */
  D copyPropertiesToDTO(E source, D target, String... ignoreProperties);

  /**
   * Copies the properties from a DTO to an entity.
   *
   * @param source The DTO to copy the properties from.
   * @return The entity with the copied properties.
   */
  E copyPropertiesToEntity(D source);

  /**
   * Copies the properties from a DTO to an entity.
   *
   * @param source The DTO to copy the properties from.
   * @param target The entity to copy the properties to.
   * @return The entity with the copied properties.
   */
  default E copyPropertiesToEntity(D source, E target) {
    return copyPropertiesToEntity(source, target, CrudKeys.STR_EMPTY);
  }

  /**
   * Copies the properties from a DTO to an entity.
   *
   * @param source           The DTO to copy the properties from.
   * @param ignoreProperties The properties to ignore.
   * @return The entity with the copied properties.
   */
  default E copyPropertiesToEntity(D source, String... ignoreProperties) {
    return copyPropertiesToEntity(source, getInstance(), ignoreProperties);
  }

  /**
   * Copies the properties from an entity to a DTO.
   *
   * @param source           The entity to copy the properties from.
   * @param target           The DTO to copy the properties to.
   * @param ignoreProperties The properties to ignore.
   * @return The DTO with the copied properties.
   */
  E copyPropertiesToEntity(D source, E target, String... ignoreProperties);

}
