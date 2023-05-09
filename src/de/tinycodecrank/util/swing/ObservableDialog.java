package de.tinycodecrank.util.swing;

import java.awt.Window;
import java.util.function.Consumer;

import javax.swing.JDialog;

import de.tinycodecrank.monads.opt.Opt;
import de.tinycodecrank.util.swing.events.GuiCloseEvent;
import de.tinycodecrank.util.swing.events.GuiCloseEvent.Result;

@SuppressWarnings("serial")
public abstract class ObservableDialog<BL extends Logic<?, Args>, R, Args> extends JDialog
{
	private Opt<Consumer<GuiCloseEvent<R>>>	closeListener;
	protected Opt<BL>						businessLogic;
	
	private BL createBusinessLogic()
	{
		return createBusinessLogic(null);
	}
	
	protected abstract BL createBusinessLogic(Args args);
	
	public ObservableDialog(Window owner, ModalityType modality, Consumer<GuiCloseEvent<R>> closeListener)
	{
		super(owner, modality);
		this.preInitAction();
		this.closeListener	= Opt.of(closeListener);
		this.businessLogic	= Opt.of(createBusinessLogic());
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		ObservableGui.getDefaultAppIcon().if_(this::setIconImage);
	}
	
	public ObservableDialog(Window owner, ModalityType modality, Consumer<GuiCloseEvent<R>> closeListener, Args args)
	{
		super(owner, modality);
		this.preInitAction();
		this.closeListener	= Opt.of(closeListener);
		this.businessLogic	= Opt.of(createBusinessLogic(args));
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		ObservableGui.getDefaultAppIcon().if_(this::setIconImage);
	}
	
	@Override
	public void dispose()
	{
		dispose(new GuiCloseEvent<R>(Result.ABORT));
	}
	
	public final void dispose(GuiCloseEvent<R> event)
	{
		disposeAction();
		this.businessLogic.if_(BL::disposeAction);
		this.businessLogic.if_(bL ->
		{
			this.businessLogic = Opt.empty();
			bL.dispose();
		});
		super.dispose();
		this.closeListener.if_(cL ->
		{
			this.closeListener = Opt.empty();
			cL.accept(event);
		});
	}
	
	public void preInitAction()
	{}
	
	public void disposeAction()
	{}
}