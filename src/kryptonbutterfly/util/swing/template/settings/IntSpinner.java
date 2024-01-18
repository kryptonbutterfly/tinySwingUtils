package kryptonbutterfly.util.swing.template.settings;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface IntSpinner
{
	int min() default Integer.MIN_VALUE;
	
	int max() default Integer.MAX_VALUE;
	
	int stepSize() default 1;
}