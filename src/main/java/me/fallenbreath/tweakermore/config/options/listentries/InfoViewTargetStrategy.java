package me.fallenbreath.tweakermore.config.options.listentries;

public enum InfoViewTargetStrategy implements EnumOptionEntry
{
	POINTED,
	BEAM;

	public static final InfoViewTargetStrategy DEFAULT = POINTED;

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
		return "tweakermore.list_entry.infoViewTargetStrategy.";
	}
}
