package com.mds.crud.base;


import static com.mds.crud.keys.CrudKeys.ONE_INDEX;
import static com.mds.crud.keys.CrudKeys.SECOND_INDEX;
import static com.mds.crud.keys.CrudKeys.ZERO_INDEX;
import static com.mds.crud.keys.PageableKeys.PAGEABLE_PARAMS;
import static com.mds.shared.core.pattern.utils.CollectionUtils.convertToList;
import static com.mds.shared.core.pattern.utils.FunctionUtils.executableObject;
import static com.mds.shared.core.pattern.utils.FunctionUtils.executableObjectNullSafe;
import static com.mds.shared.core.pattern.utils.ObjectUtils.nonNull;
import static com.mds.shared.core.helper.ConversionHelper.convertJsonToObject;
import static org.springframework.util.ReflectionUtils.makeAccessible;

import com.mds.crud.annotation.InjectionDefault;
import com.mds.crud.dto.general.PageableParamDTO;
import com.mds.crud.interfaces.JpaSpecificationRepository;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Abstract base for service classes that auto-discovers the default
 * {@link JpaSpecificationRepository} via {@link InjectionDefault} and
 * provides generic instance-creation, pagination and JPA specification
 * helpers.
 *
 * @param <E> the entity type
 * @param <R> the response DTO type
 * @param <D> the DTO type
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
public abstract class AbstractServiceBase<E, R, D> {

  public static final Logger logger = LoggerFactory.getLogger(AbstractServiceBase.class);
  private final Class<E> entityClass;
  private final Class<R> responseDTOClass;
  private final Class<D> dtoClass;
  protected JpaSpecificationRepository<E, Long> patternRepository;

  /**
   * This is the constructor of the `AbstractServiceBase` class.
   */
  protected AbstractServiceBase() {
    ParameterizedType genericSuperClass = (ParameterizedType) getClass().getGenericSuperclass();
    final List<Type> types = convertToList(genericSuperClass.getActualTypeArguments());
    entityClass = (Class<E>) types.get(ZERO_INDEX);
    responseDTOClass = (Class<R>) types.get(ONE_INDEX);
    dtoClass = (Class<D>) types.get(SECOND_INDEX);
  }

  /**
   * Loads the default repository for the current class.
   *
   * @return The default repository.
   * @throws NullPointerException If the repository instance is null.
   */
  protected synchronized JpaSpecificationRepository<E, Long> loadPatternRepositoryDefault() {
    JpaSpecificationRepository<E, Long> instance = null;

    // Iterate over all the fields in the class.
    for (Field f : getClass().getDeclaredFields()) {

      // Check if the field is annotated with the `InjectionDefault` annotation.
      if (f.isAnnotationPresent(InjectionDefault.class)) {

        // Convert the field to a repository.
        instance = convertFieldToRepository(f);

        // Break out of the loop since we have found the default repository.
        break;
      }
    }

    // Return the repository.
    return instance;
  }

  /**
   * Converts a field to a JpaRepository.
   *
   * @param field The field to convert.
   * @return The JpaRepository.
   * @throws IllegalAccessException if an exception is made
   */
  private JpaSpecificationRepository<E, Long> convertFieldToRepository(Field field) {
    return executableObjectNullSafe(() -> {
      JpaSpecificationRepository<E, Long> repository = null;

      // Make accessible in the current field.
      makeAccessible(field);

      // Get the value of the field.
      final Object fieldValue = field.get(this);

      // Check if the field value is an instance of the JpaRepository class.
      if (fieldValue instanceof JpaRepository) {
        repository = (JpaSpecificationRepository<E, Long>) fieldValue;
      }

      // Return the repository.
      return repository;
    }, () -> null);
  }

  /**
   * Generates a new instance of the entity class.
   * <p>
   * This method creates a new instance of the entity class and returns it.
   * <p>
   * Preconditions:<br> - The entityClass parameter must be a valid class.
   * <p>
   * Post conditions:<br> - The method returns a new instance of the entityClass.
   *
   * @return New entity instance
   * @throws InstantiationException If an error occurs in the Instantiation of the entity
   * @throws IllegalAccessException If the entity is not accessible.
   */
  public E generateNewInstanceEntity() {
    // Create a new instance of the entity class.
    return executableObject(() -> entityClass.getDeclaredConstructor().newInstance());
  }

  /**
   * Generates a new instance of the response DTO class.
   * <p>
   * This method creates a new instance of the response DTO class and returns it.
   * <p>
   * Preconditions:<br> - The responseDTOClass parameter must be a valid class.
   * <p>
   * Post conditions:<br> - The method returns a new instance of the responseDTOClass.
   *
   * @return New response DTO instance
   * @throws InstantiationException If an error occurs in the Instantiation of the response DTO
   * @throws IllegalAccessException If the response DTO is not accessible.
   */
  public R generateNewInstanceResponseDTO() {
    // Create a new instance of the response DTO class.
    return executableObject(() -> responseDTOClass.getDeclaredConstructor().newInstance());
  }

  /**
   * Generates a new instance of the DTO class.
   * <p>
   * This method creates a new instance of the DTO class and returns it.
   * <p>
   * Preconditions:<br> - The dtoClass parameter must be a valid class.
   * <p>
   * Post conditions:<br> - The method returns a new instance of the dtoClass.
   *
   * @return New DTO instance
   * @throws InstantiationException If an error occurs in the Instantiation of the DTO
   * @throws IllegalAccessException If the DTO is not accessible.
   */
  public D generateNewInstanceDTO() {
    // Create a new instance of the DTO class.
    return executableObject(() -> dtoClass.getDeclaredConstructor().newInstance());
  }

  public Specification<E> validateParams(Map<String, Object> params) {
    return (root, query, builder) -> {
      List<Predicate> predicates = new ArrayList<>();
      if (nonNull(params)) {
        params.entrySet()
              .stream()
              .filter(entryFilter -> PAGEABLE_PARAMS.stream().noneMatch(entryFilter.getKey()::equalsIgnoreCase))
              .forEach(entryResult -> {
                Predicate predicate;
                Path<String> pathName = root.get(entryResult.getKey());
                if (entryResult.getValue() instanceof String value && (value.contains("%") || value.contains("_"))) {
                  predicate = builder.like(builder.lower(pathName), value.toLowerCase());
                } else {
                  predicate = builder.equal(pathName, entryResult.getValue());
                }
                predicates.add(predicate);
              });
      }
      return query.where(predicates.toArray(Predicate[]::new)).getRestriction();
    };
  }

  public Pageable generatePageableInToParams(Map<String, Object> params) {
    return convertJsonToObject(params, PageableParamDTO.class).convertToPageable();
  }

}
