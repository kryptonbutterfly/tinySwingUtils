package de.tinycodecrank.util.swing.template.settings;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface ShortSpinner
{
	short min() default Short.MIN_VALUE;
	
	short max() default Short.MAX_VALUE;
	
	short stepSize() default 1;
}