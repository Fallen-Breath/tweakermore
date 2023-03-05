package me.fallenbreath.tweakermore.config.options.listentries;

public enum AutoCollectMaterialListItemLogType implements EnumOptionEntry
{
	FULL,
	SUMMARY;

	public static final AutoCollectMaterialListItemLogType DEFAULT = FULL;

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
		return "tweakermore.list_entry.autoCollectMaterialListItemMessageType.";
	}
}
