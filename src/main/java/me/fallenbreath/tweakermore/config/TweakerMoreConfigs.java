package me.fallenbreath.tweakermore.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.*;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.restrictions.ItemRestriction;
import fi.dy.masa.malilib.util.restrictions.UsageRestriction;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.annotations.Config;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TweakerMoreConfigs
{
	@Config(Config.Type.GENERIC)
	public static final ConfigInteger AUTO_FILL_CONTAINER_THRESHOLD = new ConfigInteger("autoFillContainerThreshold", 2, 1, 36, "autoFillContainerThreshold.comment");
	@Config(Config.Type.GENERIC)
	public static final ConfigDouble NETHER_PORTAL_SOUND_CHANCE = new ConfigDouble("netherPortalSoundChance", 0.01D, 0.0D, 0.01D, "netherPortalSoundChance.comment");
	@Config(Config.Type.GENERIC)
	public static final ConfigBoolean VILLAGER_OFFER_USES_DISPLAY = new ConfigBoolean("villagerOfferUsesDisplay", false, "villagerOfferUsesDisplay.comment");
	@Config(Config.Type.GENERIC)
	public static final ConfigBoolean SHULKER_TOOLTIP_ENCHANTMENT_HINT = new ConfigBoolean("shulkerTooltipEnchantmentHint", false, "shulkerTooltipEnchantmentHint.comment");

	@Config(Config.Type.LIST)
	public static final ConfigOptionList HAND_RESTORE_LIST_TYPE = new ConfigOptionList("handRestockListType", UsageRestriction.ListType.NONE, "handRestockListType.comment");
	@Config(Config.Type.LIST)
	public static final ConfigStringList HAND_RESTORE_WHITELIST = new ConfigStringList("handRestockWhiteList", ImmutableList.of(getItemId(Items.BUCKET)), "handRestockWhiteList.comment");
	@Config(Config.Type.LIST)
	public static final ConfigStringList HAND_RESTORE_BLACKLIST = new ConfigStringList("handRestockBlackList", ImmutableList.of(getItemId(Items.LAVA_BUCKET)), "handRestockBlackList.comment");
	public static final ItemRestriction HAND_RESTORE_RESTRICTION = new ItemRestriction();

	@Config(Config.Type.DISABLE)
	public static final ConfigBooleanHotkeyed DISABLE_LIGHT_UPDATES = new ConfigBooleanHotkeyed("disableLightUpdates", false, "", "disableLightUpdates.comment", "Disable Light Updates");
	@Config(Config.Type.DISABLE)
	public static final ConfigBooleanHotkeyed DISABLE_REDSTONE_WIRE_PARTICLE = new ConfigBooleanHotkeyed("disableRedstoneWireParticle", false, "", "disableRedstoneWireParticle.comment", "Disable particle of redstone wire");

	private static String getItemId(Item item)
	{
		return Registry.ITEM.getId(item).toString();
	}

	private static final Map<Config.Type, List<IConfigBase>> OPTION_SETS = Util.make(() -> {
		HashMap<Config.Type, List<IConfigBase>> map = Maps.newHashMap();
		map.put(Config.Type.TOGGLE, new ArrayList<>(TweakerMoreToggles.getFeatureToggles()));
		for (Field field : TweakerMoreConfigs.class.getDeclaredFields())
		{
			Config annotation = field.getAnnotation(Config.class);
			if (annotation != null)
			{
				try
				{
					IConfigBase option = (IConfigBase)field.get(null);
					for (Config.Type type : annotation.value())
					{
						map.computeIfAbsent(type, key -> Lists.newArrayList()).add(option);
					}
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}
		return map;
	});

	@SuppressWarnings("unchecked")
	public static <T extends IConfigBase> List<T> getOptions(Config.Type optionType)
	{
		return (List<T>)OPTION_SETS.getOrDefault(optionType, Lists.newArrayList());
	}

	public static <T extends IConfigBase> ImmutableList<T> updateOptionList(ImmutableList<T> originalConfig, Config.Type optionType)
	{
		List<T> optionList = Lists.newArrayList(originalConfig);
		optionList.addAll(getOptions(optionType));
		return ImmutableList.copyOf(optionList);
	}

	private static final String CONFIG_FILE_NAME = TweakerMoreMod.MOD_ID + ".json";

	private static File getConfigFile()
	{
		return FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE_NAME).toFile();
	}

	public static void loadFromFile()
	{
		File configFile = getConfigFile();
		if (configFile.exists() && configFile.isFile() && configFile.canRead())
		{
			JsonElement element = JsonUtils.parseJsonFile(configFile);

			if (element != null && element.isJsonObject())
			{
				JsonObject root = element.getAsJsonObject();

				ConfigUtils.readConfigBase(root, "Generic", getOptions(Config.Type.GENERIC));
				ConfigUtils.readConfigBase(root, "Lists", getOptions(Config.Type.LIST));
				ConfigUtils.readHotkeyToggleOptions(root, "TweakHotkeys", "TweakToggles", getOptions(Config.Type.TOGGLE));
				ConfigUtils.readHotkeyToggleOptions(root, "DisableHotkeys", "DisableToggles", getOptions(Config.Type.DISABLE));
			}
		}
	}

	public static void saveToFile()
	{
		File configFile = getConfigFile();
		JsonObject root = new JsonObject();

		ConfigUtils.writeConfigBase(root, "Generic", getOptions(Config.Type.GENERIC));
		ConfigUtils.writeConfigBase(root, "Lists", getOptions(Config.Type.LIST));
		ConfigUtils.writeHotkeyToggleOptions(root, "TweakHotkeys", "TweakToggles", getOptions(Config.Type.TOGGLE));
		ConfigUtils.writeHotkeyToggleOptions(root, "DisableHotkeys", "DisableToggles", getOptions(Config.Type.DISABLE));

		JsonUtils.writeJsonToFile(root, configFile);
	}
}
