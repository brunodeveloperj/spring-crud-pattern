package com.mds.crud.interfaces.api;

import com.mds.crud.exception.FunctionPatternException;
import com.mds.error.handler.exception.GeneralException;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * REST controller contract that exposes standard CRUD endpoints
 * (find, insert, update, delete) with pagination and filtering support.
 *
 * @param <R> the response DTO type
 * @param <D> the request DTO type
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
public interface PatternApi<R, D> {

  /**
   * Finds all patterns.
   *
   * @param page  The page number.
   * @param limit The limit.
   * @param sort  The sort.
   * @return The response DTO.
   */
  @ResponseStatus(HttpStatus.OK)
  @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
  R findAll(@RequestParam(value = "_page", required = false, defaultValue = "0") Integer page, @RequestParam(value = "_limit", required = false, defaultValue = "10") Integer limit, @RequestParam(value = "_sort", required = false) String sort);

  /**
   * Finds all patterns.
   *
   * @param params The params.
   * @return The response DTO.
   * @throws FunctionPatternException If an error occurs.
   */
  @ResponseStatus(HttpStatus.OK)
  @GetMapping(value = "/filter", produces = {MediaType.APPLICATION_JSON_VALUE})
  R findAllByParams(@RequestParam Map<String, Object> params);

  /**
   * Finds a pattern by id.
   *
   * @param id The id of the pattern.
   * @return The response DTO.
   * @throws FunctionPatternException If an error occurs.
   */
  @ResponseStatus(HttpStatus.OK)
  @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
  R findById(@PathVariable(value = "id") Long id);

  /**
   * Inserts a new pattern.
   *
   * @param dto The DTO of the pattern.
   * @return The id of the inserted pattern.
   * @throws FunctionPatternException If an error occurs.
   */
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
  Long insert(@Valid @RequestBody D dto);

  /**
   * Updates a pattern.
   *
   * @param dto The DTO of the pattern.
   * @return The id of the updated pattern.
   * @throws FunctionPatternException If an error occurs.
   */
  @ResponseStatus(HttpStatus.OK)
  @PutMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
  Long update(@Valid @RequestBody D dto);

  /**
   * Deletes a pattern by id.
   *
   * @param id The id of the pattern.
   */
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
  ResponseEntity<Void> delete(@PathVariable(value = "id") Long id);

}
