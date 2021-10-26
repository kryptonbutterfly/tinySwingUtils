package de.tinycodecrank.util.swing;

import de.tinycodecrank.monads.Opt;

public abstract class DialogLogicTemplate<Gui extends ObservableDialog<?, ?, Args>, Args>
{
	protected Opt<Gui> gui;
	
	protected DialogLogicTemplate(Gui gui)
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