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

import me.fallenbreath.tweakermore.util.render.matrix.IMatrixStack;
import net.minecraft.client.gui.DrawableHelper;  // will be remapped to DrawContext in mc1.20
import org.jetbrains.annotations.NotNull;

public interface GuiRenderContext
{
	// ============================= Getters =============================

	@NotNull
	IMatrixStack getMatrixStack();

	@NotNull
	DrawableHelper getGuiDrawer();

	// ============================= Manipulators =============================

	void pushMatrix();

	void popMatrix();

	void translate(double x, double y);

	void scale(double x, double y);

	//#if MC < 12106
	void translateDirect(double x, double y, double z);
	void scaleDirect(double x, double y, double z);
	//#endif
}
