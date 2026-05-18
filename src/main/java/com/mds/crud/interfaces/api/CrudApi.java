package com.mds.crud.interfaces.api;


import com.mds.crud.base.AbstractEntityBase;
import com.mds.crud.base.AbstractResponseDTOBase;
import com.mds.crud.interfaces.JpaSpecificationRepository;
import com.mds.error.handler.exception.GeneralException;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import org.springframework.data.domain.Pageable;

/**
 * Generic CRUD contract exposing find, insert, update, delete and save
 * operations for a given entity/DTO pair.
 *
 * @param <E> the entity type
 * @param <R> the response DTO type
 * @param <D> the DTO type
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
public interface CrudApi<E extends AbstractEntityBase<E, D>, R extends AbstractResponseDTOBase<D>, D> {

  /**
   * Finds all entities.
   *
   * @param pageable The pagination information.
   * @return The list of entities.
   */
  R findAll(Pageable pageable);

  /**
   * Finds all entities.
   *
   * @param params The params information.
   * @return The list of entities.
   */
  R findAllByParams(Map<String, Object> params);

  /**
   * Finds an entity by its ID.
   *
   * @param id The ID of the entity.
   * @return The entity.
   */
  R findById(final Long id);

  /**
   * Checks if an entity exists by its ID.
   *
   * @param id The ID of the entity.
   * @return True if the entity exists, false otherwise.
   */
  boolean existsById(final Long id);

  /**
   * Inserts a new entity.
   *
   * @param dto The DTO containing the entity data.
   * @return The ID of the newly created entity.
   * @throws GeneralException If an error occurs during the insertion.
   */
  Long insert(@NotNull D dto) throws GeneralException;

  /**
   * Updates an existing entity.
   *
   * @param dto The DTO containing the entity data.
   * @return The ID of the updated entity.
   * @throws GeneralException If an error occurs during the update.
   */
  Long update(@NotNull D dto) throws GeneralException;

  /**
   * Deletes an entity by its ID.
   *
   * @param id The ID of the entity.
   * @throws GeneralException If an error occurs during the deletion.
   */
  void delete(final Long id) throws GeneralException;

  /**
   * Saves an entity.
   *
   * @param entity The entity to save.
   * @return The ID of the saved entity.
   * @throws GeneralException If an error occurs during the save.
   */
  Long save(final E entity) throws GeneralException;

  /**
   * Validates an object value.
   *
   * @param value    The object value to validate.
   * @param isUpdate True if the object is being updated, false otherwise.
   */
  void validate(final Object value, boolean isUpdate);

  /**
   * Gets the repository for the entity type.
   *
   * @return The repository.
   */
  JpaSpecificationRepository<E, Long> getRepository();

}

