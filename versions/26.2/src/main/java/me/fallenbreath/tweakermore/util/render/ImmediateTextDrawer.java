/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.util.render;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import java.util.LinkedHashMap;
import java.util.Map;

public class ImmediateTextDrawer implements Font.GlyphVisitor, AutoCloseable
{
	private final Matrix4fc pose;
	private final Font.DisplayMode displayMode;
	private final int lightCoords;
	private final TextRenderBatch batch;
	private final boolean immediate;
	private final Map<RenderType, StagedVertexBuffer.Draw> draws = new LinkedHashMap<>();

	ImmediateTextDrawer(Matrix4fc pose, Font.DisplayMode displayMode, int lightCoords)
	{
		this.pose = new Matrix4f(pose);
		this.displayMode = displayMode;
		this.lightCoords = lightCoords;
		TextRenderBatch activeBatch = TextRenderBatch.getActiveBatch();
		this.immediate = activeBatch == null;
		this.batch = this.immediate ? TextRenderBatch.createImmediate() : activeBatch;
	}

	public void append(Font.PreparedText preparedText)
	{
		preparedText.visit(this);
	}

	@Override
	public void acceptRenderable(TextRenderable renderable)
	{
		RenderType renderType = renderable.renderType(this.displayMode);
		StagedVertexBuffer.Draw draw = this.draws.get(renderType);
		if (draw == null)
		{
			draw = this.batch.getOrCreateDraw(renderType, this.draws.isEmpty());
			this.draws.put(renderType, draw);
		}
		renderable.render(this.pose, this.batch.getStagedBuffer().getVertexBuilder(draw), this.lightCoords, false);
	}

	public void draw()
	{
		if (this.immediate)
		{
			this.batch.flush();
		}
	}

	@Override
	public void close()
	{
		if (this.immediate)
		{
			this.batch.flush();
			this.batch.close();
		}
	}
}
