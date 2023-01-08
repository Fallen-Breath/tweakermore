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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableBeaconBeamRendering;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeaconBlockEntityRenderer.class)
public abstract class BeaconBlockEntityRendererMixin
{
	@Inject(
			//#if MC >= 11700
			//$$ method = "renderBeam(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;FJII[F)V",
			//#elseif MC >= 11500
			method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;FJII[F)V",
			//#else
			//$$ method = "renderBeaconLightBeam",
			//#endif
			at = @At("HEAD"),
			cancellable = true
	)
	private static void disableBeaconBeamRendering(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.DISABLE_BEACON_BEAM_RENDERING.getBooleanValue())
		{
			ci.cancel();
		}
	}
}
