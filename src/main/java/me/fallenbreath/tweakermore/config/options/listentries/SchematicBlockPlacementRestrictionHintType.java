package me.fallenbreath.tweakermore.config.options.listentries;

public enum SchematicBlockPlacementRestrictionHintType implements EnumOptionEntry
{
	ALL(true, true, true),
	OPERATION_NOT_ALLOWED(false, true, true),
	WRONG_ITEM_ONLY(false, false, true),
	NEVER(false, false, false);

	public static final SchematicBlockPlacementRestrictionHintType DEFAULT = ALL;

	public final boolean showNotPossible;
	public final boolean showNotAllowed;
	public final boolean showWrongItem;

	SchematicBlockPlacementRestrictionHintType(boolean showNotPossible, boolean showNotAllowed, boolean showWrongItem)
	{
		this.showNotPossible = showNotPossible;
		this.showNotAllowed = showNotAllowed;
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
