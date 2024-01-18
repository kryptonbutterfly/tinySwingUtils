package kryptonbutterfly.util.swing.template.settings;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface LongSpinner
{
	long min() default Long.MIN_VALUE;
	
	long max() default Long.MAX_VALUE;
	
	long stepSize() default 1;
}