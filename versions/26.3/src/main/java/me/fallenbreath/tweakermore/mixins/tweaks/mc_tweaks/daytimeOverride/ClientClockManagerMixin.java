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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.daytimeOverride;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.fallenbreath.tweakermore.impl.mc_tweaks.daytimeOverride.ClientClockInstanceWithOverworldMark;
import net.minecraft.client.ClientClockManager;
import net.minecraft.core.Holder;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * mc < 26.1       : subproject 1.15.2 (main project)
 * mc [26.1, 26.3) : subproject 26.1.2
 * mc >= 26.3      : subproject 26.3        <--------
 */
@Mixin(ClientClockManager.class)
public abstract class ClientClockManagerMixin
{
	@ModifyExpressionValue(
			method = "lambda$getInstance$0",
			at = @At(
					value = "NEW",
					target = "()Lnet/minecraft/client/ClientClockManager$ClientClockInstance;"
			)
	)
	private static ClientClockManager.ClientClockInstance daytimeOverride_markOverworld(
			ClientClockManager.ClientClockInstance clock,
			@Local(argsOnly = true) Holder<WorldClock> definition
	)
	{
		if (definition.is(WorldClocks.OVERWORLD))
		{
			((ClientClockInstanceWithOverworldMark)clock).setIsOverworldClockInstance$tweakermore();
		}
		return clock;
	}
}
