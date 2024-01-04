module de.tinycodecrank.SwingUtils
{
	exports de.tinycodecrank.util.swing.events;
	exports de.tinycodecrank.util.swing.model;
	exports de.tinycodecrank.util.swing;
	exports de.tinycodecrank.util.swing.template;
	exports de.tinycodecrank.util.swing.template.settings;
	
	requires de.tinycodecrank.Monads;
	requires transitive java.desktop;
	requires de.tinycodecrank.mathUtils;
	requires de.tinycodecrank.ReflectionUtils;
	requires lombok;
}