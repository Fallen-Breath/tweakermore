package me.fallenbreath.tweakermore.config.options.listentries;

public enum InfoViewRenderStrategy implements EnumOptionEntry
{
	HOTKEY_HELD,
	ALWAYS;

	public static final InfoViewRenderStrategy DEFAULT = HOTKEY_HELD;

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
		return "tweakermore.list_entry.infoViewRenderStrategy.";
	}
}
