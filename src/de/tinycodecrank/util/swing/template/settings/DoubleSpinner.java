package de.tinycodecrank.util.swing.template.settings;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface DoubleSpinner
{
	double min() default Double.MIN_VALUE;
	
	double max() default Double.MAX_VALUE;
	
	double stepSize() default 1.0F;
}