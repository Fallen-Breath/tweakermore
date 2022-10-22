package me.fallenbreath.tweakermore.util;

/**
 * A runnable wrapper that only runs the delegate Runnable at the first invocation
 */
public class ThrowawayRunnable implements Runnable
{
	private final Runnable delegate;
	private boolean processed;

	private ThrowawayRunnable(Runnable delegate)
	{
		this.delegate = delegate;
		this.processed = false;
	}
	
	public static ThrowawayRunnable of(Runnable runnable)
	{
		return new ThrowawayRunnable(runnable);
	}

	@Override
	public void run()
	{
		if (!this.processed)
		{
			this.processed = true;
			this.delegate.run();
		}
	}
}
