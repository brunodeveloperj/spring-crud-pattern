package com.mds.crud.annotation;


import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.mds.crud.enumerator.TypeEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks fields or methods whose values should be skipped during
 * entity ↔ DTO property copying in {@link com.mds.crud.base.AbstractEntityBase}.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
@Documented
@Target({ElementType.TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface IgnoreProperties {

  /**
   * The property name to ignore.
   *
   * @return the property name
   */
  String value();

  /**
   * The copy direction(s) in which this property is ignored.
   *
   * @return the applicable type(s)
   */
  TypeEnum[] type();

}
