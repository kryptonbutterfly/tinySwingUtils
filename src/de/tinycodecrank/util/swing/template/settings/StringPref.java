package de.tinycodecrank.util.swing.template.settings;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface StringPref
{
	boolean multiline() default false;
	
	int lines() default 1;
}