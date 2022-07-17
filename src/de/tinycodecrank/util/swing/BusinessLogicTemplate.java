package de.tinycodecrank.util.swing;

import de.tinycodecrank.monads.opt.Opt;

public abstract class BusinessLogicTemplate<Gui extends ObservableGui<?, ?, Args>, Args>
{
	protected Opt<Gui> gui;
	
	protected BusinessLogicTemplate(Gui gui)
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