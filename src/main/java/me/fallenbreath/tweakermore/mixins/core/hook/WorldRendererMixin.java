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

package me.fallenbreath.tweakermore.mixins.core.hook;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import me.fallenbreath.tweakermore.event.TweakerMoreRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * mc1.19.4-: subproject 1.15.2 (main project)        <--------
 * mc1.19.4+: subproject 1.19.4
 */
@Mixin(LevelRenderer.class)
public abstract class WorldRendererMixin
{
	// around the renderChunkDebugInfo method call, before the matrixStack.pop() (matrixStack == RenderSystem.getModelViewStack())

	@Inject(
			method = "renderLevel",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/LevelRenderer;renderDebug(Lnet/minecraft/client/Camera;)V"
			)
	)
	private void worldRenderPostHook$TKM(CallbackInfo ci, @Local(argsOnly = true) PoseStack matrices)
	{
		TweakerMoreRenderEvents.dispatchRenderWorldPostEvent(
				Minecraft.getInstance()
				//#if MC >= 11600
				//$$ , matrices
				//#endif
		);
	}
}
