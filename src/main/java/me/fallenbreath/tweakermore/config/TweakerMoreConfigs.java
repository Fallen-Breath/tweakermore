package me.fallenbreath.tweakermore.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.*;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.restrictions.ItemRestriction;
import fi.dy.masa.malilib.util.restrictions.UsageRestriction;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.util.RegistryUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Items;
import net.minecraft.util.Util;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.fallenbreath.tweakermore.util.mixin.ModIds.*;

public class TweakerMoreConfigs implements IConfigHandler
{
	/**
	 * ============================
	 *     Config Declarations
	 * ============================
	 */

	////////////////////
	//    MC Tweaks   //
	////////////////////

	@Config(Config.Type.GENERIC)
	public static final ConfigInteger AUTO_FILL_CONTAINER_THRESHOLD = new ConfigInteger("autoFillContainerThreshold", 2, 1, 36, "autoFillContainerThreshold.comment");
	@Config(Config.Type.GENERIC)
	public static final ConfigDouble NETHER_PORTAL_SOUND_CHANCE = new ConfigDouble("netherPortalSoundChance", 0.01D, 0.0D, 0.01D, "netherPortalSoundChance.comment");
	@Config(Config.Type.GENERIC)
	public static final ConfigBoolean VILLAGER_OFFER_USES_DISPLAY = new ConfigBoolean("villagerOfferUsesDisplay", false, "villagerOfferUsesDisplay.comment");
	@Config(Config.Type.GENERIC)
	public static final ConfigBoolean SHULKER_TOOLTIP_ENCHANTMENT_HINT = new ConfigBoolean("shulkerTooltipEnchantmentHint", false, "shulkerTooltipEnchantmentHint.comment");
	@Config(Config.Type.GENERIC)
	public static final ConfigInteger CHAT_MESSAGE_LIMIT = new ConfigInteger("chatMessageLimit", 100, 100, 10000, "chatMessageLimit.comment");

	@Config(Config.Type.HOTKEY)
	public static final ConfigHotkey COPY_SIGN_TEXT_TO_CLIPBOARD = new ConfigHotkey("copySignTextToClipBoard", "", "copySignTextToClipBoard.comment");

	@Config(Config.Type.LIST)
	public static final ConfigOptionList HAND_RESTORE_LIST_TYPE = new ConfigOptionList("handRestockListType", UsageRestriction.ListType.NONE, "handRestockListType.comment");
	@Config(Config.Type.LIST)
	public static final ConfigStringList HAND_RESTORE_WHITELIST = new ConfigStringList("handRestockWhiteList", ImmutableList.of(RegistryUtil.getItemId(Items.BUCKET)), "handRestockWhiteList.comment");
	@Config(Config.Type.LIST)
	public static final ConfigStringList HAND_RESTORE_BLACKLIST = new ConfigStringList("handRestockBlackList", ImmutableList.of(RegistryUtil.getItemId(Items.LAVA_BUCKET)), "handRestockBlackList.comment");
	public static final ItemRestriction HAND_RESTORE_RESTRICTION = new ItemRestriction();

	@Config(value = Config.Type.TWEAK, modRequire = itemscroller)
	public static final ConfigBooleanHotkeyed TWEAKM_AUTO_CLEAN_CONTAINER = new ConfigBooleanHotkeyed("tweakmAutoCleanContainer", false, "", "tweakmAutoCleanContainer.comment", "Auto Clean Container");
	@Config(value = Config.Type.TWEAK, modRequire = itemscroller)
	public static final ConfigBooleanHotkeyed TWEAKM_AUTO_FILL_CONTAINER = new ConfigBooleanHotkeyed("tweakmAutoFillContainer", false, "", "tweakmAutoFillContainer.comment", "Auto Fill Container");
	@Config(value = Config.Type.TWEAK, modRequire = {tweakeroo, litematica})
	public static final ConfigBooleanHotkeyed TWEAKM_AUTO_PICK_SCHEMATIC_BLOCK = new ConfigBooleanHotkeyed("tweakmAutoPickSchematicBlock", false, "", "tweakmAutoPickSchematicBlock.comment", "Auto Pick Schematic Block");

	@Config(Config.Type.DISABLE)
	public static final ConfigBooleanHotkeyed DISABLE_LIGHT_UPDATES = new ConfigBooleanHotkeyed("disableLightUpdates", false, "", "disableLightUpdates.comment", "Disable Light Updates");
	@Config(Config.Type.DISABLE)
	public static final ConfigBooleanHotkeyed DISABLE_REDSTONE_WIRE_PARTICLE = new ConfigBooleanHotkeyed("disableRedstoneWireParticle", false, "", "disableRedstoneWireParticle.comment", "Disable particle of redstone wire");

	////////////////////
	//   Mod Tweaks   //
	////////////////////

	@Config(value = Config.Type.GENERIC, modRequire = optifine, category = Config.Category.MOD_TWEAKS)
	public static final ConfigBoolean OF_UNLOCK_F3_FPS_LIMIT = new ConfigBoolean("ofUnlockF3FpsLimit", false, "ofUnlockF3FpsLimit.comment");

	@Config(value = Config.Type.GENERIC, modRequire = xaero_worldmap, category = Config.Category.MOD_TWEAKS)
	public static final ConfigBoolean XMAP_NO_SESSION_FINALIZATION_WAIT = new ConfigBoolean("xmapNoSessionFinalizationWait", false, "xmapNoSessionFinalizationWait.comment");

	//////////////////////////
	//  TweakerMore Setting //
	//////////////////////////

	@Config(value = Config.Type.HOTKEY, category = Config.Category.SETTING)
	public static final ConfigHotkey OPEN_TWEAKERMORE_CONFIG_GUI = new ConfigHotkey("openTweakermoreConfigGui", "K,C", "openTweakermoreConfigGui.comment");
	@Config(value = Config.Type.HOTKEY, category = Config.Category.SETTING)
	public static final ConfigBoolean TWEAKERMORE_DEBUG_SWITCH = new ConfigBoolean("tweakermoreDebugSwitch", false, "tweakermoreDebugSwitch.comment");

	/**
	 * ============================
	 *    Implementation Details
	 * ============================
	 */

	private static final List<TweakerMoreOption> OPTIONS = Util.make(() -> {
		List<TweakerMoreOption> list = Lists.newArrayList();
		for (Field field : TweakerMoreConfigs.class.getDeclaredFields())
		{
			Config annotation = field.getAnnotation(Config.class);
			if (annotation != null)
			{
				try
				{
					IConfigBase option = (IConfigBase)field.get(null);
					list.add(new TweakerMoreOption(annotation, option));
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}
		return list;
	});

	private static final Map<Config.Category, List<TweakerMoreOption>> CATEGORY_TO_OPTION = Util.make(() -> {
		Map<Config.Category, List<TweakerMoreOption>> map = Maps.newLinkedHashMap();
		OPTIONS.forEach(tweakerMoreOption -> map.computeIfAbsent(tweakerMoreOption.getCategory(), k -> Lists.newArrayList()).add(tweakerMoreOption));
		return map;
	});
	private static final Map<Config.Type, List<TweakerMoreOption>> TYPE_TO_OPTION = Util.make(() -> {
		Map<Config.Type, List<TweakerMoreOption>> map = Maps.newLinkedHashMap();
		OPTIONS.forEach(tweakerMoreOption -> map.computeIfAbsent(tweakerMoreOption.getType(), k -> Lists.newArrayList()).add(tweakerMoreOption));
		return map;
	});
	private static final Map<IConfigBase, TweakerMoreOption> CONFIG_TO_OPTION = Util.make(() -> {
		Map<IConfigBase, TweakerMoreOption> map = Maps.newLinkedHashMap();
		OPTIONS.forEach(tweakerMoreOption -> map.put(tweakerMoreOption.getOption(), tweakerMoreOption));
		return map;
	});

	public static List<TweakerMoreOption> getOptions(Config.Category categoryType)
	{
		return CATEGORY_TO_OPTION.getOrDefault(categoryType, Collections.emptyList());
	}

	public static List<TweakerMoreOption> getOptions(Config.Type optionType)
	{
		return TYPE_TO_OPTION.getOrDefault(optionType, Collections.emptyList());
	}

	public static Stream<IConfigBase> getAllConfigOptionStream()
	{
		return OPTIONS.stream().map(TweakerMoreOption::getOption);
	}

	public static Optional<TweakerMoreOption> getOptionFromConfig(IConfigBase iConfigBase)
	{
		return Optional.ofNullable(CONFIG_TO_OPTION.get(iConfigBase));
	}

	/**
	 * ====================
	 *    Config Storing
	 * ====================
	 */

	private static final String CONFIG_FILE_NAME = TweakerMoreMod.MOD_ID + ".json";

	private static File getConfigFile()
	{
		return FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE_NAME).toFile();
	}

	private static JsonObject ROOT_JSON_OBJ = new JsonObject();

	public static void loadFromFile()
	{
		File configFile = getConfigFile();
		if (configFile.exists() && configFile.isFile() && configFile.canRead())
		{
			JsonElement element = JsonUtils.parseJsonFile(configFile);

			if (element != null && element.isJsonObject())
			{
				JsonObject root = element.getAsJsonObject();
				loadFromJson(root);
				ROOT_JSON_OBJ = root;
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static <T extends IConfigBase> List<T> getConfigOptions(Config.Type optionType)
	{
		return (List<T>)getOptions(optionType).stream().map(TweakerMoreOption::getOption).collect(Collectors.toList());
	}

	public static void loadFromJson(JsonObject jsonObject)
	{
		ConfigUtils.readConfigBase(jsonObject, "Generic", getConfigOptions(Config.Type.GENERIC));
		ConfigUtils.readConfigBase(jsonObject, "GenericHotkeys", getConfigOptions(Config.Type.HOTKEY));
		ConfigUtils.readConfigBase(jsonObject, "Lists", getConfigOptions(Config.Type.LIST));
		ConfigUtils.readHotkeyToggleOptions(jsonObject, "TweakHotkeys", "TweakToggles", getConfigOptions(Config.Type.TWEAK));
		ConfigUtils.readHotkeyToggleOptions(jsonObject, "DisableHotkeys", "DisableToggles", getConfigOptions(Config.Type.DISABLE));

		onConfigLoaded();
	}

	private static void onConfigLoaded()
	{
		TweakerMoreConfigs.HAND_RESTORE_RESTRICTION.setListType((UsageRestriction.ListType)TweakerMoreConfigs.HAND_RESTORE_LIST_TYPE.getOptionListValue());
		TweakerMoreConfigs.HAND_RESTORE_RESTRICTION.setListContents(TweakerMoreConfigs.HAND_RESTORE_BLACKLIST.getStrings(), TweakerMoreConfigs.HAND_RESTORE_WHITELIST.getStrings());
	}

	public static void saveToFile()
	{
		File configFile = getConfigFile();
		JsonObject root = ROOT_JSON_OBJ;

		ConfigUtils.writeConfigBase(root, "Generic", getConfigOptions(Config.Type.GENERIC));
		ConfigUtils.writeConfigBase(root, "GenericHotkeys", getConfigOptions(Config.Type.HOTKEY));
		ConfigUtils.writeConfigBase(root, "Lists", getConfigOptions(Config.Type.LIST));
		ConfigUtils.writeHotkeyToggleOptions(root, "TweakHotkeys", "TweakToggles", getConfigOptions(Config.Type.TWEAK));
		ConfigUtils.writeHotkeyToggleOptions(root, "DisableHotkeys", "DisableToggles", getConfigOptions(Config.Type.DISABLE));

		JsonUtils.writeJsonToFile(root, configFile);
	}

	@Override
	public void load()
	{
		loadFromFile();
	}

	@Override
	public void save()
	{
		saveToFile();
	}
}
