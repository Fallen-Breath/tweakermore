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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.blockBreakingCooldownOverride;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MultiPlayerGameMode.class)
public abstract class ClientPlayerInteractionManagerMixin
{
	@ModifyExpressionValue(
			method = "startDestroyBlock",
			at = @At(value = "CONSTANT", args = "intValue=5")
	)
	private int blockBreakingCooldownOverride$modifyInitialCreativeCooldown(int original)
	{
		return TweakerMoreConfigs.BLOCK_BREAKING_COOLDOWN_OVERRIDE.getBooleanValue() ? TweakerMoreConfigs.BLOCK_BREAKING_COOLDOWN_OVERRIDE_VALUE.getIntegerValue() : original;
	}

	@ModifyExpressionValue(
			method = "continueDestroyBlock",
			at = @At(value = "CONSTANT", args = "intValue=5", ordinal = 0)
	)
	private int blockBreakingCooldownOverride$modifyContinuousCreativeCooldown(int original)
	{
		return TweakerMoreConfigs.BLOCK_BREAKING_COOLDOWN_OVERRIDE.getBooleanValue() ? TweakerMoreConfigs.BLOCK_BREAKING_COOLDOWN_OVERRIDE_VALUE.getIntegerValue() : original;
	}
}
