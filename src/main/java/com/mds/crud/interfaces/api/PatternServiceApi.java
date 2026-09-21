package com.mds.crud.interfaces.api;

import com.mds.crud.base.AbstractEntityBase;
import com.mds.crud.base.AbstractResponseDTOBase;

/**
 * Service-layer contract that extends {@link CrudApi} for pattern-based
 * CRUD services.
 *
 * <p>It also exposes {@link CrudOperations}, the framework-free surface
 * intended for output adapters in hexagonal architectures, so the same
 * bean serves both layered/MVC and ports-and-adapters consumers.
 *
 * @param <E> the entity type
 * @param <R> the response DTO type
 * @param <D> the DTO type
 *
 * @author MDS
 * @see CrudApi
 * @see CrudOperations
 * @since 0.0.1-SNAPSHOT
 */
public interface PatternServiceApi<E extends AbstractEntityBase<E, D>, R extends AbstractResponseDTOBase<D>, D> extends CrudApi<E, R, D>, CrudOperations<D, R> {


}
