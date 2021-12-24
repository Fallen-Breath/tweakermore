package me.fallenbreath.tweakermore.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fi.dy.masa.malilib.config.options.*;
import fi.dy.masa.malilib.util.restrictions.ItemRestriction;
import fi.dy.masa.malilib.util.restrictions.UsageRestriction;
import me.fallenbreath.tweakermore.config.annotations.DisableConfig;
import me.fallenbreath.tweakermore.config.annotations.GenericConfig;
import me.fallenbreath.tweakermore.config.annotations.ListConfig;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

public class TweakerMoreConfigs
{
	@GenericConfig
	public static final ConfigInteger AUTO_FILL_CONTAINER_THRESHOLD = new ConfigInteger("autoFillContainerThreshold", 2, 1, 36, "autoFillContainerThreshold.comment");
	@GenericConfig
	public static final ConfigDouble NETHER_PORTAL_SOUND_CHANCE = new ConfigDouble("netherPortalSoundChance", 0.01D, 0.0D, 0.01D, "netherPortalSoundChance.comment");
	@GenericConfig
	public static final ConfigBoolean VILLAGER_OFFER_USES_DISPLAY = new ConfigBoolean("villagerOfferUsesDisplay", false, "villagerOfferUsesDisplay.comment");

	@ListConfig
	public static final ConfigOptionList HAND_RESTORE_LIST_TYPE = new ConfigOptionList("handRestockListType", UsageRestriction.ListType.NONE, "handRestockListType.comment");
	@ListConfig
	public static final ConfigStringList HAND_RESTORE_WHITELIST = new ConfigStringList("handRestockWhiteList", ImmutableList.of(getItemId(Items.BUCKET)), "handRestockWhiteList.comment");
	@ListConfig
	public static final ConfigStringList HAND_RESTORE_BLACKLIST = new ConfigStringList("handRestockBlackList", ImmutableList.of(getItemId(Items.LAVA_BUCKET)), "handRestockBlackList.comment");
	public static final ItemRestriction HAND_RESTORE_RESTRICTION = new ItemRestriction();

	@DisableConfig
	public static final ConfigBooleanHotkeyed DISABLE_LIGHT_UPDATES = new ConfigBooleanHotkeyed("disableLightUpdates", false, "", "disableLightUpdates.comment", "Disable Light Updates");
	@DisableConfig
	public static final ConfigBooleanHotkeyed DISABLE_REDSTONE_WIRE_PARTICLE = new ConfigBooleanHotkeyed("disableRedstoneWireParticle", false, "", "disableRedstoneWireParticle.comment", "Disable particle of redstone wire");

	private static String getItemId(Item item)
	{
		return Registry.ITEM.getId(item).toString();
	}

	@SuppressWarnings("unchecked")
	public static <T> ImmutableList<T> updateOptionList(ImmutableList<T> originalConfig, Class<? extends Annotation> annotationClass)
	{
		List<T> optionList = Lists.newArrayList(originalConfig);
		for (Field field : TweakerMoreConfigs.class.getDeclaredFields())
		{
			if (field.getAnnotation(annotationClass) != null)
			{
				try
				{
					optionList.add((T)field.get(null));
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}
		return ImmutableList.copyOf(optionList);
	}
}
