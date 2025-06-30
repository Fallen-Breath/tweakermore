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

import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

/**
 * For those who needs in-game transformation and guiDrawer drawing (mc1.21.6+) (very hacky)
 * <p>
 * mc1.21.6-: subproject 1.15.2 (main project)
 * mc1.21.6+: subproject 1.21.6        <--------
 */
public class MixedRenderContext implements WorldRenderContext
{
	private final WorldRenderContext worldRenderContext;
	private final InWorldGuiDrawer guiDrawer;

	public MixedRenderContext(WorldRenderContext worldRenderContext)
	{
		this.worldRenderContext = worldRenderContext;
		this.guiDrawer = InWorldGuiDrawer.getInstance();
	}

	public static MixedRenderContext create(WorldRenderContext worldRenderContext)
	{
		return new MixedRenderContext(worldRenderContext);
	}

	public void renderGuiElements()
	{
		this.guiDrawer.render();
	}

	@NotNull
	public DrawContext getGuiDrawer()
	{
		return this.guiDrawer.getDrawContext();
	}

	@Override
	public void pushMatrix()
	{
		this.worldRenderContext.pushMatrix();
	}

	@Override
	public void popMatrix()
	{
		this.worldRenderContext.popMatrix();
	}

	@Override
	public void translate(double x, double y, double z)
	{
		this.worldRenderContext.translate(x, y, z);
	}

	@Override
	public void scale(double x, double y, double z)
	{
		this.worldRenderContext.scale(x, y, z);
	}

	@Override
	public void multMatrix(Matrix4f matrix4f)
	{
		this.worldRenderContext.multMatrix(matrix4f);
	}
}
