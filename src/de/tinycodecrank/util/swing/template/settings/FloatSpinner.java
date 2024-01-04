package de.tinycodecrank.util.swing.template.settings;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface FloatSpinner
{
	float min() default 0;
	
	float max() default 1;
	
	float stepSize() default 0.05F;
}