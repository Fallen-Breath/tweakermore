/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024  Fallen_Breath and contributors
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

import org.jetbrains.annotations.NotNull;

//#if MC >= 12006
//$$ import me.fallenbreath.tweakermore.util.render.matrix.JomlMatrixStack;
//#else
import me.fallenbreath.tweakermore.util.render.matrix.McMatrixStack;
//#endif

//#if MC >= 12000
//$$ import net.minecraft.client.gui.DrawContext;
//$$ import org.jetbrains.annotations.Nullable;
//#endif

// TODO: resolve those inelegant `getMatrixStack().asMcRaw()` spams
public class WorldRenderContextImpl extends RenderContextImpl
{
	WorldRenderContextImpl(
			//#if MC >= 12000
			//$$ @Nullable DrawContext drawContext,
			//#endif

			//#if MC >= 12006
			//$$ @NotNull JomlMatrixStack matrixStack
			//#else
			@NotNull McMatrixStack matrixStack
			//#endif
	)
	{
		super(
				//#if MC >= 12000
				//$$ drawContext,
				//#endif
				matrixStack
		);
	}
}
