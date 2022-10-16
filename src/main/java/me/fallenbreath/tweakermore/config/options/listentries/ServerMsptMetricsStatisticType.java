package me.fallenbreath.tweakermore.config.options.listentries;

public enum ServerMsptMetricsStatisticType implements EnumOptionEntry
{
	GAME_TICK,
	SECOND_AVG,
	SECOND_MAX,
	MINUTE_AVG,
	MINUTE_MAX;

	public static final ServerMsptMetricsStatisticType USE_VANILLA = GAME_TICK;
	public static final ServerMsptMetricsStatisticType DEFAULT = USE_VANILLA;

	@Override
	public EnumOptionEntry[] getAllValues()
	{
		return values();
	}

	@Override
	public EnumOptionEntry getDefault()
	{
		return DEFAULT;
	}

	@Override
	public String getTranslationPrefix()
	{
		return "tweakermore.list_entry.serverMsptMetricsStatistic.";
	}
}
