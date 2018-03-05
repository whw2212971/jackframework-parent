package org.jackframework.component.mvc.spring;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ServiceMapping {

    String value() default "";

}
