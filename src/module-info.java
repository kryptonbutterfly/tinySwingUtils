module kryptonbutterfly.SwingUtils
{
	opens kryptonbutterfly.util.swing.state;
	
	exports kryptonbutterfly.util.swing.events;
	exports kryptonbutterfly.util.swing.model;
	exports kryptonbutterfly.util.swing;
	exports kryptonbutterfly.util.swing.template;
	exports kryptonbutterfly.util.swing.template.settings;
	exports kryptonbutterfly.util.swing.state;
	
	requires kryptonbutterfly.Monads;
	requires transitive java.desktop;
	requires kryptonbutterfly.mathUtils;
	requires kryptonbutterfly.ReflectionUtils;
	requires lombok;
}