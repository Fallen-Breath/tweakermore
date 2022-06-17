package me.fallenbreath.tweakermore.config.options.listentries;

public enum SchematicBlockPlacementRestrictionHintType implements EnumOptionEntry
{
	ALL(true, true),
	WRONG_ITEM_ONLY(false, true),
	NEVER(false, false);

	public static final SchematicBlockPlacementRestrictionHintType DEFAULT = ALL;

	public final boolean showNotPossible;
	public final boolean showWrongItem;

	SchematicBlockPlacementRestrictionHintType(boolean showNotPossible, boolean showWrongItem)
	{
		this.showNotPossible = showNotPossible;
		this.showWrongItem = showWrongItem;
	}

	@Override
	public SchematicBlockPlacementRestrictionHintType[] getAllValues()
	{
		return values();
	}

	@Override
	public SchematicBlockPlacementRestrictionHintType getDefault()
	{
		return DEFAULT;
	}

	@Override
	public String getTranslationPrefix()
	{
		return "tweakermore.list_entry.schematicBlockPlacementRestrictionHintType.";
	}
}
