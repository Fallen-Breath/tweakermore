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

import static me.fallenbreath.tweakermore.config.ConfigFactory.*;
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
	public static final ConfigInteger AUTO_FILL_CONTAINER_THRESHOLD = newConfigInteger("autoFillContainerThreshold", 2, 1, 36);
	@Config(Config.Type.GENERIC)
	public static final ConfigDouble NETHER_PORTAL_SOUND_CHANCE = newConfigDouble("netherPortalSoundChance", 0.01D, 0.0D, 0.01D);
	@Config(Config.Type.GENERIC)
	public static final ConfigBoolean VILLAGER_OFFER_USES_DISPLAY = newConfigBoolean("villagerOfferUsesDisplay", false);
	@Config(Config.Type.GENERIC)
	public static final ConfigBoolean SHULKER_TOOLTIP_ENCHANTMENT_HINT = newConfigBoolean("shulkerTooltipEnchantmentHint", false);
	@Config(Config.Type.GENERIC)
	public static final ConfigInteger CHAT_MESSAGE_LIMIT = newConfigInteger("chatMessageLimit", 100, 100, 10000);
	@Config(Config.Type.GENERIC)
	public static final ConfigDouble SAFE_AFK_HEALTH_THRESHOLD = newConfigDouble("safeAfkHealthThreshold", 10, 0, 100);

	@Config(Config.Type.HOTKEY)
	public static final ConfigHotkey COPY_SIGN_TEXT_TO_CLIPBOARD = newConfigHotKey("copySignTextToClipBoard", "");

	@Config(value = Config.Type.LIST, modRequire = tweakeroo)
	public static final ConfigOptionList HAND_RESTORE_LIST_TYPE = newConfigOptionList("handRestockListType", UsageRestriction.ListType.NONE);
	@Config(value = Config.Type.LIST, modRequire = tweakeroo)
	public static final ConfigStringList HAND_RESTORE_WHITELIST = newConfigStringList("handRestockWhiteList", ImmutableList.of(RegistryUtil.getItemId(Items.BUCKET)));
	@Config(value = Config.Type.LIST, modRequire = tweakeroo)
	public static final ConfigStringList HAND_RESTORE_BLACKLIST = newConfigStringList("handRestockBlackList", ImmutableList.of(RegistryUtil.getItemId(Items.LAVA_BUCKET)));
	public static final ItemRestriction HAND_RESTORE_RESTRICTION = new ItemRestriction();

	@Config(value = Config.Type.TWEAK, modRequire = itemscroller)
	public static final ConfigBooleanHotkeyed TWEAKM_AUTO_CLEAN_CONTAINER = newConfigBooleanHotkeyed("tweakmAutoCleanContainer");
	@Config(value = Config.Type.TWEAK, modRequire = itemscroller)
	public static final ConfigBooleanHotkeyed TWEAKM_AUTO_FILL_CONTAINER = newConfigBooleanHotkeyed("tweakmAutoFillContainer");
	@Config(value = Config.Type.TWEAK, modRequire = {tweakeroo, litematica})
	public static final ConfigBooleanHotkeyed TWEAKM_AUTO_PICK_SCHEMATIC_BLOCK = newConfigBooleanHotkeyed("tweakmAutoPickSchematicBlock");
	@Config(Config.Type.TWEAK)
	public static final ConfigBooleanHotkeyed TWEAKM_SAFE_AFK = newConfigBooleanHotkeyed("tweakmSafeAfk");

	@Config(Config.Type.DISABLE)
	public static final ConfigBooleanHotkeyed DISABLE_LIGHT_UPDATES = newConfigBooleanHotkeyed("disableLightUpdates");
	@Config(Config.Type.DISABLE)
	public static final ConfigBooleanHotkeyed DISABLE_REDSTONE_WIRE_PARTICLE = newConfigBooleanHotkeyed("disableRedstoneWireParticle");

	////////////////////
	//   Mod Tweaks   //
	////////////////////

	@Config(value = Config.Type.GENERIC, modRequire = optifine, category = Config.Category.MOD_TWEAKS)
	public static final ConfigBoolean OF_UNLOCK_F3_FPS_LIMIT = newConfigBoolean("ofUnlockF3FpsLimit", false);

	@Config(value = Config.Type.GENERIC, modRequire = xaero_worldmap, category = Config.Category.MOD_TWEAKS)
	public static final ConfigBoolean XMAP_NO_SESSION_FINALIZATION_WAIT = newConfigBoolean("xmapNoSessionFinalizationWait", false);

	//////////////////////////
	//  TweakerMore Setting //
	//////////////////////////

	@Config(value = Config.Type.HOTKEY, category = Config.Category.SETTING)
	public static final ConfigHotkey OPEN_TWEAKERMORE_CONFIG_GUI = newConfigHotKey("openTweakermoreConfigGui", "K,C");
	@Config(value = Config.Type.HOTKEY, category = Config.Category.SETTING)
	public static final ConfigBoolean TWEAKERMORE_DEBUG_SWITCH = newConfigBooleanHotkeyed("tweakermoreDebugSwitch");

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
