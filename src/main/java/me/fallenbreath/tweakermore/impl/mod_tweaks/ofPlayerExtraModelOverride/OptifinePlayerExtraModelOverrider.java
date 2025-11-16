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

package me.fallenbreath.tweakermore.impl.mod_tweaks.ofPlayerExtraModelOverride;

import fi.dy.masa.malilib.config.options.ConfigOptionList;
import me.fallenbreath.tweakermore.util.ReflectionUtils;
import net.minecraft.client.Minecraft;

import java.util.Map;
import java.util.Optional;

import static me.fallenbreath.tweakermore.impl.mod_tweaks.ofPlayerExtraModelOverride.OverrideDefinitions.OVERRIDE_DEFS;

public class OptifinePlayerExtraModelOverrider
{
	public static Optional<OverrideImpl> overridePlayerConfig(String playerName)
	{
		boolean isMe = Optional.ofNullable(Minecraft.getInstance().player).
				map(player -> player.getGameProfile().getName().equals(playerName)).
				orElse(false);

		for (OverrideDefinition overrideDefinition : OVERRIDE_DEFS)
		{
			boolean doOverride = false;
			switch (overrideDefinition.getStrategy())
			{
				case ME:
					doOverride = isMe;
					break;
				case ALL:
					doOverride = true;
					break;
				case UNTOUCHED:
				default:
					break;
			}

			if (doOverride)
			{
				return Optional.ofNullable(overrideDefinition.getImpl());
			}
		}
		return Optional.empty();
	}

	public static void onConfigValueChanged(ConfigOptionList config)
	{
		// clean optifine's player config cache map,
		// so optifine will try to fetch player config again
		// which means our PlayerConfigurationReceiverMixin mixin can do its job
		ReflectionUtils.getClass("net.optifine.player.PlayerConfigurations").ifPresent(clazz -> {
			ReflectionUtils.getStaticField(clazz, "mapConfigurations").ifPresent(map -> {
				if (map instanceof Map)
				{
					((Map<?, ?>)map).clear();
				}
			});
		});
	}
}
