package com.mds.crud.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Convenience interface that combines {@link JpaRepository} and
 * {@link JpaSpecificationExecutor} into a single contract.
 *
 * <p>Repositories extending this interface inherit full CRUD operations
 * together with dynamic query support via Spring Data JPA
 * {@link org.springframework.data.jpa.domain.Specification Specification}
 * predicates, eliminating the need to declare both super-interfaces
 * individually.
 *
 * @param <T>  the domain entity type
 * @param <ID> the entity identifier type
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
public interface JpaSpecificationRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

}
