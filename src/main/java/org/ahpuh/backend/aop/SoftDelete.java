package org.ahpuh.backend.aop;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Where;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Where(clause = "is_deleted = false")
@DynamicInsert
public @interface SoftDelete {

}