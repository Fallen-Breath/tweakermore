package me.fallenbreath.tweakermore.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.restrictions.UsageRestriction;
import me.fallenbreath.tweakermore.gui.TweakerMoreConfigGui;
import me.fallenbreath.tweakermore.util.FabricUtil;
import me.fallenbreath.tweakermore.util.FileUtil;
import me.fallenbreath.tweakermore.util.JsonSaveAble;
import org.spongepowered.include.com.google.common.collect.ImmutableMap;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TweakerMoreConfigStorage implements IConfigHandler
{
	private static final TweakerMoreConfigStorage INSTANCE = new TweakerMoreConfigStorage();
	private static final Map<String, JsonSaveAble> INTERNAL_DATA_SAVERS = new ImmutableMap.Builder<String, JsonSaveAble>().
			put("configGui", TweakerMoreConfigGui.getSetting()).
			build();

	private JsonObject loadedJson = new JsonObject();

	private TweakerMoreConfigStorage()
	{
	}

	public static TweakerMoreConfigStorage getInstance()
	{
		return INSTANCE;
	}

	@SuppressWarnings("unchecked")
	private static <T extends IConfigBase> List<T> getConfigOptions(Config.Type optionType)
	{
		return (List<T>)TweakerMoreConfigs.getOptions(optionType).stream().
				filter(o -> !o.isDevOnly() || FabricUtil.isDevelopmentEnvironment()).
				map(TweakerMoreOption::getConfig).
				collect(Collectors.toList());
	}

	private void onConfigLoaded()
	{
		TweakerMoreConfigs.HAND_RESTORE_RESTRICTION.setListType((UsageRestriction.ListType)TweakerMoreConfigs.HAND_RESTORE_LIST_TYPE.getOptionListValue());
		TweakerMoreConfigs.HAND_RESTORE_RESTRICTION.setListContents(TweakerMoreConfigs.HAND_RESTORE_BLACKLIST.getStrings(), TweakerMoreConfigs.HAND_RESTORE_WHITELIST.getStrings());
	}

	@Override
	public void load()
	{
		JsonObject root = null;
		File configFile = FileUtil.getConfigFile();
		if (configFile.exists() && configFile.isFile() && configFile.canRead())
		{
			JsonElement element = JsonUtils.parseJsonFile(configFile);

			if (element != null && element.isJsonObject())
			{
				root = element.getAsJsonObject();
			}
		}
		if (root != null)
		{
			this.loadFromJson(root);
		}
	}

	public void loadFromJson(JsonObject root)
	{
		this.loadedJson = root;
		ConfigUtils.readConfigBase(root, "Generic", getConfigOptions(Config.Type.GENERIC));
		ConfigUtils.readConfigBase(root, "GenericHotkeys", getConfigOptions(Config.Type.HOTKEY));
		ConfigUtils.readConfigBase(root, "Lists", getConfigOptions(Config.Type.LIST));
		ConfigUtils.readHotkeyToggleOptions(root, "TweakHotkeys", "TweakToggles", getConfigOptions(Config.Type.TWEAK));
		ConfigUtils.readHotkeyToggleOptions(root, "DisableHotkeys", "DisableToggles", getConfigOptions(Config.Type.DISABLE));
		ConfigUtils.readConfigBase(root, "Fixes", getConfigOptions(Config.Type.FIX));

		loadInternal(root);

		onConfigLoaded();
	}

	private void loadInternal(JsonObject jsonObject)
	{
		JsonObject internal = JsonUtils.getNestedObject(jsonObject, "internal", false);
		if (internal != null)
		{
			INTERNAL_DATA_SAVERS.forEach((name, jsonSaveAble) -> {
				JsonObject object = JsonUtils.getNestedObject(internal, name, false);
				if (object != null)
				{
					jsonSaveAble.loadFromJsonSafe(object);
				}
			});
		}
	}

	@Override
	public void save()
	{
		File configFile = FileUtil.getConfigFile();
		JsonObject root = TweakerMoreConfigs.PRESERVE_CONFIG_UNKNOWN_ENTRIES.getBooleanValue() ? loadedJson : new JsonObject();

		ConfigUtils.writeConfigBase(root, "Generic", getConfigOptions(Config.Type.GENERIC));
		ConfigUtils.writeConfigBase(root, "GenericHotkeys", getConfigOptions(Config.Type.HOTKEY));
		ConfigUtils.writeConfigBase(root, "Lists", getConfigOptions(Config.Type.LIST));
		ConfigUtils.writeHotkeyToggleOptions(root, "TweakHotkeys", "TweakToggles", getConfigOptions(Config.Type.TWEAK));
		ConfigUtils.writeHotkeyToggleOptions(root, "DisableHotkeys", "DisableToggles", getConfigOptions(Config.Type.DISABLE));
		ConfigUtils.writeConfigBase(root, "Fixes", getConfigOptions(Config.Type.FIX));

		saveInternal(root);

		JsonUtils.writeJsonToFile(root, configFile);
	}

	private void saveInternal(JsonObject jsonObject)
	{
		JsonObject internal = JsonUtils.getNestedObject(jsonObject, "internal", true);
		assert internal != null;
		INTERNAL_DATA_SAVERS.forEach((name, jsonSaveAble) -> {
			internal.add(name, jsonSaveAble.dumpToJson());
		});
	}
}
