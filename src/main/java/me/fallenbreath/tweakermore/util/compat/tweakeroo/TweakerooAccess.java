/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.util.compat.tweakeroo;

import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.tweakeroo.config.Configs;
import fi.dy.masa.tweakeroo.util.CameraEntity;
import me.fallenbreath.tweakermore.util.FabricUtils;
import me.fallenbreath.tweakermore.util.ModIds;
import me.fallenbreath.tweakermore.util.ReflectionUtils;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;

public class TweakerooAccess
{
	public static final boolean TWEAKEROO_LOADED = FabricUtils.isModLoaded(ModIds.tweakeroo);

	@Nullable
	public static LocalPlayer getFreecamEntity()
	{
		if (TWEAKEROO_LOADED)
		{
			return Access.getFreecamEntity();
		}
		return null;
	}

	// for 1.21+ tweakeroo,
	// CARPET_ACCURATE_PLACEMENT_PROTOCOL changed to ACCURATE_PLACEMENT_PROTOCOL
	public static boolean getAccuratePlacementProtocolValue()
	{
		if (TWEAKEROO_LOADED)
		{
			return Access.getAccuratePlacementProtocolValue();
		}
		return false;
	}

	private static class Access
	{
		@Nullable
		public static LocalPlayer getFreecamEntity()
		{
			return CameraEntity.getCamera();
		}

		// for 1.21+ tweakeroo,
		// CARPET_ACCURATE_PLACEMENT_PROTOCOL changed to ACCURATE_PLACEMENT_PROTOCOL
		public static boolean getAccuratePlacementProtocolValue()
		{
			Class<?> genericClass = Configs.Generic.class;
			ReflectionUtils.ValueWrapper<ConfigBoolean> newAccField = ReflectionUtils.getStaticField(genericClass, "ACCURATE_PLACEMENT_PROTOCOL");
			if (newAccField.isPresent())
			{
				return newAccField.get().getBooleanValue();
			}
			ReflectionUtils.ValueWrapper<ConfigBoolean> oldAccField = ReflectionUtils.getStaticField(genericClass, "CARPET_ACCURATE_PLACEMENT_PROTOCOL");
			if (oldAccField.isPresent())
			{
				return oldAccField.get().getBooleanValue();
			}

			return false;
		}
	}
}
