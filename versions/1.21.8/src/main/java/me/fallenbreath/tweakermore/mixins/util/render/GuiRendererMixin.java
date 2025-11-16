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

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import me.fallenbreath.tweakermore.util.render.context.InWorldGuiRendererHook;
import net.minecraft.client.gui.render.GuiRenderer;
import com.mojang.blaze3d.platform.Window;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GuiRenderer.class)
public abstract class GuiRendererMixin implements InWorldGuiRendererHook
{
	@Unique
	private boolean inWorldGuiRender$TKM;

	@Override
	public void setInWorldGuiRender$TKM(boolean flag)
	{
		this.inWorldGuiRender$TKM = flag;
	}

	@WrapWithCondition(
			method = "renderPreparedDraws",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/systems/RenderSystem;setProjectionMatrix(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lcom/mojang/blaze3d/ProjectionType;)V"
			)
	)
	private boolean skipSetProjectionMatrixForInWorldGuiRendering(
			GpuBufferSlice gpuBufferSlice, ProjectionType projectionType,
			@Local Window window
	)
	{
		return !this.inWorldGuiRender$TKM;
	}

	@ModifyArg(
			method = "renderPreparedDraws",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/DynamicUniforms;writeTransform(Lorg/joml/Matrix4fc;Lorg/joml/Vector4fc;Lorg/joml/Vector3fc;Lorg/joml/Matrix4fc;F)Lcom/mojang/blaze3d/buffers/GpuBufferSlice;"
			),
			index = 0
	)
	private Matrix4fc w(Matrix4fc matrix4fc)
	{
		if (this.inWorldGuiRender$TKM)
		{
			matrix4fc = RenderSystem.getModelViewMatrix();
		}
		return matrix4fc;
	}
}
