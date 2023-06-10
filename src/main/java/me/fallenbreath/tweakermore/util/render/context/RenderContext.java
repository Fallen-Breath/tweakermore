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

package me.fallenbreath.tweakermore.util.render.context;

import net.minecraft.client.gui.DrawableHelper;  // will be remapped to DrawContext in mc1.20
import net.minecraft.client.util.math.Matrix4f;

//#if MC >= 12000
//$$ import me.fallenbreath.tweakermore.mixins.util.render.DrawContextAccessor;
//$$ import net.minecraft.client.MinecraftClient;
//$$ import net.minecraft.client.render.Tessellator;
//$$ import net.minecraft.client.render.VertexConsumerProvider;
//#endif

//#if MC >= 11600
//$$ import org.jetbrains.annotations.NotNull;
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

public interface RenderContext
{
	// ============================= Factories =============================

	static RenderContext of(
			//#if MC >= 11600
			//$$ @NotNull MatrixStack matrixStack
			//#endif
	)
	{
		//#if MC >= 12000
		//$$ DrawContext drawContext = new DrawContext(MinecraftClient.getInstance(), VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer()));
		//$$ ((DrawContextAccessor)drawContext).setMatrices(matrixStack);
		//$$ return new RenderContextImpl(drawContext, matrixStack);
		//#elseif MC >= 11600
		//$$ return new RenderContextImpl(matrixStack);
		//#else
		return new RenderContextImpl();
		//#endif
	}

	//#if MC >= 12000
	//$$ static RenderContext of(@NotNull DrawContext drawContext)
	//$$ {
	//$$ 	return new RenderContextImpl(drawContext, drawContext.getMatrices());
	//$$ }
	//#endif

	// ============================= Getters =============================

	DrawableHelper getGuiDrawer();

	//#if MC >= 11600
	//$$ MatrixStack getMatrixStack();
	//#endif

	// ============================= Manipulators =============================

	void pushMatrix();

	void popMatrix();

	void translate(double x, double y, double z);

	void scale(double x, double y, double z);

	void multMatrix(Matrix4f matrix4f);
}
