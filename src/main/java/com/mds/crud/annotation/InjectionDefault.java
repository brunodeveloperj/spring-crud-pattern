package com.mds.crud.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AliasFor;

/**
 * Marks the field that should be treated as the default injected service or
 * repository in {@link com.mds.crud.base.AbstractResourceBase} and
 * {@link com.mds.crud.base.AbstractServiceBase}.
 *
 * @author MDS
 * @since 0.0.1-SNAPSHOT
 */
@Autowired
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface InjectionDefault {

}
