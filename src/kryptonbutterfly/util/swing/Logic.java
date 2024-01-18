package kryptonbutterfly.util.swing;

import java.awt.Window;

import kryptonbutterfly.monads.opt.Opt;

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