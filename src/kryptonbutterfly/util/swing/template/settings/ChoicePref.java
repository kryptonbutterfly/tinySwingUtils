package kryptonbutterfly.util.swing.template.settings;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ListCellRenderer;

@Retention(RUNTIME)
@Target(FIELD)
public @interface ChoicePref
{
	public Class<? extends Values<?>> valuesSupplier();
	
	public Class<? extends ListCellRenderer<?>> renderer() default DefaultListCellRenderer.class;
}