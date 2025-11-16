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

package me.fallenbreath.tweakermore.impl.mc_tweaks.movingPistonBlockSelectable;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.Minecraft;

public class MovingPistonBlockSelectableHelper
{
	public static boolean enabled = false;
	public static final ThreadLocal<Boolean> applyOutlineShapeOverride = ThreadLocal.withInitial(() -> false);

	public static boolean shouldEnableFeature()
	{
		return enabled &&
				TweakerMoreConfigs.MOVING_PISTON_BLOCK_SELECTABLE.getBooleanValue() &&
				!(TweakerMoreConfigs.MOVING_PISTON_BLOCK_SELECTABLE_CREATE_ONLY.getBooleanValue() && !currentPlayerIsCreative());
	}

	private static boolean currentPlayerIsCreative()
	{
		Minecraft mc = Minecraft.getInstance();
		if (mc != null && mc.player != null)
		{
			return mc.player.isCreative();
		}
		return false;
	}
}
