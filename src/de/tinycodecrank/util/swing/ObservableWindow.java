package de.tinycodecrank.util.swing;

import java.awt.Dialog.ModalityType;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Window;
import java.util.function.Consumer;

import javax.swing.JDialog;
import javax.swing.JFrame;

import de.tinycodecrank.monads.opt.Opt;
import de.tinycodecrank.util.swing.events.GuiCloseEvent;
import de.tinycodecrank.util.swing.events.GuiCloseEvent.Result;

public abstract class ObservableWindow<W extends Window, Logic extends LogicTemplate<?, Args>, R, Args>
{
	private static Opt<Image> defaultAppIcon = Opt.empty();
	
	private Opt<Consumer<GuiCloseEvent<R>>>	closeListener;
	protected Logic							logic;
	public W								window	= null;
	
	public ObservableWindow(Consumer<GuiCloseEvent<R>> closeListener)
	{
		this(closeListener, null);
	}
	
	public ObservableWindow(Consumer<GuiCloseEvent<R>> closeListener, Args args)
	{
		this.closeListener	= Opt.of(closeListener);
		this.logic			= createLogic(args);
	}
	
	public void createDialog(Window owner, ModalityType modality)
	{
		EventQueue.invokeLater(
			() -> new ViewDialog(
				owner,
				modality,
				this::preInitAction,
				this::initAction,
				this));
	}
	
	public void createFrame()
	{
		EventQueue.invokeLater(
			() -> new ViewFrame(
				this::preInitAction,
				this::initAction,
				this));
	}
	
	public void preInitAction(W gui)
	{}
	
	public abstract void initAction(W gui);
	
	protected abstract Logic createLogic(Args args);
	
	public void dispose()
	{
		dispose(new GuiCloseEvent<R>(Result.ABORT));
	}
	
	public final void dispose(GuiCloseEvent<R> event)
	{
		disposeAction();
		final var bl = logic;
		if (bl != null)
		{
			logic = null;
			bl.disposeAction();
			bl.dispose();
		}
		window.dispose();
		closeListener.if_(cl ->
		{
			this.closeListener = Opt.empty();
			cl.accept(event);
		});
	}
	
	public void disposeAction()
	{}
	
	@SuppressWarnings("serial")
	private class ViewFrame extends JFrame
	{
		@SuppressWarnings("unchecked")
		private ViewFrame(
			Consumer<W> preInitAction,
			Consumer<W> initAction,
			ObservableWindow<W, Logic, R, Args> observable)
		{
			observable.window = (W) this;
			preInitAction.accept((W) this);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			defaultAppIcon.if_(this::setIconImage);
			this.setVisible(true);
			initAction.accept((W) this);
		}
	}
	
	@SuppressWarnings("serial")
	private class ViewDialog extends JDialog
	{
		@SuppressWarnings("unchecked")
		private ViewDialog(
			Window owner,
			ModalityType modality,
			Consumer<W> preInitAction,
			Consumer<W> initAction,
			ObservableWindow<W, Logic, R, Args> observable)
		{
			super(owner, modality);
			observable.window = (W) this;
			preInitAction.accept((W) this);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			defaultAppIcon.if_(this::setIconImage);
			initAction.accept((W) this);
		}
	}
}