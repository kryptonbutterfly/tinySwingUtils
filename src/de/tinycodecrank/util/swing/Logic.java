package de.tinycodecrank.util.swing;

import java.awt.Window;

import de.tinycodecrank.monads.opt.Opt;

public abstract class Logic<Gui extends Window, Args>
{
	protected Opt<Gui> gui;
	
	protected Logic(Gui gui)
	{
		this.gui = Opt.of(gui);
	}
	
	protected final void dispose()
	{
		this.gui = Opt.empty();
	}
	
	protected void disposeAction()
	{}
}