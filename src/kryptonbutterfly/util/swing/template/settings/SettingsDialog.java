package kryptonbutterfly.util.swing.template.settings;

import java.awt.Window;
import java.util.function.Consumer;

import kryptonbutterfly.util.swing.ObservableDialog;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;
import kryptonbutterfly.util.swing.template.IWindowState;

@SuppressWarnings("serial")
public final class SettingsDialog<Prefs> extends ObservableDialog<BL<Prefs, SettingsDialog<Prefs>>, Void, Consumer<SettingsDialog<Prefs>>> implements BL.SettingsGui<Prefs, SettingsDialog<Prefs>>
{
	final Prefs			prefs;
	private final int	scrollUnitIncrement;
	
	public SettingsDialog(
		Window owner,
		ModalityType modality,
		Consumer<GuiCloseEvent<Void>> closeListener,
		IWindowState windowState,
		Prefs prefs,
		int scrollUnitIncrement)
	{
		super(owner, modality, closeListener, windowState::persistBounds);
		this.scrollUnitIncrement	= scrollUnitIncrement;
		this.prefs					= prefs;
		windowState.setBounds(this);
		
		businessLogic.if_(bl -> init(this, bl, prefs));
		setVisible(true);
	}
	
	@Override
	protected BL<Prefs, SettingsDialog<Prefs>> createBusinessLogic(Consumer<SettingsDialog<Prefs>> args)
	{
		return new BL<>(this, args);
	}
	
	@Override
	public int scrollStepSize()
	{
		return scrollUnitIncrement;
	}
}