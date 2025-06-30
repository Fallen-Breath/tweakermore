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

package me.fallenbreath.tweakermore.mixins.util.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.util.IdentifierUtils;
import net.minecraft.client.gl.RenderPipelines;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import me.fallenbreath.tweakermore.util.render.TweakerMoreRenderPipelines;

@Mixin(RenderPipelines.class)
public abstract class RenderPipelinesMixin
{
	@Shadow @Final private static RenderPipeline.Snippet POSITION_TEX_COLOR_SNIPPET;

	@Shadow
	private static RenderPipeline register(RenderPipeline pipeline)
	{
		return null;
	}

	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void addTweakerMorePipelines(CallbackInfo ci)
	{
		TweakerMoreRenderPipelines.GUI_TEXTURED_NO_DEPTH_TEST = register(
				RenderPipeline.builder(POSITION_TEX_COLOR_SNIPPET).
				withLocation(IdentifierUtils.of(TweakerMoreMod.MOD_ID, "pipeline/gui_textured_no_depth_test")).
				withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).
				withDepthWrite(false).
				build()
		);
	}
}
