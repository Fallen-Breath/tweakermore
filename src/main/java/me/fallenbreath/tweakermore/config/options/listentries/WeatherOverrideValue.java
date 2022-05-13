package me.fallenbreath.tweakermore.config.options.listentries;

public enum WeatherOverrideValue implements EnumOptionEntry
{
	CLEAR(false, false),
	RAIN(true, false),
	THUNDER(true, true);

	public static final WeatherOverrideValue DEFAULT = CLEAR;

	private final boolean raining;
	private final boolean thundering;

	WeatherOverrideValue(boolean raining, boolean thundering)
	{
		this.raining = raining;
		this.thundering = thundering;
	}

	public float getRainGradient()
	{
		return this.raining ? 1.0F : 0.0F;
	}

	public float getThunderGradient()
	{
		return this.thundering ? 1.0F : 0.0F;
	}

	@Override
	public WeatherOverrideValue[] getAllValues()
	{
		return values();
	}

	@Override
	public WeatherOverrideValue getDefault()
	{
		return DEFAULT;
	}

	@Override
	public String getTranslationPrefix()
	{
		return "tweakermore.list_entry.weatherOverrideValue.";
	}
}
