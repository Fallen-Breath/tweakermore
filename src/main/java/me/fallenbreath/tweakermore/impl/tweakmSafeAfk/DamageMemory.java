package me.fallenbreath.tweakermore.impl.tweakmSafeAfk;

public class DamageMemory
{
	private static long lastHurtMs = 0;
	private static final long MAX_TIME_WAIT = 15 * 1000;  // 15s

	public static void resetTime()
	{
		lastHurtMs = -MAX_TIME_WAIT;
	}

	public static void recordTime()
	{
		lastHurtMs = System.currentTimeMillis();
	}

	public static boolean hasRecord()
	{
		return System.currentTimeMillis() - lastHurtMs <= MAX_TIME_WAIT;
	}
}
