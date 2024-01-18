package kryptonbutterfly.util.swing.template.settings;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface ByteSpinner
{
	byte min() default Byte.MIN_VALUE;
	
	byte max() default Byte.MAX_VALUE;
	
	byte stepSize() default 1;
}