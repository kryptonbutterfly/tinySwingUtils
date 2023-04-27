package de.tinycodecrank.util.swing;

import de.tinycodecrank.monads.opt.Opt;

public abstract class LogicTemplate<Gui extends ObservableWindow<?, ?, ?, Args>, Args>
{
	protected Opt<Gui> gui;
	
	protected LogicTemplate(Gui gui)
	{
		this.gui = Opt.of(gui);
	}
	
	protected final void dispose()
	{
		this.gui = Opt.empty();
	}
	
	protected abstract void disposeAction();
}