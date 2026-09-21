package com.mds.crud.base;

import static com.mds.shared.core.pattern.utils.CollectionUtils.convertToList;
import static com.mds.shared.core.pattern.utils.FunctionUtils.executableObject;
import static com.mds.shared.core.pattern.utils.FunctionUtils.executableObjectNullSafe;
import static com.mds.shared.core.pattern.utils.ReflectionUtils.generateInstance;
import static org.springframework.util.ReflectionUtils.makeAccessible;
import static org.springframework.util.ReflectionUtils.setField;

import com.mds.crud.annotation.IgnoreProperties;
import com.mds.crud.enumerator.TypeEnum;
import com.mds.crud.interfaces.EnumerationPattern;
import com.mds.crud.interfaces.api.EntityApi;
import com.mds.crud.keys.CrudKeys;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;

/**
 * Abstract base that implements the bidirectional property-copying
 * contract defined by {@link EntityApi}.
 *
 * <p>Subclasses inherit reflection-based copy logic that transfers field
 * values between entity and DTO instances, honouring
 * {@link IgnoreProperties} annotations.
 *
 * <p>The DTO type may be a mutable JavaBean (populated field by field) or a
 * Java {@code record} (built through its canonical constructor from
 * same-named entity properties), which makes immutable DTOs a first-class
 * option for hexagonal application ports.
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
    dtoClass = (Class<D>) resolveTypeArgument(CrudKeys.ONE_INDEX);
  }

  /**
   * Resolves a generic type argument walking up the class hierarchy, so
   * intermediate entity base classes keep working when the concrete class
   * does not parameterize {@link AbstractEntityBase} directly.
   */
  private Type resolveTypeArgument(int index) {
    Class<?> current = getClass();
    while (current != null && !Object.class.equals(current)) {
      Type genericSuperclass = current.getGenericSuperclass();
      if (genericSuperclass instanceof ParameterizedType parameterized
          && parameterized.getActualTypeArguments().length > index) {
        return parameterized.getActualTypeArguments()[index];
      }
      current = current.getSuperclass();
    }
    throw new IllegalStateException(
        "Cannot resolve DTO type argument for " + getClass().getName());
  }

  @Override
  public D copyPropertiesToDTO() {
    if (dtoClass.isRecord()) {
      return copyPropertiesToRecord();
    }
    D dtoInstance = generateInstance(dtoClass);
    return copyPropertiesToDTO(dtoInstance);
  }

  /**
   * Builds a record DTO through its canonical constructor, mapping every
   * record component from the same-named entity property. Components whose
   * entity counterpart is missing or type-incompatible receive the default
   * value for their type (null, or the primitive default).
   */
  private D copyPropertiesToRecord() {
    RecordComponent[] components = dtoClass.getRecordComponents();
    Class<?>[] parameterTypes = new Class<?>[components.length];
    Object[] arguments = new Object[components.length];
    for (int i = 0; i < components.length; i++) {
      parameterTypes[i] = components[i].getType();
      arguments[i] = resolveComponentValue(components[i]);
    }
    return executableObject(
        () -> dtoClass.getDeclaredConstructor(parameterTypes).newInstance(arguments));
  }

  private Object resolveComponentValue(RecordComponent component) {
    Object value = validateInstanceOf(getFieldValueBySourceClass(component.getName(), getInstance()));
    if (value != null && isAssignableValue(component.getType(), value.getClass())) {
      return value;
    }
    return defaultValueFor(component.getType());
  }

  private static Object defaultValueFor(Class<?> type) {
    if (!type.isPrimitive()) {
      return null;
    }
    if (boolean.class.equals(type)) {
      return false;
    }
    if (char.class.equals(type)) {
      return '\0';
    }
    if (byte.class.equals(type)) {
      return (byte) 0;
    }
    if (short.class.equals(type)) {
      return (short) 0;
    }
    if (int.class.equals(type)) {
      return 0;
    }
    if (long.class.equals(type)) {
      return 0L;
    }
    if (float.class.equals(type)) {
      return 0f;
    }
    if (double.class.equals(type)) {
      return 0d;
    }
    return null;
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
    if (validatedValue != null && isAssignableValue(field.getType(), validatedValue.getClass())) {
      if (Modifier.isPrivate(field.getModifiers())) {
        makeAccessible(field);
      }
      setField(field, target, validatedValue);
    }
  }

  /**
   * Checks whether a value type can be assigned into a field type, including
   * primitive-to-wrapper equivalence (e.g. an {@code int} field accepts an
   * {@code Integer} value).
   */
  private static boolean isAssignableValue(Class<?> fieldType, Class<?> valueType) {
    if (fieldType.isPrimitive()) {
      return ClassUtils.isAssignable(fieldType, valueType);
    }
    return fieldType.isAssignableFrom(valueType);
  }

  private Object getFieldValueBySourceClass(final String name, Object source) {
    return executableObjectNullSafe(() -> {
      Field field = findFieldInHierarchy(source.getClass(), name);
      if (field == null) {
        return null;
      }
      makeAccessible(field);
      return field.get(source);
    }, () -> null);
  }

  private static Field findFieldInHierarchy(Class<?> type, String name) {
    Class<?> current = type;
    while (current != null && !Object.class.equals(current)) {
      try {
        return current.getDeclaredField(name);
      } catch (NoSuchFieldException ignored) {
        current = current.getSuperclass();
      }
    }
    return null;
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
