/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * TweakerMore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TweakerMore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TweakerMore.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.fallenbreath.tweakermore.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.util.JsonUtils;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.migration.ConfigRenameMigration;
import me.fallenbreath.tweakermore.config.statistic.OptionStatisticSaver;
import me.fallenbreath.tweakermore.gui.TweakerMoreConfigGui;
import me.fallenbreath.tweakermore.util.FabricUtil;
import me.fallenbreath.tweakermore.util.FileUtil;
import me.fallenbreath.tweakermore.util.JsonSaveAble;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//#if MC < 11700
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
//#endif

//#if MC < 11800
import com.google.gson.JsonParser;
//#endif

public class TweakerMoreConfigStorage implements IConfigHandler
{
	private static final TweakerMoreConfigStorage INSTANCE = new TweakerMoreConfigStorage();
	private static final Map<String, JsonSaveAble> INTERNAL_DATA_SAVERS = new ImmutableMap.Builder<String, JsonSaveAble>().
			put("configGui", TweakerMoreConfigGui.getSetting()).
			put("configStatistic", new OptionStatisticSaver()).
			build();

	private JsonObject loadedJson = new JsonObject();

	private TweakerMoreConfigStorage() {}

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
			this.loadFromJson(root, true);
		}
	}

	public void loadFromJson(JsonObject root, boolean isSelfConfig)
	{
		if (isSelfConfig)
		{
			//#if MC >= 11800
			//$$ this.loadedJson = root.deepCopy();
			//#else
			// trick to make a deep copy of the root object, cuz JsonObject.deepCopy is not private in gson v2.8.0
			this.loadedJson = new JsonParser().parse(root.toString()).getAsJsonObject();
			//#endif

			ConfigRenameMigration.patchConfig(root, Lists.newArrayList(
					// keys are collected from calls to ConfigUtils in the rest of this method
					"Generic", "GenericHotkeys", "Lists", "Fixes",
					"TweakHotkeys", "TweakToggles", "DisableHotkeys", "DisableToggles"
			));
		}
		ConfigUtils.readConfigBase(root, "Generic", getConfigOptions(Config.Type.GENERIC));
		ConfigUtils.readConfigBase(root, "GenericHotkeys", getConfigOptions(Config.Type.HOTKEY));
		ConfigUtils.readConfigBase(root, "Lists", getConfigOptions(Config.Type.LIST));
		ConfigUtils.readHotkeyToggleOptions(root, "TweakHotkeys", "TweakToggles", getConfigOptions(Config.Type.TWEAK));
		ConfigUtils.readHotkeyToggleOptions(root, "DisableHotkeys", "DisableToggles", getConfigOptions(Config.Type.DISABLE));
		ConfigUtils.readConfigBase(root, "Fixes", getConfigOptions(Config.Type.FIX));

		loadInternal(root);

		TweakerMoreConfigs.onConfigLoaded();
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
		try
		{
			JsonObject root = TweakerMoreConfigs.PRESERVE_CONFIG_UNKNOWN_ENTRIES.getBooleanValue() ? loadedJson : new JsonObject();

			ConfigUtils.writeConfigBase(root, "Generic", getConfigOptions(Config.Type.GENERIC));
			ConfigUtils.writeConfigBase(root, "GenericHotkeys", getConfigOptions(Config.Type.HOTKEY));
			ConfigUtils.writeConfigBase(root, "Lists", getConfigOptions(Config.Type.LIST));
			ConfigUtils.writeHotkeyToggleOptions(root, "TweakHotkeys", "TweakToggles", getConfigOptions(Config.Type.TWEAK));
			ConfigUtils.writeHotkeyToggleOptions(root, "DisableHotkeys", "DisableToggles", getConfigOptions(Config.Type.DISABLE));
			ConfigUtils.writeConfigBase(root, "Fixes", getConfigOptions(Config.Type.FIX));

			saveInternal(root);

			File configFile = FileUtil.getConfigFile();
			// malilib in <mc1.17 doesn't have the "save to temp then rename" operation, so we do it ourself
			//#if MC >= 11700
			//$$ JsonUtils.writeJsonToFile(root, configFile);
			//#else
			File tempFile = new File(configFile.getParent(), configFile.getName() + ".tmp");
			JsonUtils.writeJsonToFile(root, tempFile);
			Files.move(tempFile.toPath(), configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			//#endif
		}
		catch (Exception e)
		{
			TweakerMoreMod.LOGGER.error("Failed to save the config file of TweakerMore", e);
		}
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
