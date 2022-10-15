package me.fallenbreath.tweakermore.config.options.listentries;

public enum InfoViewStrategy implements EnumOptionEntry
{
	HOTKEY_HELD,
	POINTED,
	BEAM;

	public static final InfoViewStrategy DEFAULT = HOTKEY_HELD;

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
		return "tweakermore.list_entry.infoViewStrategy.";
	}
}
