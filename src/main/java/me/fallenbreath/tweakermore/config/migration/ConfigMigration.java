/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  Fallen_Breath and contributors
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

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.util.JsonUtils;

public class ConfigMigration
{
	public static void patchConfig(JsonObject root)
	{
		ConfigRenameMigration.patchConfig(root, Lists.newArrayList(
				"Generic", "GenericHotkeys", "Lists", "Fixes",
				"TweakHotkeys", "TweakToggles", "DisableHotkeys", "DisableToggles"
		));

		// v3.34: move the moving piston selection options from Generic to TweakToggles
		copyEntriesIfAbsent(
				root, "Generic", "TweakToggles",
				"movingPistonBlockSelectable",
				"movingPistonBlockSelectableCreativeOnly"
		);
	}

	private static void copyEntriesIfAbsent(JsonObject root, String sourceGroup, String targetGroup, String... names)
	{
		JsonObject source = JsonUtils.getNestedObject(root, sourceGroup, false);
		if (source == null)
		{
			return;
		}

		for (String name : names)
		{
			if (!source.has(name))
			{
				continue;
			}

			JsonObject target = JsonUtils.getNestedObject(root, targetGroup, true);
			if (!target.has(name))
			{
				target.add(name, source.get(name));
			}
		}
	}
}
