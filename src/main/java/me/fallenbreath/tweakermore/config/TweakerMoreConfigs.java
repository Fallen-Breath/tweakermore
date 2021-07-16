package me.fallenbreath.tweakermore.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.options.ConfigBooleanHotkeyed;
import fi.dy.masa.malilib.config.options.ConfigDouble;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import fi.dy.masa.malilib.config.options.ConfigStringList;
import fi.dy.masa.malilib.util.restrictions.ItemRestriction;
import fi.dy.masa.malilib.util.restrictions.UsageRestriction;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;

public class TweakerMoreConfigs
{
	// Generic
	public static final ConfigDouble NETHER_PORTAL_SOUND_CHANCE = new ConfigDouble("netherPortalSoundChance", 0.01D, 0.0D, 0.01D, "The chance for a nether portal block to play sound\nSet it to 0.001 or 0.0001 for less noisy portal");

	// List
	public static final ConfigOptionList HAND_RESTORE_LIST_TYPE = new ConfigOptionList("handRestockListType", UsageRestriction.ListType.NONE, "The item restriction type for tweakHandRestock");
	public static final ConfigStringList HAND_RESTORE_WHITELIST = new ConfigStringList("handRestockWhiteList", ImmutableList.of(getItemId(Items.BUCKET)), "The items that will trigger tweakHandRestock");
	public static final ConfigStringList HAND_RESTORE_BLACKLIST = new ConfigStringList("handRestockBlackList", ImmutableList.of(getItemId(Items.LAVA_BUCKET)), "The items that will NOT trigger tweakHandRestock");
	public static final ItemRestriction HAND_RESTORE_RESTRICTION = new ItemRestriction();

	private static String getItemId(Item item)
	{
		return Registry.ITEM.getId(item).toString();
	}

	// Disable
	public static final ConfigBooleanHotkeyed DISABLE_LIGHT_UPDATES = new ConfigBooleanHotkeyed("disableLightUpdates", false, "", "Yeets client-side light updates");
}
