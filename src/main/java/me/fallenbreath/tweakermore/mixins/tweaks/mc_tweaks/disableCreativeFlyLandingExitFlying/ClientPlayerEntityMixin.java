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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableCreativeFlyLandingExitFlying;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(LocalPlayer.class)
public abstract class ClientPlayerEntityMixin
{
	@ModifyExpressionValue(
			method = "aiStep",
			slice = @Slice(
					from = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/client/player/AbstractClientPlayer;aiStep()V"
					)
			),
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;isAlwaysFlying()Z",
					ordinal = 0
			)
	)
	private boolean disableCreativeFlyLandingExit_dontExitFlyingStateOnLanding(boolean cannotExit)
	{
		if (TweakerMoreConfigs.DISABLE_CREATIVE_FLY_LANDING_EXIT_FLYING.getBooleanValue())
		{
			cannotExit = true;
		}
		return cannotExit;
	}
}
