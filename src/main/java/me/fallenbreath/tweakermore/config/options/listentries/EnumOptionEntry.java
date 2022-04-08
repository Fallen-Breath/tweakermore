package me.fallenbreath.tweakermore.config.options.listentries;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

import java.util.Arrays;

public interface EnumOptionEntry extends IConfigOptionListEntry
{
	////////////////////////////////////////////
	//     enum classes should have these     //
	////////////////////////////////////////////

	String name();
	int ordinal();
	EnumOptionEntry[] values();

	////////////////////////////////////////////
	//            to be implemented           //
	////////////////////////////////////////////

	/**
	 * The default/fallback value
	 */
	EnumOptionEntry getDefault();

	/**
	 * The translation key prefix, e.g. "tweakermore.value.my_value_type."
	 */
	String getTranslationPrefix();

	////////////////////////////////////////////////////
	//   implementations for IConfigOptionListEntry   //
	////////////////////////////////////////////////////

	@Override
	default String getStringValue()
	{
		return this.name().toLowerCase();
	}

	@Override
	default String getDisplayName()
	{
		return StringUtils.translate(this.getTranslationPrefix() + this.name().toLowerCase());
	}

	@Override
	default IConfigOptionListEntry cycle(boolean forward)
	{
		int index = this.ordinal();
		EnumOptionEntry[] values = this.values();

		index += forward ? 1 : -1;
		index = (index + values.length) % values.length;

		return values()[index];
	}

	@Override
	default IConfigOptionListEntry fromString(String value)
	{
		return Arrays.stream(values()).
				filter(o -> o.name().equalsIgnoreCase(value)).
				findFirst().
				orElseGet(this::getDefault);
	}
}
