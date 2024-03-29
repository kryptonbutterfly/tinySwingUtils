package kryptonbutterfly.util.swing;

import java.awt.Image;
import java.util.function.Consumer;

import javax.swing.JFrame;

import kryptonbutterfly.monads.opt.Opt;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;
import kryptonbutterfly.util.swing.events.GuiCloseEvent.Result;

public abstract class ObservableGui<BL extends Logic<?, Args>, R, Args> extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	private Opt<Consumer<GuiCloseEvent<R>>>	closeListener;
	protected Opt<BL>						businessLogic;
	
	private BL createBusinessLogic()
	{
		
		// FIXME null
		return createBusinessLogic(null);
	}
	
	protected abstract BL createBusinessLogic(Args args);
	
	private static Opt<Image> defaultAppIcon = Opt.empty();
	
	public ObservableGui(Consumer<GuiCloseEvent<R>> closeListener)
	{
		this.preInitAction();
		this.closeListener	= Opt.of(closeListener);
		this.businessLogic	= Opt.of(createBusinessLogic());
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		defaultAppIcon.if_(this::setIconImage);
		this.setVisible(true);
	}
	
	public ObservableGui(Consumer<GuiCloseEvent<R>> closeListener, Args args)
	{
		this.preInitAction();
		this.closeListener	= Opt.of(closeListener);
		this.businessLogic	= Opt.of(createBusinessLogic(args));
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		defaultAppIcon.if_(this::setIconImage);
		this.setVisible(true);
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
		this.businessLogic.if_(bL -> {
			this.businessLogic = Opt.empty();
			bL.dispose();
		});
		super.dispose();
		this.closeListener.if_(cL -> {
			this.closeListener = Opt.empty();
			cL.accept(event);
		});
	}
	
	public void preInitAction()
	{}
	
	public void disposeAction()
	{}
	
	public static void setDefaultAppImage(Opt<Image> defaultAppIcon)
	{
		ObservableGui.defaultAppIcon = defaultAppIcon;
	}
	
	public static final Opt<Image> getDefaultAppIcon()
	{
		return defaultAppIcon;
	}
}