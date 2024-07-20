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
import me.fallenbreath.tweakermore.util.ReflectionUtil;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class TweakerooAccess
{
	@Nullable
	public static ClientPlayerEntity getFreecamEntity()
	{
		return CameraEntity.getCamera();
	}

	// for 1.21+ tweakeroo,
	// CARPET_ACCURATE_PLACEMENT_PROTOCOL changed to ACCURATE_PLACEMENT_PROTOCOL
	public static boolean getAccuratePlacementProtocolValue()
	{
		Class<?> genericClass = Configs.Generic.class;
		ReflectionUtil.ValueWrapper<ConfigBoolean> newAccField = ReflectionUtil.getStaticField(genericClass, "ACCURATE_PLACEMENT_PROTOCOL");
		if (newAccField.isPresent())
		{
			return newAccField.get().getBooleanValue();
		}
		ReflectionUtil.ValueWrapper<ConfigBoolean> oldAccField = ReflectionUtil.getStaticField(genericClass, "CARPET_ACCURATE_PLACEMENT_PROTOCOL");
		if (oldAccField.isPresent())
		{
			return oldAccField.get().getBooleanValue();
		}

		return false;
	}
}
