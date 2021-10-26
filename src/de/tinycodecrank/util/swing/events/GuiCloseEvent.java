package de.tinycodecrank.util.swing.events;

import de.tinycodecrank.monads.Opt;

public class GuiCloseEvent<C>
{
	private final Opt<Runnable>	consequence;
	private final Result		result;
	private final Opt<C>		returnValue;
	
	public GuiCloseEvent(Result result)
	{
		this(result, Opt.empty(), Opt.empty());
	}
	
	public GuiCloseEvent(Result result, Runnable consequence)
	{
		this(result, Opt.of(consequence), Opt.empty());
	}
	
	public GuiCloseEvent(Result result, Opt<Runnable> consequence, C returnValue)
	{
		this(result, consequence, Opt.of(returnValue));
	}
	
	public GuiCloseEvent(Result result, Runnable consequence, C returnValue)
	{
		this(result, Opt.of(consequence), Opt.of(returnValue));
	}
	
	public GuiCloseEvent(Result result, Opt<Runnable> consequence, Opt<C> returnValue)
	{
		this.result			= result;
		this.consequence	= consequence;
		this.returnValue	= returnValue;
	}
	
	public boolean success()
	{
		return result == Result.SUCCESS;
	}
	
	public void runConsequence()
	{
		this.consequence.if_(Runnable::run);
	}
	
	public Opt<C> getReturnValue()
	{
		return this.returnValue;
	}
	
	public static enum Result
	{
		SUCCESS,
		ABORT;
	}
}