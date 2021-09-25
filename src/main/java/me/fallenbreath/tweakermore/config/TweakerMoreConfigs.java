package me.fallenbreath.tweakermore.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.options.*;
import fi.dy.masa.malilib.util.restrictions.ItemRestriction;
import fi.dy.masa.malilib.util.restrictions.UsageRestriction;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;

public class TweakerMoreConfigs
{
	// Generic
	public static final ConfigInteger AUTO_FILL_CONTAINER_THRESHOLD = new ConfigInteger("autoFillContainerThreshold", 2, 1, 36, "tweakermore.autoFillContainerThreshold.comment");
	public static final ConfigDouble NETHER_PORTAL_SOUND_CHANCE = new ConfigDouble("netherPortalSoundChance", 0.01D, 0.0D, 0.01D, "tweakermore.netherPortalSoundChance.comment");

	// List
	public static final ConfigOptionList HAND_RESTORE_LIST_TYPE = new ConfigOptionList("handRestockListType", UsageRestriction.ListType.NONE, "handRestockListType.comment");
	public static final ConfigStringList HAND_RESTORE_WHITELIST = new ConfigStringList("handRestockWhiteList", ImmutableList.of(getItemId(Items.BUCKET)), "handRestockWhiteList.comment");
	public static final ConfigStringList HAND_RESTORE_BLACKLIST = new ConfigStringList("handRestockBlackList", ImmutableList.of(getItemId(Items.LAVA_BUCKET)), "handRestockBlackList.comment");
	public static final ItemRestriction HAND_RESTORE_RESTRICTION = new ItemRestriction();

	// Disable
	public static final ConfigBooleanHotkeyed DISABLE_LIGHT_UPDATES = new ConfigBooleanHotkeyed("disableLightUpdates", false, "", "disableLightUpdates.comment", "Disable Light Updates");

	private static String getItemId(Item item)
	{
		return Registry.ITEM.getId(item).toString();
	}
}
