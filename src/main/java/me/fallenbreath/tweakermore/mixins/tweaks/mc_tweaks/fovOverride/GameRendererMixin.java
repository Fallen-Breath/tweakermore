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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.fovOverride;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin
{
	@Inject(
			method = "getFov",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/MinecraftClient;options:Lnet/minecraft/client/options/GameOptions;"
			),
			cancellable = true
	)
	private void applyFovOverride_fovOverride(
			//#if MC >= 12103
			//$$ CallbackInfoReturnable<Float> cir
			//#else
			CallbackInfoReturnable<Double> cir
			//#endif
	)
	{
		if (TweakerMoreConfigs.FOV_OVERRIDE_ENABLED.getBooleanValue())
		{
			double override = TweakerMoreConfigs.FOV_OVERRIDE_VALUE.getDoubleValue();
			//#if MC >= 12103
			//$$ cir.setReturnValue((float)override);
			//#else
			cir.setReturnValue(override);
			//#endif
		}
	}
}
