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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.f3GSimplify;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.render.debug.ChunkBorderDebugRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkBorderDebugRenderer.class)
public abstract class ChunkBorderDebugRendererMixin
{
	@ModifyExpressionValue(
			method = "render",
			at = {
					@At(value = "CONSTANT", args = "intValue=16", ordinal = 0),
					@At(value = "CONSTANT", args = "intValue=16", ordinal = 1),
					//#if MC >= 11700
					//$$ @At(
					//$$ 		value = "INVOKE",
					//$$ 		target = "Lnet/minecraft/client/world/ClientWorld;getTopY()I",
					//$$ 		ordinal = 1
					//$$ ),
					//#else
					@At(value = "CONSTANT", args = "intValue=256", ordinal = 0),
					//#endif
			}
	)
	private int f3GSimplify_dontRenderYellowLine(int forLoopUpperLimit)
	{
		if (TweakerMoreConfigs.F3_G_SIMPLIFY.getBooleanValue())
		{
			forLoopUpperLimit = Integer.MIN_VALUE;
		}
		return forLoopUpperLimit;
	}
}
