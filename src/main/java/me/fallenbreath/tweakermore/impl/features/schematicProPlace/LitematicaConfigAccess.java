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

package me.fallenbreath.tweakermore.impl.features.schematicProPlace;

import fi.dy.masa.litematica.config.Configs;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import me.fallenbreath.tweakermore.util.ReflectionUtils;

public class LitematicaConfigAccess
{
	/*
	 * Litematica changed the field type of `Configs.Visuals.ENABLE_RENDERING` and `Configs.Visuals.ENABLE_SCHEMATIC_RENDERING`
	 * from `ConfigBoolean` to `ConfigBooleanHotkeyed` in 1.21.11-0.26.4 and 26.1.2-0.27.2,
	 * So we can't simply access the field directly without having a compatibility issue.
	 * Here's the reflection workaround.
	 * See: https://github.com/sakura-ryoko/litematica/commit/becd797f531f327d00c1b10ddc3080806289c275#diff-5497d03d960ee02f94a8d1b0a4de53b02f53078c731ca62d79167f3ebb6f6a0b
	 */

	public static ConfigBoolean ENABLE_RENDERING()
	{
		return (ConfigBoolean)ReflectionUtils.getStaticField(Configs.Visuals.class, "ENABLE_RENDERING").get();
	}

	public static ConfigBoolean ENABLE_SCHEMATIC_RENDERING()
	{
		return (ConfigBoolean)ReflectionUtils.getStaticField(Configs.Visuals.class, "ENABLE_SCHEMATIC_RENDERING").get();
	}
}
