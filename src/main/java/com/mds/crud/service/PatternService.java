package com.mds.crud.service;


import com.mds.crud.base.AbstractEntityBase;
import com.mds.crud.base.AbstractResponseDTOBase;
import com.mds.crud.base.AbstractServiceBase;
import com.mds.crud.dto.general.PageableDTO;
import com.mds.crud.dto.general.PageableParamDTO;
import com.mds.crud.interfaces.JpaSpecificationRepository;
import com.mds.crud.interfaces.api.PatternServiceApi;
import com.mds.crud.keys.CrudKeys;
import com.mds.shared.core.pattern.utils.FunctionUtils;
import com.mds.error.handler.exception.GeneralException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ReflectionUtils;


/**
 * Default implementation of {@link PatternServiceApi} providing standard
 * CRUD operations (find, insert, update, delete) with pagination support
 * and before/after lifecycle hooks.
 *
 * @param <E> the entity type
 * @param <R> the response DTO type
 * @param <D> the DTO type
 *
 * @author MDS
 * @see AbstractServiceBase
 * @see PatternServiceApi
 * @since 0.0.1-SNAPSHOT
 */
public class PatternService<E extends AbstractEntityBase<E, D>, R extends AbstractResponseDTOBase<D>, D> extends AbstractServiceBase<E, R, D> implements PatternServiceApi<E, R, D> {

  /**
   * Finds all entities.
   * <p>
   * This method calls the `executable()` method to get the entities from the database. The `executable()` method executes a lambda expression in a separate thread.
   * <p>
   * The method then converts the entities to DTOs. The DTOs are created by calling the `copyPropertiesToDTO()` method on each entity.
   * <p>
   * The method then sets the pagination information on the response DTO. The pagination information is calculated by calling the `compute()` method on the `PageablePageDTO` class.
   *
   * @param pageable The pagination information.
   * @return The list of entities.
   */
  @Override
  public R findAll(Pageable pageable) {

    // Gets the entities from the database.
    final Page<E> pageEntity = getRepository().findAll(pageable);

    // Converts the entities to DTOs.
    return toResponse(pageEntity);
  }

  /**
   * Finds a page of entities using library-typed pagination, the
   * framework-free surface for hexagonal output adapters.
   *
   * @param params page/limit/sort descriptor; {@code null} falls back to
   *               the defaults (page 0, size 10, sort by id).
   * @return The paginated response envelope.
   */
  @Override
  public R findAll(PageableParamDTO params) {
    return findAll(toPageable(params));
  }

  /**
   * Finds a page of entities applying map-driven filters, keeping the
   * pagination descriptor outside the filter map.
   *
   * @param filters attribute filters; may be {@code null} or empty.
   * @param params  page/limit/sort descriptor; {@code null} uses defaults.
   * @return The paginated response envelope.
   */
  @Override
  public R findAllByFilters(Map<String, Object> filters, PageableParamDTO params) {
    final Page<E> pageEntity = getRepository().findAll(validateParams(filters), toPageable(params));
    return toResponse(pageEntity);
  }

  private Pageable toPageable(PageableParamDTO params) {
    return Objects.requireNonNullElse(params, new PageableParamDTO()).convertToPageable();
  }

  private R toResponse(Page<E> pageEntity) {
    R responseDTO = generateNewInstanceResponseDTO();
    responseDTO.setPageable(PageableDTO.compute(pageEntity.getTotalElements(), pageEntity.getPageable().getPageSize(), pageEntity.getPageable()));
    responseDTO.setContent(pageEntity.getContent().stream().map(AbstractEntityBase::copyPropertiesToDTO).collect(Collectors.toList()));
    return responseDTO;
  }

  /**
   * Finds all entities.
   * <p>
   * This method calls the `executable()` method to get the entities from the database. The `executable()` method executes a lambda expression in a separate thread.
   * <p>
   * The method then converts the entities to DTOs. The DTOs are created by calling the `copyPropertiesToDTO()` method on each entity.
   * <p>
   * The method then sets the pagination information on the response DTO. The pagination information is calculated by calling the `compute()` method on the `PageablePageDTO` class.
   *
   * @param params The params information.
   * @return The list of entities.
   */
  @Override
  public R findAllByParams(Map<String, Object> params) {
    // Gets the entities from the database.
    final Page<E> pageEntity = getRepository().findAll(validateParams(params), generatePageableInToParams(params));

    // Converts the entities to DTOs.
    return toResponse(pageEntity);
  }

  /**
   * Finds an entity and returns it already converted to its DTO.
   *
   * @param id The ID of the entity to find.
   * @return The DTO, or {@code null} when not found.
   */
  @Override
  public D findDtoById(Long id) {
    return getRepository().findById(id).map(AbstractEntityBase::copyPropertiesToDTO).orElse(null);
  }

  /**
   * Finds an entity by its ID.
   * <p>
   * This method calls the `executable()` method to get the entity from the database. The `executable()` method executes a lambda expression in a separate thread.
   * <p>
   * The method then creates a new DTO if the entity is not null. The DTO is created by calling the `copyPropertiesToDTO()` method on the entity.
   * <p>
   * The method then adds the DTO to the response DTO. The response DTO is created by calling the `generateNewInstanceResponseDTO()` method.
   *
   * @param id The ID of the entity to find.
   * @return The entity, or null if the entity does not exist.
   */
  @Override
  public R findById(Long id) {
    // Gets the entity from the database.
    final Optional<E> optionalEntity = getRepository().findById(id);
    E entityDB = optionalEntity.orElse(null);

    // Converts the entity to a DTO.
    D dto = null;
    if (Objects.nonNull(entityDB)) {
      dto = entityDB.copyPropertiesToDTO();
    }

    // Creates a response DTO.
    R responseDTO = generateNewInstanceResponseDTO();
    responseDTO.setPageable(new PageableDTO(false));
    responseDTO.addRegisterInContent(dto);
    return responseDTO;
  }

  /**
   * Checks if an entity exists by its ID.
   * <p>
   * This method throws a `NullPointerException` if the ID parameter is null.
   * <p>
   * The method first calls the `getRepository()` method to get the repository for the entity type. The `getRepository()` method returns an instance of the repository for the entity type.
   * <p>
   * The method then calls the `existsById()` method on the repository to check if the entity exists. The `existsById()` method returns true if the entity exists, false otherwise.
   *
   * @param id The ID of the entity to check.
   * @return True if the entity exists, false otherwise.
   * @throws NullPointerException If the ID parameter is null.
   */
  @Override
  public boolean existsById(Long id) {
    if (id == null) {
      throw new NullPointerException("Id parameter is null.");
    }

    // Checks if the entity exists in the database.
    return getRepository().existsById(id);
  }

  /**
   * Inserts an entity.
   * <p>
   * This method first creates a new entity instance using the generateNewInstanceEntity() method.
   * <p>
   * The generateNewInstanceEntity() method returns a new instance of the entity class.
   * <p>
   * The method then copies the data from the DTO to the entity using the copyPropertiesToEntity() method.
   * <p>
   * The copyPropertiesToEntity() method copies the data from the DTO to the entity's properties.
   * <p>
   * The method then saves the entity to the database using the save() method.
   * <p>
   * The save() method saves the entity to the database.
   * <p>
   * The method then calls the doBeforeInsert() and doAfterInsert() callbacks.
   * <p>
   * The doBeforeInsert() callback is called before the entity is inserted.
   * <p>
   * The doAfterInsert() callback is called after the entity is inserted.
   *
   * @param dto The DTO containing the entity data.
   * @return The ID of the inserted entity.
   * @throws GeneralException If an error occurs during the insert operation.
   */
  @Override
  public Long insert(D dto) throws GeneralException {
    return doInsert(dto).getId();
  }

  /**
   * Inserts an entity and returns the persisted DTO, identifier included.
   *
   * @param dto The DTO containing the entity data.
   * @return The persisted DTO.
   * @throws GeneralException If an error occurs during the insert operation.
   */
  @Override
  public D insertAndReturn(D dto) throws GeneralException {
    return doInsert(dto).copyPropertiesToDTO();
  }

  private E doInsert(D dto) throws GeneralException {
    doBeforeInsert(dto);
    // Creates a new entity instance and copies the data from the DTO.
    final var entity = generateNewInstanceEntity().copyPropertiesToEntity(dto);

    // Saves the entity to the database.
    save(entity);

    doAfterInsert(dto, entity.getId());
    return entity;
  }

  /**
   * Performs operations before inserting an entity.
   * <p>
   * This method does not perform any validation on this class. Subclasses can override this method to perform custom validation before inserting an entity.
   *
   * @param dto The DTO containing the entity data.
   * @throws GeneralException If an error occurs during the insert operation.
   */
  protected void doBeforeInsert(D dto) throws GeneralException {
    // This method does not perform any validation on this class.
  }

  /**
   * Performs operations after inserting an entity.
   * <p>
   * This method does not perform any validation on this class. Subclasses can override this method to perform custom validation after inserting an entity.
   *
   * @param dto      The DTO containing the entity data.
   * @param entityId The ID of the inserted entity.
   * @throws GeneralException If an error occurs during the insert operation.
   */
  protected void doAfterInsert(D dto, Long entityId) throws GeneralException {
    // This method does not perform any validation on this class.
  }

  /**
   * Updates an entity.
   * <p>
   * This method first updates the entity using the `copyPropertiesToEntity()` method. The `copyPropertiesToEntity()` method copies the data from the DTO to the entity.
   * <p>
   * The method then calls the `doBeforeUpdate()` and `doAfterUpdate()` callbacks. The `doBeforeUpdate()` callback is called before the entity is updated. The `doAfterUpdate()` callback is called after the entity is updated.
   *
   * @param dto The DTO containing the entity data.
   * @return The ID of the updated entity.
   * @throws GeneralException If an error occurs during the update operation.
   */
  @Override
  public Long update(D dto) throws GeneralException {
    return doUpdate(dto).getId();
  }

  /**
   * Updates an entity and returns the persisted DTO.
   *
   * @param dto The DTO containing the entity data.
   * @return The persisted DTO.
   * @throws GeneralException If an error occurs during the update operation.
   */
  @Override
  public D updateAndReturn(D dto) throws GeneralException {
    return doUpdate(dto).copyPropertiesToDTO();
  }

  private E doUpdate(D dto) throws GeneralException {
    doBeforeUpdate(dto);

    // Updates the entity using the `copyPropertiesToEntity()` method.
    final var entity = resolveEntityForUpdate(dto);
    final var entityId = save(entity);

    doAfterUpdate(dto, entityId);
    return entity;
  }

  /**
   * Resolves the entity to persist for an update. When the DTO carries an
   * existing identifier, the managed entity is loaded and the DTO properties
   * are merged onto it — null values are skipped by the copy engine, so
   * fields absent from the DTO (version, audit columns, associations) are
   * preserved. Otherwise a new entity is built from the DTO, keeping the
   * previous behaviour (the save validation rejects unknown identifiers).
   *
   * @param dto The DTO containing the entity data.
   * @return The entity to save.
   */
  protected E resolveEntityForUpdate(D dto) {
    final Long id = extractDtoId(dto);
    if (id != null) {
      final Optional<E> persisted = getRepository().findById(id);
      if (persisted.isPresent()) {
        return persisted.get().copyPropertiesToEntity(dto);
      }
    }
    return generateNewInstanceEntity().copyPropertiesToEntity(dto);
  }

  private Long extractDtoId(D dto) {
    if (dto == null) {
      return null;
    }
    final Field field = ReflectionUtils.findField(dto.getClass(), "id");
    if (field == null) {
      return null;
    }
    ReflectionUtils.makeAccessible(field);
    final Object value = ReflectionUtils.getField(field, dto);
    return value instanceof Number number ? number.longValue() : null;
  }

  /**
   * Performs operations before updating an entity.
   * <p>
   * This method does not perform any validation on this class. Subclasses can override this method to perform custom validation before updating an entity.
   *
   * @param dto The DTO containing the entity data.
   * @throws GeneralException If an error occurs during the update operation.
   */
  protected void doBeforeUpdate(D dto) throws GeneralException {
    // This method does not perform any validation on this class.
  }

  /**
   * Performs operations after updating an entity.
   * <p>
   * This method does not perform any validation on this class. Subclasses can override this method to perform custom validation after updating an entity.
   *
   * @param dto      The DTO containing the entity data.
   * @param entityId The ID of the entity that was updated.
   * @throws GeneralException If an error occurs during the update operation.
   */
  protected void doAfterUpdate(D dto, Long entityId) throws GeneralException {
    // This method does not perform any validation on this class.
  }

  /**
   * Deletes an entity by its ID.
   * <p>
   * This method validates that the entity exists before attempting deletion,
   * and provides before/after hooks for subclasses to customise the workflow.
   *
   * @param id The ID of the entity to delete.
   * @throws GeneralException           If an error occurs during the deletion.
   * @throws NullPointerException        If the id parameter is null.
   * @throws EmptyResultDataAccessException If the entity does not exist.
   */
  @Override
  public void delete(Long id) throws GeneralException {
    validate(id, true);
    doBeforeDelete(id);
    getRepository().deleteById(id);
    doAfterDelete(id);
  }

  /**
   * Performs operations before deleting an entity.
   * <p>
   * Subclasses can override this method to perform custom logic before deletion
   * (e.g. cascade checks, audit logging).
   *
   * @param id The ID of the entity to delete.
   * @throws GeneralException If an error occurs during the pre-delete operation.
   */
  protected void doBeforeDelete(Long id) throws GeneralException {
    // No-op — override in subclasses
  }

  /**
   * Performs operations after deleting an entity.
   * <p>
   * Subclasses can override this method to perform custom logic after deletion
   * (e.g. cache eviction, event publishing).
   *
   * @param id The ID of the entity to delete.
   * @throws GeneralException If an error occurs during the post-delete operation.
   */
  protected void doAfterDelete(Long id) throws GeneralException {
    // No-op — override in subclasses
  }

  /**
   * Saves an entity.
   * <p>
   * This method first validates the entity and then saves it to the database.
   * <p>
   * The entity is validated before it is saved to ensure that it is in a valid state.
   * <p>
   * The entity is saved using a doAfterSave() callback to allow subclasses to perform custom logic after the entity is saved.
   *
   * @param entity The entity to save.
   * @return The ID of the saved entity.
   * @throws GeneralException If an error occurs during the save operation.
   */
  @Override
  public Long save(E entity) throws GeneralException {
    final boolean isUpdate = entity.isExistId();
    // Validates the entity before saving it.
    doBeforeSave(entity, isUpdate);

    // Saves the entity to the database.
    final var entityDB = FunctionUtils.executable(() -> validate(entity, isUpdate), () -> getRepository().save(entity));

    // Performs custom logic after the entity is saved.
    doAfterSave(entity, isUpdate);
    return entityDB.getId();
  }

  /**
   * Performs operations before saving an entity.
   * <p>
   * This method does not perform any validation on this class. Subclasses can override this method to perform custom validation before saving an entity.
   *
   * @param entity   The entity to save.
   * @param isUpdate True if the entity is being updated, false otherwise.
   * @throws GeneralException If an error occurs during the save operation.
   */
  protected void doBeforeSave(E entity, boolean isUpdate) throws GeneralException {
    // Does not perform validation on this class
  }

  /**
   * Performs operations after saving an entity.
   * <p>
   * This method does not perform any validation on this class. Subclasses can override this method to perform custom validation after saving an entity.
   *
   * @param entity   The entity that was saved.
   * @param isUpdate True if the entity was updated, false otherwise.
   * @throws GeneralException If an error occurs during the save operation.
   */
  protected void doAfterSave(E entity, boolean isUpdate) throws GeneralException {
    // Does not perform validation on this class
  }

  /**
   * Validates an entity.
   * <p>
   * This method throws a NullPointerException if the value parameter is null.
   * <p>
   * The method first checks if the value parameter is null.
   * <p>
   * If it is null, the method throws a NullPointerException.
   * <p>
   * The method then checks if the operation is an update.
   * <p>
   * If it is an update, the method checks if the entity exists.
   * <p>
   * If the entity does not exist, the method throws an EmptyResultDataAccessException.
   *
   * @param value    The entity to validate.
   * @param isUpdate True if the entity is being updated, false insertion.
   * @throws NullPointerException           If the value parameter is null.
   * @throws EmptyResultDataAccessException If the entity does not exist and the operation is an update.
   */
  @Override
  public void validate(Object value, boolean isUpdate) {
    if (value == null) {
      throw new NullPointerException("Value parameter is null.");
    }
    if (isUpdate) {
      final Long id = (value instanceof AbstractEntityBase ? ((E) value).getId() : (Long) value);
      if (id == null) {
        throw new NullPointerException("Cannot perform update process.");
      } else if (!existsById(id)) {
        throw new EmptyResultDataAccessException("Result not found with the report id.", CrudKeys.ZERO_INDEX);
      }
    }
  }

  /**
   * Gets the repository for the entity type.
   *
   * @return The repository for the entity type.
   * <p>
   * The repository is lazy-loaded to avoid unnecessary database calls. The repository is loaded using the {@link #loadPatternRepositoryDefault()} method.
   */
  @Override
  public JpaSpecificationRepository<E, Long> getRepository() {
    if (patternRepository == null) {
      patternRepository = loadPatternRepositoryDefault();
    }
    return patternRepository;
  }

}
