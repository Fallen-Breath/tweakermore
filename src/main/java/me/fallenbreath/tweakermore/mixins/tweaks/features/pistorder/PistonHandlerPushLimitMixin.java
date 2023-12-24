/*
 * This file is part of the Pistorder project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * Pistorder is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Pistorder is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Pistorder.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.fallenbreath.tweakermore.mixins.tweaks.features.pistorder;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.impl.features.pistorder.PistorderRenderer;
import me.fallenbreath.tweakermore.impl.features.pistorder.pushlimit.PushLimitManager;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.block.piston.PistonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(conflict = @Condition(value = ModIds.pistorder, versionPredicates = "<=1.6.0"))
@Mixin(PistonHandler.class)
public abstract class PistonHandlerPushLimitMixin
{
	@ModifyExpressionValue(
			method = "tryMove",
			at = @At(
					value = "CONSTANT",
					args = "intValue=12"
			),
			require = 3,
			allow = 3
	)
	private int tkmPistorder_modifyPushLimitPistorderMod(int value)
	{
		if (PistorderRenderer.getInstance().isEnabled())
		{
			value = PushLimitManager.getInstance().getPushLimit();
		}
		return value;
	}
}
