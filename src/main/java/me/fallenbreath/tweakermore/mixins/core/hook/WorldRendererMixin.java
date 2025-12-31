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
import me.fallenbreath.tweakermore.event.TweakerMoreRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 12006
//$$ import org.joml.Matrix4fStack;
//#else
import com.mojang.blaze3d.vertex.PoseStack;
//#endif

//#if MC >= 11904
//$$ import org.spongepowered.asm.mixin.injection.Slice;
//#endif

@Mixin(LevelRenderer.class)
public abstract class WorldRendererMixin
{
	@Shadow @Final private Minecraft minecraft;

	// around the renderChunkDebugInfo method call, before the matrixStack.pop() (matrixStack == RenderSystem.getModelViewStack())

	//#if MC >= 11904
	//$$ @Inject(
	//$$ 		method = "renderLevel",
	//$$ 		slice = @Slice(
	//$$ 				from = @At(
	//$$ 						//#if MC >= 12110
	//$$ 						//$$ value = "INVOKE",
	//$$ 						//$$ target = "Lnet/minecraft/client/renderer/LevelRenderer;addWeatherPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lnet/minecraft/world/phys/Vec3;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V"
	//$$ 						//#elseif MC >= 12106
	//$$ 						//$$ value = "INVOKE",
	//$$ 						//$$ target = "Lnet/minecraft/client/renderer/LevelRenderer;addWeatherPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lnet/minecraft/world/phys/Vec3;FLcom/mojang/blaze3d/buffers/GpuBufferSlice;)V"
	//$$ 						//#elseif MC >= 12104
	//$$ 						//$$ value = "INVOKE",
	//$$ 						//$$ target = "Lnet/minecraft/client/renderer/LevelRenderer;addWeatherPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lnet/minecraft/world/phys/Vec3;FLnet/minecraft/client/renderer/FogParameters;)V"
	//$$ 						//#elseif MC >= 12103
	//$$ 						//$$ value = "INVOKE",
	//$$ 						//$$ target = "Lnet/minecraft/client/renderer/LevelRenderer;addWeatherPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/world/phys/Vec3;FLnet/minecraft/client/renderer/FogParameters;)V"
	//$$ 						//#else
	//$$ 						value = "CONSTANT",
	//$$ 						args = "stringValue=weather",
	//$$ 						ordinal = 1
	//$$ 						//#endif
	//$$ 				)
	//$$ 		),
	//$$ 		at = @At(
	//$$ 				value = "INVOKE",
	//$$ 				//#if MC >= 12006
	//$$ 				//$$ target = "Lorg/joml/Matrix4fStack;popMatrix()Lorg/joml/Matrix4fStack;",
	//$$ 				//$$ remap = false,
	//$$ 				//#else
	//$$ 				target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V",
	//$$ 				//#endif
	//$$ 				ordinal = 0
	//$$ 		)
	//$$ )
	//#else
	@Inject(
			method = "renderLevel",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/LevelRenderer;renderDebug(Lnet/minecraft/client/Camera;)V"
			)
	)
	//#endif
	private void worldRenderPostHook$TKM(
			CallbackInfo ci,
			@Local(
					//#if MC < 12006
					argsOnly = true
					//#endif
			)
			//#if MC >= 12006
			//$$ Matrix4fStack matrices
			//#else
			PoseStack matrices
			//#endif
	)
	{
		TweakerMoreRenderEvents.dispatchRenderWorldPostEvent(
				this.minecraft
				//#if MC >= 11600
				//$$ , matrices
				//#endif
		);
	}
}
