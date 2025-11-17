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

package me.fallenbreath.tweakermore.util.render.context;

import me.fallenbreath.tweakermore.util.render.matrix.McMatrixStack;
import net.minecraft.client.gui.GuiComponent;
import org.jetbrains.annotations.NotNull;

//#if MC >= 12106
//$$ import me.fallenbreath.tweakermore.util.render.matrix.Joml3x2fMatrixStack;
//#endif

//#if MC >= 12000
//$$ import me.fallenbreath.tweakermore.util.render.matrix.IMatrixStack;
//#endif

//#if 11600 <= MC && MC < 12000
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif

public class GuiRenderContextImpl implements GuiRenderContext
{
	//#if MC >= 12000
	//$$ @NotNull
	//$$ private final GuiGraphics drawContext;
	//#endif

	@NotNull
	//#if MC >= 12000
	//$$ private final IMatrixStack matrixStack;
	//#else
	private final McMatrixStack matrixStack;
	//#endif

	//#if MC >= 12000
	//$$ public GuiRenderContextImpl(@NotNull GuiGraphics drawContext, @NotNull IMatrixStack matrixStack)
	//$$ {
	//$$ 	this.drawContext = drawContext;
	//$$ 	this.matrixStack = matrixStack;
	//$$ }
	//$$
	//$$ public GuiRenderContextImpl(@NotNull GuiGraphics drawContext)
	//$$ {
	//$$ 	this(
	//$$ 			drawContext,
	//$$ 			//#if MC >= 12106
	//$$ 			//$$ new Joml3x2fMatrixStack(drawContext.pose())
	//$$ 			//#else
	//$$ 			new McMatrixStack(drawContext.pose())
	//$$ 			//#endif
	//$$ 	);
	//$$ }
	//#else
	public GuiRenderContextImpl(@NotNull McMatrixStack matrixStack)
	{
		this.matrixStack = matrixStack;
	}
	//#endif

	//#if 11600 <= MC && MC < 12000
	//$$ @Override
	//$$ @NotNull
	//$$ public PoseStack getMcRawMatrixStack()
	//$$ {
	//$$ 	return this.matrixStack.asMcRaw();
	//$$ }
	//#endif

	@Override
	@NotNull
	public GuiComponent getGuiDrawer()
	{
		//#if MC >= 12000
		//$$ return this.drawContext;
		//#else
		return new GuiComponent(){};
		//#endif
	}

	@Override
	public void pushMatrix()
	{
		this.matrixStack.pushMatrix();
	}

	@Override
	public void popMatrix()
	{
		this.matrixStack.popMatrix();
	}

	@Override
	public void translate(double x, double y)
	{
		this.matrixStack.translate(x, y, 0);
	}

	@Override
	public void scale(double x, double y)
	{
		this.matrixStack.scale(x, y, 1);
	}

	//#if MC < 12106
	@Override
	public void translateDirect(double x, double y, double z)
	{
		this.matrixStack.translate(x, y, z);
	}

	@Override
	public void scaleDirect(double x, double y, double z)
	{
		this.matrixStack.scale(x, y, z);
	}
	//#endif
}
