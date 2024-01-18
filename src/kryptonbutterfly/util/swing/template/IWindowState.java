package kryptonbutterfly.util.swing.template;

import java.awt.Component;
import java.awt.Frame;

public interface IWindowState
{
	/**
	 * Sets the bounds of the supplied target.
	 */
	public void setBounds(Component target);
	
	/**
	 * Sets the bounds and extended state of the target.
	 */
	public void setBoundsAndState(Frame target);
	
	/**
	 * Retrieves the bounds of the source.
	 */
	public void persistBounds(Component source);
	
	/**
	 * Retrieves the bounds and extended state of the target.
	 */
	public void persistBoundsAndState(Frame source);
}