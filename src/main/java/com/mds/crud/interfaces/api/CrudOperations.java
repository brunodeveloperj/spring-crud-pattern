package com.mds.crud.interfaces.api;

import com.mds.crud.dto.general.PageableParamDTO;
import com.mds.error.handler.exception.GeneralException;
import java.util.Map;

/**
 * Framework-free CRUD surface for output adapters in hexagonal
 * (ports-and-adapters) architectures.
 *
 * <p>Every signature uses only library types ({@code D}, {@code R},
 * {@link PageableParamDTO}), {@link Map} and {@link Long} — no
 * {@code org.springframework.data} types such as {@code Pageable},
 * {@code Specification} or {@code Page} leak through the contract. A
 * persistence adapter can therefore implement an application output port by
 * delegating to this interface without importing Spring Data into its own
 * code, and can be unit-tested with a plain mock.
 *
 * <p>For classic layered/MVC applications prefer {@link CrudApi}, which
 * additionally exposes the Spring Data types directly.
 *
 * @param <D> the DTO type exchanged with the adapter
 * @param <R> the response DTO type (paginated envelope)
 *
 * @author MDS
 * @see CrudApi
 * @see PatternServiceApi
 * @since 0.0.1-SNAPSHOT
 */
public interface CrudOperations<D, R> {

  /**
   * Finds a page of entities using library-typed pagination.
   *
   * @param params page/limit/sort descriptor; {@code null} falls back to the
   *               defaults (page 0, size 10, sort by id).
   * @return the paginated response envelope.
   */
  R findAll(PageableParamDTO params);

  /**
   * Finds a page of entities applying equality/LIKE filters built from a
   * parameter map (a {@code %} or {@code _} value triggers a case-insensitive
   * LIKE; everything else is an equality predicate). Pagination keys are not
   * expected inside {@code filters} — pass them via {@code params}.
   *
   * @param filters attribute filters; may be {@code null} or empty.
   * @param params  page/limit/sort descriptor; {@code null} uses defaults.
   * @return the paginated response envelope.
   */
  R findAllByFilters(Map<String, Object> filters, PageableParamDTO params);

  /**
   * Finds an entity and returns it already converted to its DTO, so the
   * caller does not need to unwrap the response envelope.
   *
   * @param id the entity id.
   * @return the DTO, or {@code null} when not found.
   */
  D findDtoById(Long id);

  /**
   * Inserts an entity and returns the persisted DTO (identifier included),
   * saving the caller a follow-up query.
   *
   * @param dto the DTO containing the entity data.
   * @return the persisted DTO.
   * @throws GeneralException if the insert fails.
   */
  D insertAndReturn(D dto) throws GeneralException;

  /**
   * Updates an entity and returns the persisted DTO. When the DTO carries an
   * existing identifier the update is merged onto the managed entity, so
   * fields absent from the DTO (version, audit columns, associations) are
   * preserved.
   *
   * @param dto the DTO containing the entity data.
   * @return the persisted DTO.
   * @throws GeneralException if the update fails.
   */
  D updateAndReturn(D dto) throws GeneralException;

  /**
   * Deletes an entity by its id.
   *
   * @param id the entity id.
   * @throws GeneralException if the delete fails.
   */
  void delete(Long id) throws GeneralException;

  /**
   * Checks if an entity exists by its id.
   *
   * @param id the entity id.
   * @return true when the entity exists.
   */
  boolean existsById(Long id);

}
