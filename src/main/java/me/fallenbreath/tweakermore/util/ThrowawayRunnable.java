package me.fallenbreath.tweakermore.util;

public class ThrowawayRunnable implements Runnable
{
	private final Runnable runnable;
	private boolean processed;

	private ThrowawayRunnable(Runnable runnable)
	{
		this.runnable = runnable;
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
			this.runnable.run();
		}
	}
}
