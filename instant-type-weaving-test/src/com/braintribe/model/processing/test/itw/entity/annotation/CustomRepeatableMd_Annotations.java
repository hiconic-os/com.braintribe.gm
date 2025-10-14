// ============================================================================
package com.braintribe.model.processing.test.itw.entity.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Container for {@link Repeatable} annotation {@link CustomRepeatableMd_Annotation}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Documented
public @interface CustomRepeatableMd_Annotations {
	CustomRepeatableMd_Annotation[] value();
}
