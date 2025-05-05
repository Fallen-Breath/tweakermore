/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2025  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.commandHistoryLimit;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.util.CommandHistoryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CommandHistoryManager.class)
public abstract class CommandHistoryManagerMixin
{
	@ModifyExpressionValue(
			method = "add",
			at = @At(
					value = "CONSTANT",
					args = "intValue=50"
			),
			// in case some other mod use @ModifyConstant
			require = 0
	)
	private int commandHistoryLimit_modify(int limit)
	{
		if (TweakerMoreConfigs.COMMAND_HISTORY_LIMIT.isModified())
		{
			limit = TweakerMoreConfigs.COMMAND_HISTORY_LIMIT.getIntegerValue();
		}
		return limit;
	}
}
