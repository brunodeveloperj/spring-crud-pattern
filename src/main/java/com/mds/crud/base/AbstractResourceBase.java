package com.mds.crud.base;


import static com.mds.shared.core.pattern.utils.FunctionUtils.executableObjectNullSafe;
import static org.springframework.util.ReflectionUtils.makeAccessible;

import com.mds.crud.annotation.InjectionDefault;
import com.mds.crud.interfaces.api.PatternServiceApi;
import java.lang.reflect.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstract base for REST resource (controller) classes that auto-discovers
 * the default {@link PatternServiceApi} via the {@link InjectionDefault}
 * annotation.
 *
 * @param <E> the entity type
 * @param <R> the response DTO type
 * @param <D> the DTO type
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
public abstract class AbstractResourceBase<E extends AbstractEntityBase<E, D>, R extends AbstractResponseDTOBase<D>, D> {

  public static final Logger logger = LoggerFactory.getLogger(AbstractResourceBase.class);
  private PatternServiceApi<E, R, D> patternService;
  private String currentResourceName;

  /**
   * Gets the pattern service.
   * <p>
   * This method gets the pattern service and returns it.
   * <p>
   * Preconditions:<br> - None.
   * <p>
   * Post conditions:<br> - The method returns the pattern service.
   */
  public synchronized PatternServiceApi<E, R, D> getPatternService() {
    // If the pattern service is null, load the default pattern service.
    if (patternService == null) {
      patternService = loadPatternServiceDefault();
    }

    // Return the pattern service.
    return patternService;
  }

  /**
   * Loads the default pattern service.
   * <p>
   * This method loads the default pattern service and returns it.
   * <p>
   * Preconditions:<br> - None.
   * <p>
   * Post conditions:<br> - The method returns the default pattern service.
   *
   * @throws NullPointerException If the service injection is null.
   */
  private PatternServiceApi<E, R, D> loadPatternServiceDefault() {
    PatternServiceApi<E, R, D> instance = null;

    // Iterate over all the fields in the class.
    for (Field f : getClass().getDeclaredFields()) {

      // Check if the field is annotated with the `InjectionDefault` annotation.
      if (f.isAnnotationPresent(InjectionDefault.class)) {

        // Convert the field to a service.
        instance = convertFieldToService(f);

        // Break out of the loop since we have found the default service.
        break;
      }
    }

    // Return the service.
    return instance;
  }

  /**
   * Converts a field to a service.
   * <p>
   * This method converts the field to a service by getting the value of the field and checking if it is an instance of the PatternServiceApi class.
   * <p>
   * Preconditions:<br> - The field must be accessible.
   * <p>
   * Post conditions:<br> - The method returns the service or null if the field is not an instance of the PatternServiceApi class.
   *
   * @throws IllegalAccessException If the field is not accessible.
   */
  private PatternServiceApi<E, R, D> convertFieldToService(Field field) {
    return executableObjectNullSafe(() -> {
      PatternServiceApi<E, R, D> service = null;

      // Make accessible in the current field.
      makeAccessible(field);

      // Get the value of the field.
      final Object fieldValue = field.get(this);

      // Check if the field value is an instance of the PatternServiceApi class.
      if (fieldValue instanceof PatternServiceApi) {
        service = (PatternServiceApi<E, R, D>) fieldValue;
      }

      // Return the service.
      return service;
    }, () -> null);
  }

  protected synchronized String getCurrentResourceName() {
    if (currentResourceName == null) {
      currentResourceName = getClass().getSimpleName();
    }
    return currentResourceName;
  }

}
