package com.mds.crud.resource;


import com.mds.crud.base.AbstractEntityBase;
import com.mds.crud.base.AbstractResourceBase;
import com.mds.crud.base.AbstractResponseDTOBase;
import com.mds.crud.dto.general.PageableParamDTO;
import com.mds.crud.interfaces.api.PatternApi;
import java.util.Map;
import org.springframework.http.ResponseEntity;

/**
 * Default REST resource (controller) implementation that delegates every
 * CRUD endpoint to the underlying {@link PatternServiceApi}.
 *
 * @param <E> the entity type
 * @param <R> the response DTO type
 * @param <D> the DTO type
 *
 * @author MDS
 * @see AbstractResourceBase
 * @see PatternApi
 * @since 0.0.1-SNAPSHOT
 */
public class PatternResource<E extends AbstractEntityBase<E, D>, R extends AbstractResponseDTOBase<D>, D> extends AbstractResourceBase<E, R, D> implements PatternApi<R, D> {

  /**
   * Finds all patterns.
   *
   * @param page  The page number.
   * @param limit The limit.
   * @param sort  The sort.
   * @return The response DTO.
   */
  @Override
  public R findAll(Integer page, Integer limit, String sort) {
    logger.info("\n :::::::::::::::::::::::::: {} ::::: findAll ::::::::::::::::::::::::::", getCurrentResourceName());
    return getPatternService().findAll(PageableParamDTO.builder().pageNumber(page).limit(limit).sorts(sort).build().convertToPageable());
  }

  /**
   * Finds all patterns.
   *
   * @param params The params.
   * @return The response DTO.
   */
  @Override
  public R findAllByParams(Map<String, Object> params) {
    logger.info("\n :::::::::::::::::::::::::: {} ::::: findAllByParams ::::::::::::::::::::::::::", getCurrentResourceName());
    return getPatternService().findAllByParams(params);
  }

  /**
   * Finds a pattern by id.
   *
   * @param id The id of the pattern.
   * @return The response DTO.
   */
  @Override
  public R findById(Long id) {
    logger.info("\n :::::::::::::::::::::::::: {} ::::: findById ::::::::::::::::::::::::::", getCurrentResourceName());
    return getPatternService().findById(id);
  }

  /**
   * Inserts a new pattern.
   *
   * @param dto The DTO of the pattern.
   * @return The id of the inserted pattern.
   */
  @Override
  public Long insert(D dto) {
    logger.info("\n :::::::::::::::::::::::::: {} ::::: insert ::::::::::::::::::::::::::", getCurrentResourceName());
    return getPatternService().insert(dto);
  }

  /**
   * Updates a pattern.
   *
   * @param dto The DTO of the pattern.
   * @return The id of the updated pattern.
   */
  @Override
  public Long update(D dto) {
    logger.info("\n :::::::::::::::::::::::::: {} ::::: update ::::::::::::::::::::::::::", getCurrentResourceName());
    return getPatternService().update(dto);
  }

  /**
   * Deletes a pattern by id.
   *
   * @param id The id of the pattern.
   */
  @Override
  public ResponseEntity<Void> delete(Long id) {
    logger.info("\n :::::::::::::::::::::::::: {} ::::: delete ::::::::::::::::::::::::::", getCurrentResourceName());
    getPatternService().delete(id);
    return ResponseEntity.noContent().build();
  }

}
