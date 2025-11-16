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

package me.fallenbreath.tweakermore.config.migration;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.util.JsonUtils;
import net.minecraft.Util;

import java.util.List;
import java.util.Optional;

public class ConfigRenameMigration
{
	// old -> new
	private static final BiMap<String, String> CONFIG_NAME_MAPPER = Util.make(HashBiMap.create(), map -> {
		// v3.10: remove tweakm prefix
		Lists.newArrayList(
				"tweakmAutoCleanContainer",
				"tweakmAutoCleanContainerListType",
				"tweakmAutoCleanContainerWhiteList",
				"tweakmAutoCleanContainerBlackList",
				"tweakmAutoCollectMaterialListItem",
				"tweakmAutoFillContainer",
				"tweakmAutoPickSchematicBlock",
				"tweakmAutoPutBackExistedItem",
				"tweakmAutoVillagerTradeFavorites",
				"tweakmContainerProcessorHint",
				"tweakmContainerProcessorHintPos",
				"tweakmContainerProcessorHintScale",
				"tweakmInfoView",
				"tweakmSchematicProPlace",
				"tweakmSafeAfk",
				"tweakmSchematicBlockPlacementRestriction",
				"tweakmSchematicBlockPlacementRestrictionHint",
				"tweakmSchematicBlockPlacementRestrictionItemWhitelist",
				"tweakmSchematicBlockPlacementRestrictionMargin",
				"tweakmSchematicBlockPlacementRestrictionCheckFacing",
				"tweakmSchematicBlockPlacementRestrictionCheckSlab",
				"tweakmSchematicBlockPlacementRestrictionStrict",
				"tweakmDaytimeOverride",
				"tweakmFakeNightVision",
				"tweakmFlawlessFrames",
				"tweakmUnlimitedBlockEntityRenderDistance",
				"tweakmUnlimitedEntityRenderDistance",
				"tweakmWeatherOverride"
		).forEach(oldName -> {
			String newName = oldName.replaceFirst("^tweakm", "");
			newName = newName.substring(0, 1).toLowerCase() + newName.substring(1);
			map.put(oldName, newName);
		});

		// v3.17: add the "box" word to some shulker box item options
		Lists.newArrayList(
				"shulkerItemContentHint",
				"shulkerItemContentHintScale",
				"shulkerTooltipEnchantmentHint",
				"shulkerTooltipFillLevelHint",
				"shulkerTooltipHintLengthLimit",
				"shulkerTooltipPotionInfoHint"
		).forEach(oldName -> {
			String newName = oldName.replaceFirst("^shulker", "shulkerBox");
			map.put(oldName, newName);
		});
	});

	public static Optional<String> oldToNew(String oldConfigName)
	{
		return Optional.ofNullable(CONFIG_NAME_MAPPER.get(oldConfigName));
	}

	public static Optional<String> newToOld(String newConfigName)
	{
		return Optional.ofNullable(CONFIG_NAME_MAPPER.inverse().get(newConfigName));
	}

	public static void patchConfig(JsonObject root, List<String> keys)
	{
		for (String key : keys)
		{
			JsonObject obj = JsonUtils.getNestedObject(root, key, false);
			if (obj != null)
			{
				CONFIG_NAME_MAPPER.forEach((oldName, newName) -> {
					if (obj.has(oldName) && !obj.has(newName))
					{
						obj.add(newName, obj.get(oldName));
					}
				});
			}
		}
	}
}
