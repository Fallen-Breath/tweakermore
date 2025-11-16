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

//#if MC >= 11600
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

/**
 * For those who needs in-game transformation and guiDrawer drawing (mc1.21.6+) (very hacky)
 * <p>
 * mc1.21.6-: subproject 1.15.2 (main project)        <--------
 * mc1.21.6+: subproject 1.21.8
 */
public class MixedRenderContext extends WorldRenderContextImpl
{
	private final InWorldGuiDrawer guiDrawer;

	public MixedRenderContext(
			@NotNull McMatrixStack matrixStack
	)
	{
		super(matrixStack);
		this.guiDrawer = new InWorldGuiDrawer(
				//#if MC >= 12000
				//$$ matrixStack.asMcRaw()
				//#endif
		);
	}

	public static MixedRenderContext create()
	{
		//#if MC >= 11600
		//$$ return new MixedRenderContext(new McMatrixStack(new MatrixStack()));
		//#else
		return new MixedRenderContext(new McMatrixStack());
		//#endif
	}

	//#if 11600 <= MC && MC < 12000
	//$$ public MatrixStack getMcRawMatrixStack()
	//$$ {
	//$$ 	return ((McMatrixStack)this.getMatrixStack()).asMcRaw();
	//$$ }
	//#endif

	@NotNull
	public GuiComponent getGuiDrawer()
	{
		return this.guiDrawer.getGuiDrawer();
	}
}
