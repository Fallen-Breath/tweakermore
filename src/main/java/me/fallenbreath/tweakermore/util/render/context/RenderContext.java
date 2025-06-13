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

import me.fallenbreath.tweakermore.util.render.matrix.IMatrixStack;
import net.minecraft.client.gui.DrawableHelper;  // will be remapped to DrawContext in mc1.20
import net.minecraft.client.util.math.Matrix4f;

import me.fallenbreath.tweakermore.util.render.matrix.McMatrixStack;
import org.jetbrains.annotations.NotNull;

//#if MC >= 12106
//$$ import me.fallenbreath.tweakermore.util.render.matrix.Joml3x2fMatrixStack;
//#endif

//#if MC >= 12006
//$$ import me.fallenbreath.tweakermore.util.render.matrix.JomlMatrixStack;
//$$ import org.joml.Matrix4fStack;
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
		//$$ return new RenderContextImpl(null, new McMatrixStack(matrixStack));
		//#elseif MC >= 11600
		//$$ return new RenderContextImpl(new McMatrixStack(matrixStack));
		//#else
		return new RenderContextImpl(new McMatrixStack());
		//#endif
	}

	static WorldRenderContextImpl createWorldRenderContext(
			//#if MC >= 12006
			//$$ @NotNull Matrix4fStack matrixStack
			//#elseif MC >= 11600
			//$$ @NotNull MatrixStack matrixStack
			//#endif
	)
	{
		//#if MC >= 12106
		//$$ return new WorldRenderContextImpl(null, new JomlMatrixStack(matrixStack));
		//#elseif MC >= 12006
		//$$ return new WorldRenderContextImpl(null, new JomlMatrixStack(matrixStack));
		//#elseif MC >= 12000
		//$$ return new WorldRenderContextImpl(null, new McMatrixStack(matrixStack));
		//#elseif MC >= 11600
		//$$ return new WorldRenderContextImpl(new McMatrixStack(matrixStack));
		//#else
		return new WorldRenderContextImpl(new McMatrixStack());
		//#endif
	}

	//#if MC >= 12000
	//$$ static RenderContext of(@NotNull DrawContext drawContext)
	//$$ {
	//$$ 	return new RenderContextImpl(
	//$$ 			drawContext,
	//$$ 			//#if MC >= 12106
	//$$ 			//$$ new Joml3x2fMatrixStack(drawContext.getMatrices())
	//$$ 			//#else
	//$$ 			new McMatrixStack(drawContext.getMatrices())
	//$$ 			//#endif
	//$$ 	);
	//$$ }
	//#endif

	//#if MC >= 12006
	//$$ // NOTES: GUI rendering only
	//$$ static RenderContext of(@NotNull Matrix4fStack matrixStack)
	//$$ {
	//$$ 	return new RenderContextImpl(null, new JomlMatrixStack(matrixStack));
	//$$ }
	//#endif

	// ============================= Getters =============================

	DrawableHelper getGuiDrawer();

	@NotNull
	IMatrixStack getMatrixStack();

	// ============================= Manipulators =============================

	void pushMatrix();

	void popMatrix();

	void translate(double x, double y, double z);

	void scale(double x, double y, double z);

	void multMatrix(Matrix4f matrix4f);
}
