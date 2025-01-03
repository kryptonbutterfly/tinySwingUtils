package kryptonbutterfly.util.swing.template.settings;

import java.util.function.Consumer;

import kryptonbutterfly.util.swing.ObservableGui;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;
import kryptonbutterfly.util.swing.state.WindowState;

@SuppressWarnings("serial")
public final class SettingsWindow<Prefs> extends ObservableGui<BL<Prefs, SettingsWindow<Prefs>>, Void, Consumer<SettingsWindow<Prefs>>> implements BL.SettingsGui<Prefs, SettingsWindow<Prefs>>
{
	final Prefs			prefs;
	private final int	scrollUnitIncrement;
	
	public SettingsWindow(
		Consumer<GuiCloseEvent<Void>> closeListener,
		WindowState windowState,
		Prefs prefs,
		int scrollUnitIncrement)
	{
		super(closeListener, windowState::persistBoundsAndState);
		this.scrollUnitIncrement	= scrollUnitIncrement;
		this.prefs					= prefs;
		windowState.setBoundsAndState(this);
		
		businessLogic.if_(bl -> init(this, bl, prefs));
		setVisible(true);
	}
	
	@Override
	protected BL<Prefs, SettingsWindow<Prefs>> createBusinessLogic(Consumer<SettingsWindow<Prefs>> args)
	{
		return new BL<>(this, args);
	}
	
	@Override
	public int scrollStepSize()
	{
		return scrollUnitIncrement;
	}
}