package com.mds.crud.base;

import static com.mds.shared.core.pattern.utils.CollectionUtils.convertToList;
import static com.mds.shared.core.pattern.utils.FunctionUtils.executableObjectNullSafe;
import static com.mds.shared.core.pattern.utils.ReflectionUtils.generateInstance;
import static java.lang.reflect.Modifier.PRIVATE;
import static org.springframework.util.ReflectionUtils.makeAccessible;
import static org.springframework.util.ReflectionUtils.setField;

import com.mds.crud.annotation.IgnoreProperties;
import com.mds.crud.enumerator.TypeEnum;
import com.mds.crud.interfaces.EnumerationPattern;
import com.mds.crud.interfaces.api.EntityApi;
import com.mds.crud.keys.CrudKeys;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;

/**
 * Abstract base that implements the bidirectional property-copying
 * contract defined by {@link EntityApi}.
 *
 * <p>Subclasses inherit reflection-based copy logic that transfers field
 * values between entity and DTO instances, honouring
 * {@link IgnoreProperties} annotations.
 *
 * @param <E> the entity type
 * @param <D> the DTO type
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
public abstract class AbstractEntityBase<E, D> implements EntityApi<E, D> {

  private final Class<D> dtoClass;

  protected AbstractEntityBase() {
    ParameterizedType genericSuperClass = (ParameterizedType) getClass().getGenericSuperclass();
    final List<Type> types = convertToList(genericSuperClass.getActualTypeArguments());
    dtoClass = (Class<D>) types.get(CrudKeys.ONE_INDEX);
  }

  @Override
  public D copyPropertiesToDTO() {
    D dtoInstance = generateInstance(dtoClass);
    return copyPropertiesToDTO(dtoInstance);
  }

  /**
   * @param target The DTO to copy the properties to.
   * @return DTO class
   */
  @Override
  public D copyPropertiesToDTO(D target) {
    return copyPropertiesToDTO(getInstance(), target, getIgnoreProperties(TypeEnum.DTO));
  }

  /**
   * Copies properties from an entity to a DTO.
   * <p>
   * This method copies the properties from the specified entity to the specified DTO, ignoring the properties specified in the ignoreProperties array.
   * </p>
   *
   * @param source           The entity to copy from.
   * @param target           The DTO to copy to.
   * @param ignoreProperties The properties to ignore.
   * @return The DTO with the copied properties.
   * @see BeanUtils#copyProperties(Object, Object, String...)
   * @see #validateCopyPropertiesToDTO(E, D)
   */
  @Override
  public D copyPropertiesToDTO(E source, D target, String... ignoreProperties) {
    copyProperties(source, target, ignoreProperties);
    this.validateCopyPropertiesToDTO(target, source);
    return target;
  }

  /**
   * @param source The DTO to copy the properties from.
   * @return Entity class
   */
  @Override
  public E copyPropertiesToEntity(D source) {
    return copyPropertiesToEntity(source, getInstance(), getIgnoreProperties(TypeEnum.ENTITY));
  }

  /**
   * Copies properties from a DTO to an entity.
   * <p>
   * This method copies the properties from the specified DTO to the specified entity, ignoring the properties specified in the ignoreProperties array.
   * </p>
   *
   * @param source           The DTO to copy the properties from.
   * @param target           The entity to copy the properties to.
   * @param ignoreProperties The properties to ignore.
   * @return The entity with the copied properties.
   * @see BeanUtils#copyProperties(Object, Object, String...)
   * @see #validateCopyPropertiesToEntity(E, D)
   */
  @Override
  public E copyPropertiesToEntity(D source, E target, String... ignoreProperties) {
    copyProperties(source, target, ignoreProperties);
    this.validateCopyPropertiesToEntity(target, source);
    return target;
  }

  /**
   * Returns the ignore properties for the specified ignoreDomain.
   * <p>
   * This method iterates over all the fields in the class and checks if they are annotated with the IgnoreProperties annotation. If they are, and the annotation's type property is equal to the specified ignoreDomain, then the field's name is added to the list of ignore properties.
   * </p>
   *
   * @param ignoreDomain The ignoreDomain.
   * @return The ignore properties.
   * @see IgnoreProperties
   */
  private String[] getIgnoreProperties(final EnumerationPattern ignoreDomain) {
    List<String> listIgnoreProperties = new ArrayList<>();
    for (Field f : getInstance().getClass().getDeclaredFields()) {
      if (f.isAnnotationPresent(IgnoreProperties.class)) {
        final IgnoreProperties ignoreProperties = f.getAnnotation(IgnoreProperties.class);
        boolean existTypeProperties = (ignoreProperties != null && ignoreProperties.type() != null && ignoreProperties.type().length > 0);
        if (existTypeProperties) {
          boolean containsIgnoreDomainInTypeProperties = List.of(ignoreProperties.type()).contains(ignoreDomain);
          if(containsIgnoreDomainInTypeProperties) {
            listIgnoreProperties.add(ignoreProperties.value());
          }
        }
      }
    }
    return listIgnoreProperties.toArray(String[]::new);
  }

  /**
   * Copies the properties from the source object to the target object, ignoring the specified properties.
   *
   * @param source           The source object.
   * @param target           The target object.
   * @param ignoreProperties The properties to ignore.
   */
  private void copyProperties(Object source, Object target, String... ignoreProperties) {
    final List<String> ignorePropertiesList = convertToList(ignoreProperties);
    convertToList(target.getClass().getDeclaredFields()).stream().filter(declaredField -> ignorePropertiesList.stream().noneMatch(declaredField.getName()::equalsIgnoreCase)).forEach(field -> copyValueIntoField(field, source, target));
  }

  private void copyValueIntoField(Field field, Object source, Object target) {
    Object fieldValue = getFieldValueBySourceClass(field.getName(), source);
    final Object validatedValue = validateInstanceOf(fieldValue);
    if (validatedValue != null && field.getType().getName().equalsIgnoreCase(validatedValue.getClass().getTypeName())) {
      if(field.getModifiers() == PRIVATE) {
        makeAccessible(field);
      }
      setField(field, target, validatedValue);
    }
  }

  private Object getFieldValueBySourceClass(final String name, Object source) {
    return executableObjectNullSafe(() -> {
      Field field = source.getClass().getDeclaredField(name);
      makeAccessible(field);
      return field.get(source);
    }, () -> null);
  }

  private Object validateInstanceOf(Object value) {
    if (value != null) {
      if (value instanceof AbstractEntityBase) {
        final AbstractEntityBase<E, D> entityBase = (AbstractEntityBase<E, D>) value;
        value = entityBase.copyPropertiesToDTO();
      } else if (value instanceof List) {
        List<D> dtoList = new ArrayList<>();
        final List<? extends AbstractEntityBase<E, D>> entityList = (List<? extends AbstractEntityBase<E, D>>) value;
        if (!entityList.isEmpty()) {
          entityList.stream().map(AbstractEntityBase::copyPropertiesToDTO).forEach(dtoList::add);
        }
        value = dtoList;
      }
    }
    return value;
  }

}
