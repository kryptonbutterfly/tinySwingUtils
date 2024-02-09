package kryptonbutterfly.util.swing.state;

import static kryptonbutterfly.math.utils.range.Range.*;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;

import kryptonbutterfly.math.utils.limit.LimitInt;
import kryptonbutterfly.monads.opt.Opt;
import kryptonbutterfly.util.swing.template.IWindowState;

public final class WindowState implements IWindowState
{
	private static final WindowState DEFAULTS = new WindowState();
	
	public WindowState()
	{}
	
	public WindowState(int posX, int posY, int width, int height)
	{
		this.posX	= posX;
		this.posY	= posY;
		this.height	= height;
	}
	
	public WindowState(int posX, int posY, int width, int height, ExtendedState state)
	{
		this.posX	= posX;
		this.posY	= posY;
		this.width	= width;
		this.height	= height;
		this.state	= state;
	}
	
	@PersistableValue
	public int				posX	= 100;
	@PersistableValue
	public int				posY	= 100;
	@PersistableValue
	public int				width	= 450;
	@PersistableValue
	public int				height	= 300;
	@PersistableValue
	public int				screen	= 0;
	@PersistableValue
	public ExtendedState	state	= ExtendedState.NORMAL;
	
	@Override
	public void setBounds(Component target)
	{
		final var	screens	= GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		final var	screen	= screens[LimitInt.clamp(0, this.screen, screens.length - 1)];
		final var	bounds	= screen.getDefaultConfiguration().getBounds();
		
		final int	x	= posX + bounds.x;
		final int	y	= posY + bounds.y;
		target.setBounds(x, y, width, height);
	}
	
	@Override
	public void setBoundsAndState(Frame target)
	{
		setBounds(target);
		target.setExtendedState(state.toIntState());
	}
	
	@Override
	public void persistBounds(Component source)
	{
		persistBounds(source, ExtendedState.NORMAL);
	}
	
	@Override
	public void persistBoundsAndState(Frame source)
	{
		persistBounds(source, ExtendedState.getState(source.getExtendedState()));
	}
	
	private void persistBounds(Component source, ExtendedState state)
	{
		if (GraphicsEnvironment.isHeadless())
			return;
		
		if (state == null)
		{
			System.err.printf(
				"Illegal Window state %s. Defaultong to %s and resetting bounds to default.",
				state,
				ExtendedState.NORMAL);
			this.state	= DEFAULTS.state;
			this.posX	= DEFAULTS.posX;
			this.posY	= DEFAULTS.posY;
			this.width	= DEFAULTS.width;
			this.height	= DEFAULTS.height;
			this.screen	= DEFAULTS.screen;
			return;
		}
		this.state = state;
		
		final var	DEFAULT_SCREEN_INDEX	= 0;
		final var	screens					= GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		
		var	currScreen		= source.getGraphicsConfiguration().getDevice();
		int	currScreenIndex	= findIndex(screens, currScreen, DEFAULT_SCREEN_INDEX);
		if (currScreenIndex == DEFAULT_SCREEN_INDEX && !findIndex(screens, this.screen).isPresent())
		{
			currScreenIndex	= Opt.of(this.screen)
				.filter(new LimitInt(0, screens.length - 1)::inRange)
				.get(() -> DEFAULT_SCREEN_INDEX);
			currScreen		= screens[currScreenIndex];
		}
		this.screen = currScreenIndex;
		
		final var screenBounds = currScreen.getDefaultConfiguration().getBounds();
		
		if (!source.isVisible())
			source.setVisible(true);
		final var loc = source.getLocationOnScreen();
		
		switch (state)
		{
		case NORMAL -> {
			this.posX	= loc.x - screenBounds.x;
			this.posY	= loc.y - screenBounds.y;
			this.width	= source.getWidth();
			this.height	= source.getHeight();
		}
		case ICONIFIED -> {} // Do nothing (preserve original size and position)
		case MAXIMIZED_BOTH -> {} // Do nothing (preserve original size and position)
		case MAXIMIZED_HORIZ -> {
			this.posY	= loc.y - screenBounds.y;
			this.height	= source.getHeight();
		}
		case MAXIMIZED_VERT -> {
			this.posX	= loc.x - screenBounds.x;
			this.width	= source.getWidth();
		}
		}
	}
	
	private static <E> Opt<Integer> findIndex(E[] array, E element)
	{
		for (int i : range(array.length))
			if (array[i] == element)
				return Opt.of(i);
		return Opt.empty();
	}
	
	private static <E> int findIndex(E[] array, E element, int fallback)
	{
		return findIndex(array, element).get(() -> fallback);
	}
	
	public static enum ExtendedState
	{
		NORMAL(Frame.NORMAL),
		ICONIFIED(Frame.ICONIFIED),
		MAXIMIZED_HORIZ(Frame.MAXIMIZED_HORIZ),
		MAXIMIZED_VERT(Frame.MAXIMIZED_VERT),
		MAXIMIZED_BOTH(Frame.MAXIMIZED_BOTH);
		
		private final int state;
		
		private ExtendedState(int state)
		{
			this.state = state;
		}
		
		public static ExtendedState getState(int state)
		{
			return switch (state)
			{
			case Frame.NORMAL -> NORMAL;
			case Frame.ICONIFIED -> ICONIFIED;
			case Frame.MAXIMIZED_HORIZ -> MAXIMIZED_HORIZ;
			case Frame.MAXIMIZED_VERT -> MAXIMIZED_VERT;
			case Frame.MAXIMIZED_BOTH -> MAXIMIZED_BOTH;
			default -> throw new IllegalArgumentException("Unexpected extended Window state '%s'.".formatted(state));
			};
		}
		
		public int toIntState()
		{
			return state;
		}
	}
}