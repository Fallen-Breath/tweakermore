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

import net.minecraft.client.gui.DrawableHelper;
import org.jetbrains.annotations.NotNull;

//#if MC >= 12000
//$$ import me.fallenbreath.tweakermore.util.render.RenderUtils;
//$$ import me.fallenbreath.tweakermore.mixins.util.render.DrawContextAccessor;
//$$ import net.minecraft.client.MinecraftClient;
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

public class InWorldGuiDrawer
{
	//#if MC >= 12000
	//$$ @NotNull
	//$$ private final DrawContext drawContext;
	//$$
	//$$ public InWorldGuiDrawer(MatrixStack matrixStack)
	//$$ {
	//$$ 	this.drawContext = createDrawContext(matrixStack);
	//$$ }
	//#endif

	@NotNull
	public DrawableHelper getGuiDrawer()
	{
		//#if MC >= 12000
		//$$ return this.drawContext;
		//#else
		return new DrawableHelper(){};
		//#endif
	}

	//#if MC >= 12000
	//$$ private static DrawContext createDrawContext(MatrixStack matrixStack)
	//$$ {
	//$$ 	var drawContext = new DrawContext(MinecraftClient.getInstance(), RenderUtils.getVertexConsumer());
	//$$ 	((DrawContextAccessor)drawContext).setMatrices(matrixStack);
	//$$ 	return drawContext;
	//$$ }
	//#endif
}
