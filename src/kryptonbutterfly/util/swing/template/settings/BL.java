package kryptonbutterfly.util.swing.template.settings;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.JTextComponent;

import kryptonbutterfly.monads.opt.Opt;
import kryptonbutterfly.reflectionUtils.Accessor;
import kryptonbutterfly.util.swing.ApplyAbortPanel;
import kryptonbutterfly.util.swing.Logic;
import lombok.SneakyThrows;

final class BL<Prefs, T extends Window> extends Logic<T, Consumer<T>>
{
	private static final ByteSpinner	defaultByteSpinner		= Defaults.of(ByteSpinner.class);
	private static final ShortSpinner	defaultShortSpinner		= Defaults.of(ShortSpinner.class);
	private static final IntSpinner		defaultIntSpinner		= Defaults.of(IntSpinner.class);
	private static final LongSpinner	defaultLongSpinner		= Defaults.of(LongSpinner.class);
	private static final FloatSpinner	defaultFloatSpinner		= Defaults.of(FloatSpinner.class);
	private static final DoubleSpinner	defaultDoubleSpinner	= Defaults.of(DoubleSpinner.class);
	private static final StringPref		defaultStringPref		= Defaults.of(StringPref.class);
	
	private final Consumer<T>			boundsPersister;
	private final ArrayList<Runnable>	persist	= new ArrayList<>();
	
	BL(T gui, Consumer<T> boundsPersister)
	{
		super(gui);
		this.boundsPersister = boundsPersister;
	}
	
	void abort(ActionEvent ae)
	{
		gui.if_(T::dispose);
	}
	
	void apply(ActionEvent ae)
	{
		gui.if_(gui -> {
			persist.forEach(Runnable::run);
			gui.dispose();
		});
	}
	
	@Override
	protected void disposeAction()
	{
		gui.if_(boundsPersister::accept);
	}
	
	@SneakyThrows
	void setPref(Box box, Field field, Prefs prefs)
	{
		final var	pref		= field.getAnnotation(Pref.class);
		final var	fieldType	= field.getType();
		
		if (field.isAnnotationPresent(ChoicePref.class))
			setChoicePref(box, field, prefs, pref);
		else if (fieldType.equals(boolean.class))
			setBoolPref(box, field, prefs, pref);
		else if (fieldType.equals(byte.class))
			setBytePref(box, field, prefs, pref);
		else if (fieldType.equals(short.class))
			setShortPref(box, field, prefs, pref);
		else if (fieldType.equals(int.class))
			setIntPref(box, field, prefs, pref);
		else if (fieldType.equals(long.class))
			setLongPref(box, field, prefs, pref);
		else if (fieldType.equals(float.class))
			setFloatPref(box, field, prefs, pref);
		else if (fieldType.equals(double.class))
			setDoublePref(box, field, prefs, pref);
		else if (fieldType.equals(String.class))
			setStringPref(box, field, prefs, pref);
		else
			System.err.printf("Can't add ui element for %s#%s%n", prefs.getClass(), field.getName());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SneakyThrows
	void setChoicePref(Box box, Field field, Prefs prefs, Pref pref)
	{
		final var annotation = field.getAnnotation(ChoicePref.class);
		if (annotation == null)
			throw new IllegalArgumentException(
				"Expected Field %s.%s to be annotated with %s."
					.formatted(pref.getClass().getName(), field.getName(), ChoicePref.class.getName()));
		
		final var description = new JLabel(pref.name());
		description.setToolTipText(pref.tooltip());
		
		final var	accessor	= new Accessor<>(prefs, field);
		final var	value		= accessor.perform(Field::get);
		
		final Values<?> supplier;
		if (annotation.valuesSupplier().isMemberClass())
			supplier = annotation.valuesSupplier().getConstructor(prefs.getClass()).newInstance(prefs);
		else
			supplier = annotation.valuesSupplier().getConstructor().newInstance();
		
		final var comboBox = new JComboBox<>(supplier.values());
		comboBox.setSelectedItem(value);
		comboBox.setToolTipText(pref.tooltip());
		
		final var rendererType = annotation.renderer();
		if (rendererType != DefaultListCellRenderer.class)
		{
			final var renderer = rendererType.getDeclaredConstructor().newInstance();
			comboBox.setRenderer((ListCellRenderer) renderer);
		}
		
		final var panel = new JPanel(new GridLayout(1, 2));
		panel.add(description);
		panel.add(comboBox);
		box.add(panel);
		
		persist.add(new Runnable()
		{
			@SneakyThrows
			public void run()
			{
				accessor.applyObj(Field::set, comboBox.getSelectedItem());
			}
		});
	}
	
	@SneakyThrows
	void setBoolPref(Box box, Field field, Prefs prefs, Pref pref)
	{
		final var checkbox = new JCheckBox();
		checkbox.setToolTipText(pref.tooltip());
		final var		accessor	= new Accessor<>(prefs, field);
		final boolean	value		= accessor.performBool(Field::getBoolean);
		checkbox.setSelected(value);
		
		final var description = new JLabel(pref.name());
		description.setToolTipText(pref.tooltip());
		
		final var panel = new JPanel(new GridLayout(1, 2));
		panel.add(description);
		panel.add(checkbox);
		box.add(panel);
		
		persist.add(new Runnable()
		{
			@SneakyThrows
			public void run()
			{
				accessor.applyBool(Field::setBoolean, checkbox.isSelected());
			}
		});
	}
	
	@SneakyThrows
	void setBytePref(Box box, Field field, Prefs prefs, Pref pref)
	{
		final var	annotation	= Opt.of(field.getAnnotation(ByteSpinner.class)).get(() -> defaultByteSpinner);
		final var	description	= new JLabel(pref.name());
		description.setToolTipText(pref.tooltip());
		
		final var	accessor	= new Accessor<>(prefs, field);
		final var	value		= accessor.perform(Field::getByte);
		
		final Byte	min			= annotation.min();
		final Byte	max			= annotation.max();
		final Byte	stepSize	= annotation.stepSize();
		
		final var spinner = new JSpinner(new SpinnerNumberModel(value, min, max, stepSize));
		spinner.setToolTipText(pref.tooltip());
		
		final var panel = new JPanel(new GridLayout(1, 2));
		panel.add(description);
		panel.add(spinner);
		box.add(panel);
		
		persist.add(new Runnable()
		{
			@SneakyThrows
			public void run()
			{
				accessor.applyByte(Field::setByte, (byte) spinner.getValue());
			}
		});
	}
	
	@SneakyThrows
	void setShortPref(Box box, Field field, Prefs prefs, Pref pref)
	{
		final var	annotation	= Opt.of(field.getAnnotation(ShortSpinner.class)).get(() -> defaultShortSpinner);
		final var	description	= new JLabel(pref.name());
		description.setToolTipText(pref.tooltip());
		
		final var	accessor	= new Accessor<>(prefs, field);
		final var	value		= accessor.perform(Field::getShort);
		
		final Short	min			= annotation.min();
		final Short	max			= annotation.max();
		final Short	stepSize	= annotation.stepSize();
		
		final var spinner = new JSpinner(new SpinnerNumberModel(value, min, max, stepSize));
		spinner.setToolTipText(pref.tooltip());
		
		final var panel = new JPanel(new GridLayout(1, 2));
		panel.add(description);
		panel.add(spinner);
		box.add(panel);
		
		persist.add(new Runnable()
		{
			@SneakyThrows
			public void run()
			{
				accessor.applyShort(Field::setShort, (short) spinner.getValue());
			}
		});
	}
	
	@SneakyThrows
	void setIntPref(Box box, Field field, Prefs prefs, Pref pref)
	{
		final var	annotation	= Opt.of(field.getAnnotation(IntSpinner.class)).get(() -> defaultIntSpinner);
		final var	description	= new JLabel(pref.name());
		description.setToolTipText(pref.tooltip());
		
		final var	accessor	= new Accessor<>(prefs, field);
		final int	value		= accessor.perform(Field::getInt);
		
		final int	min			= annotation.min();
		final int	max			= annotation.max();
		final int	stepSize	= annotation.stepSize();
		
		final var spinner = new JSpinner(new SpinnerNumberModel(value, min, max, stepSize));
		spinner.setToolTipText(pref.tooltip());
		
		final var panel = new JPanel(new GridLayout(1, 2));
		panel.add(description);
		panel.add(spinner);
		box.add(panel);
		
		persist.add(new Runnable()
		{
			@SneakyThrows
			public void run()
			{
				accessor.applyInt(Field::setInt, (int) spinner.getValue());
			}
		});
	}
	
	@SneakyThrows
	void setLongPref(Box box, Field field, Prefs prefs, Pref pref)
	{
		final var	annotation	= Opt.of(field.getAnnotation(LongSpinner.class)).get(() -> defaultLongSpinner);
		final var	description	= new JLabel(pref.name());
		description.setToolTipText(pref.tooltip());
		
		final var	accessor	= new Accessor<>(prefs, field);
		final var	value		= accessor.perform(Field::getLong);
		
		final Long	min			= annotation.min();
		final Long	max			= annotation.max();
		final Long	stepSize	= annotation.stepSize();
		
		final var spinner = new JSpinner(new SpinnerNumberModel(value, min, max, stepSize));
		spinner.setToolTipText(pref.tooltip());
		
		final var panel = new JPanel(new GridLayout(1, 2));
		panel.add(description);
		panel.add(spinner);
		box.add(panel);
		
		persist.add(new Runnable()
		{
			@SneakyThrows
			public void run()
			{
				accessor.applyLong(Field::setLong, (long) spinner.getValue());
			}
		});
	}
	
	@SneakyThrows
	void setFloatPref(Box box, Field field, Prefs prefs, Pref pref)
	{
		final var	annotation	= Opt.of(field.getAnnotation(FloatSpinner.class)).get(() -> defaultFloatSpinner);
		final var	description	= new JLabel(pref.name());
		description.setToolTipText(pref.tooltip());
		
		final var	accessor	= new Accessor<>(prefs, field);
		final var	value		= accessor.perform(Field::getFloat);
		
		final Float	min			= annotation.min();
		final Float	max			= annotation.max();
		final Float	stepSize	= annotation.stepSize();
		
		final var spinner = new JSpinner(new SpinnerNumberModel(value, min, max, stepSize));
		spinner.setToolTipText(pref.tooltip());
		
		final var panel = new JPanel(new GridLayout(1, 2));
		panel.add(description);
		panel.add(spinner);
		box.add(panel);
		
		persist.add(new Runnable()
		{
			@SneakyThrows
			public void run()
			{
				accessor.applyFloat(Field::setFloat, (float) spinner.getValue());
			}
		});
	}
	
	@SneakyThrows
	void setDoublePref(Box box, Field field, Prefs prefs, Pref pref)
	{
		final var	annotation	= Opt.of(field.getAnnotation(DoubleSpinner.class)).get(() -> defaultDoubleSpinner);
		final var	description	= new JLabel(pref.name());
		description.setToolTipText(pref.tooltip());
		
		final var	accessor	= new Accessor<>(prefs, field);
		final var	value		= accessor.perform(Field::getDouble);
		
		final Double	min			= annotation.min();
		final Double	max			= annotation.max();
		final Double	stepSize	= annotation.stepSize();
		
		final var spinner = new JSpinner(new SpinnerNumberModel(value, min, max, stepSize));
		spinner.setToolTipText(pref.tooltip());
		
		final var panel = new JPanel(new GridLayout(1, 2));
		panel.add(description);
		panel.add(spinner);
		box.add(panel);
		
		persist.add(new Runnable()
		{
			@SneakyThrows
			public void run()
			{
				accessor.applyDouble(Field::setDouble, (double) spinner.getValue());
			}
		});
	}
	
	@SneakyThrows
	void setStringPref(Box box, Field field, Prefs prefs, Pref pref)
	{
		final var	annotation	= Opt.of(field.getAnnotation(StringPref.class)).get(() -> defaultStringPref);
		final var	description	= new JLabel(pref.name());
		description.setToolTipText(pref.tooltip());
		
		final var	accessor	= new Accessor<>(prefs, field);
		final var	value		= accessor.perform(Field::get);
		
		final JTextComponent textArea;
		if (annotation.lines() > 1)
			textArea = new JTextArea((String) value, annotation.lines(), 0);
		else if (annotation.multiline())
			textArea = new JTextArea((String) value);
		else
			textArea = new JTextField((String) value);
		
		textArea.setToolTipText(pref.tooltip());
		
		final var panel = new JPanel(new GridLayout(1, 2));
		panel.add(description);
		panel.add(textArea);
		box.add(panel);
		
		persist.add(new Runnable()
		{
			@SneakyThrows
			public void run()
			{
				accessor.applyObj(Field::set, textArea.getText());
			}
		});
	}
	
	private static final class Defaults implements InvocationHandler
	{
		@SuppressWarnings("unchecked")
		private static <A extends Annotation> A of(Class<A> annotationType)
		{
			@SuppressWarnings("rawtypes")
			final Class[] interfaces = { annotationType };
			return (A) Proxy.newProxyInstance(annotationType.getClassLoader(), interfaces, new Defaults());
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
		{
			return method.getDefaultValue();
		}
	}
	
	static interface SettingsGui<Prefs, T extends Window>
	{
		int scrollStepSize();
		
		default void init(T gui, BL<Prefs, T> bl, Prefs prefs)
		{
			final var	vertBox	= Box.createVerticalBox();
			final var	content	= new JPanel(new BorderLayout());
			content.add(vertBox, BorderLayout.NORTH);
			
			final var scrollPane = new JScrollPane(
				content,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			
			scrollPane.getVerticalScrollBar().setUnitIncrement(scrollStepSize());
			scrollPane.getHorizontalScrollBar().setUnitIncrement(scrollStepSize());
			
			gui.add(scrollPane, BorderLayout.CENTER);
			
			Arrays.stream(prefs.getClass().getDeclaredFields())
				.filter(f -> f.isAnnotationPresent(Pref.class))
				.forEach(f -> bl.setPref(vertBox, f, prefs));
			
			content.add(Box.createVerticalGlue(), BorderLayout.CENTER);
			gui.add(new ApplyAbortPanel("abort", bl::abort, "apply", bl::apply), BorderLayout.SOUTH);
		}
	}
}