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
import net.minecraft.client.gui.DrawableHelper;
import org.jetbrains.annotations.NotNull;

//#if 11600 <= MC && MC < 12000
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

//#if MC >= 11600
//$$ import me.fallenbreath.tweakermore.util.render.matrix.McMatrixStack;
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

/**
 * For those who needs in-game transformation and guiDrawer drawing (mc1.21.6+) (very hacky)
 * <p>
 * mc1.21.6-: subproject 1.15.2 (main project)        <--------
 * mc1.21.6+: subproject 1.21.6
 */
public class MixedRenderContext extends WorldRenderContextImpl implements GuiRenderContext
{
	//#if MC >= 12000
	//$$ @NotNull
	//$$ private final DrawContext drawContext;
	//#endif

	public MixedRenderContext(
			//#if MC >= 12000
			//$$ @NotNull DrawContext drawContext,
			//#endif
			@NotNull McMatrixStack matrixStack
	)
	{
		super(matrixStack);
		//#if MC >= 12000
		//$$ this.drawContext = drawContext;
		//#endif
	}

	public static MixedRenderContext create()
	{
		//#if MC >= 12000
		//$$ var matrixStack = new MatrixStack();
		//$$ return new MixedRenderContext(RenderContextUtils.createDrawContext(matrixStack), new McMatrixStack(matrixStack));
		//#elseif MC >= 11600
		//$$ return new MixedRenderContext(new McMatrixStack(new MatrixStack()));
		//#else
		return new MixedRenderContext(new McMatrixStack());
		//#endif
	}

	//#if 11600 <= MC && MC < 12000
	//$$ @Override
	//$$ public MatrixStack getMcRawMatrixStack()
	//$$ {
	//$$ 	return ((McMatrixStack)this.getMatrixStack()).asMcRaw();
	//$$ }
	//#endif

	@Override
	@NotNull
	public DrawableHelper getGuiDrawer()
	{
		//#if MC >= 12000
		//$$ return this.drawContext;
		//#else
		return new DrawableHelper(){};
		//#endif
	}

	@Override
	public void translate(double x, double y)
	{
		this.translate(x, y, 0);
	}

	@Override
	public void scale(double x, double y)
	{
		this.scale(x, y, 1);
	}

	@Override
	public void translateDirect(double x, double y, double z)
	{
		this.translate(x, y, z);
	}

	@Override
	public void scaleDirect(double x, double y, double z)
	{
		this.scale(x, y, z);
	}
}
