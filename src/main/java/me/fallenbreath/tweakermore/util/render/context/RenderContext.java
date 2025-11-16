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

import me.fallenbreath.tweakermore.util.render.matrix.McMatrixStack;

//#if MC >= 12106
//$$ import me.fallenbreath.tweakermore.util.render.matrix.Joml3x2fMatrixStack;
//#endif

//#if MC >= 12006
//$$ import me.fallenbreath.tweakermore.util.render.matrix.JomlMatrixStack;
//$$ import org.joml.Matrix4fStack;
//#endif

//#if MC >= 12000
//$$ import net.minecraft.client.gui.GuiGraphics;
//#endif

//#if MC >= 11600
//$$ import org.jetbrains.annotations.NotNull;
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif

/**
 * Factory class
 */
public abstract class RenderContext
{
	// ============================= Factories =============================

	public static WorldRenderContext world(
			//#if MC >= 12006
			//$$ @NotNull Matrix4fStack matrixStack
			//#elseif MC >= 11600
			//$$ @NotNull PoseStack matrixStack
			//#endif
	)
	{
		//#if MC >= 12006
		//$$ return new WorldRenderContextImpl(new JomlMatrixStack(matrixStack));
		//#elseif MC >= 11600
		//$$ return new WorldRenderContextImpl(new McMatrixStack(matrixStack));
		//#else
		return new WorldRenderContextImpl(new McMatrixStack());
		//#endif
	}

	public static GuiRenderContext gui(
			//#if MC >= 12000
			//$$ @NotNull GuiGraphics drawContext
			//#elseif MC >= 11600
			//$$ @NotNull PoseStack matrixStack
			//#endif
	)
	{
		//#if MC >= 12000
		//$$ return new GuiRenderContextImpl(drawContext);
		//#elseif MC >= 11600
		//$$ return new GuiRenderContextImpl(new McMatrixStack(matrixStack));
		//#else
		return new GuiRenderContextImpl(new McMatrixStack());
		//#endif
	}

	//#if MC >= 12000
	//$$ public static GuiRenderContext gui(@NotNull GuiGraphics drawContext, @NotNull PoseStack matrixStack)
	//$$ {
	//$$ 	return new GuiRenderContextImpl(drawContext, new McMatrixStack(matrixStack));
	//$$ }
	//#endif
}
