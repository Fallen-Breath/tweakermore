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

package me.fallenbreath.tweakermore.impl.mc_tweaks.disableEntityRenderInterpolation;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.compat.carpet.CarpetModAccess;

public class DisableEntityRenderInterpolationHelper
{
	/**
	 * Carpet's tick freeze state also freezes client world entity ticking,
	 * which cancels the client-side entity position interpolation logic,
	 * so we need to manually setting the entity's position & rotation to the correct values
	 */
	public static boolean shouldUpdatePositionOrAnglesDirectly()
	{
		return CarpetModAccess.isTickFrozen() || TweakerMoreConfigs.DISABLE_ENTITY_RENDER_INTERPOLATION_FORCED_SYNC.getBooleanValue();
	}
}
