package me.fallenbreath.tweakermore.config.options.listentries;

public enum OptifineExtraModelRenderStrategy implements EnumOptionEntry
{
	UNTOUCHED,
	ME,
	ALL;

	public static final OptifineExtraModelRenderStrategy DEFAULT = UNTOUCHED;

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
		return "tweakermore.list_entry.optifineExtraModelRenderStrategy.";
	}
}
