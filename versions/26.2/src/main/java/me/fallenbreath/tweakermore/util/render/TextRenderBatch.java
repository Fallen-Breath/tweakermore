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

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Batches {@link TextRenderer} geometry into a shared {@link StagedVertexBuffer}. The build, upload, and execute
 * flow follows Minecraft 26.2's {@code TextFeatureRenderer}, {@code RenderTypeFeatureRenderer}, and {@code StagedVertexBuffer}.
 * Unlike vanilla feature submission, this batch runs from TweakerMore's late world-render hook, after the feature frame is prepared
 */
public final class TextRenderBatch implements AutoCloseable
{
	private static final int INITIAL_CAPACITY = 65536;

	@Nullable
	private static TextRenderBatch sharedBatch;
	@Nullable
	private static TextRenderBatch activeBatch;

	private final StagedVertexBuffer stagedBuffer;
	// A GUI barrier may flush while another renderer has a local model-view transform applied.
	private final Matrix4f baseModelView = new Matrix4f();
	private final List<DrawEntry> draws = new ArrayList<>();
	@Nullable
	private DrawEntry lastDraw;

	private TextRenderBatch()
	{
		this.stagedBuffer = new StagedVertexBuffer(() -> "TweakerMore TextRenderer", INITIAL_CAPACITY);
		this.captureBaseModelView();
	}

	public static void beginBatch()
	{
		RenderSystem.assertOnRenderThread();
		if (activeBatch != null)
		{
			throw new IllegalStateException("Text render batch has already started");
		}
		if (sharedBatch == null)
		{
			sharedBatch = new TextRenderBatch();
		}
		sharedBatch.captureBaseModelView();
		activeBatch = sharedBatch;
	}

	public static void endBatch()
	{
		RenderSystem.assertOnRenderThread();
		TextRenderBatch batch = activeBatch;
		if (batch == null)
		{
			throw new IllegalStateException("Text render batch has not started");
		}

		activeBatch = null;
		try
		{
			batch.flush();
		}
		finally
		{
			batch.stagedBuffer.endFrame();
		}
	}

	public static void flushActiveBatch()
	{
		RenderSystem.assertOnRenderThread();
		if (activeBatch != null)
		{
			activeBatch.flush();
		}
	}

	public static void closeSharedBatch()
	{
		RenderSystem.assertOnRenderThread();
		if (activeBatch != null)
		{
			try
			{
				activeBatch.flush();
			}
			finally
			{
				activeBatch.stagedBuffer.endFrame();
				activeBatch = null;
			}
		}
		if (sharedBatch != null)
		{
			sharedBatch.close();
			sharedBatch = null;
		}
	}

	@Nullable
	public static TextRenderBatch getActiveBatch()
	{
		return activeBatch;
	}

	public static TextRenderBatch createImmediate()
	{
		return new TextRenderBatch();
	}

	public StagedVertexBuffer.Draw getOrCreateDraw(RenderType renderType, boolean canMergeWithPreviousGroup)
	{
		// Only the first RenderType of a TextRenderer may merge across its ordering boundary.
		if (canMergeWithPreviousGroup && this.lastDraw != null && this.lastDraw.renderType == renderType && renderType.canConsolidateConsecutiveGeometry())
		{
			return this.lastDraw.draw;
		}

		StagedVertexBuffer.Draw draw = this.stagedBuffer.appendDraw(
				renderType.format(),
				renderType.primitiveTopology(),
				renderType.sortOnUpload() ? RenderSystem.getProjectionType().vertexSorting() : null
		);
		this.lastDraw = new DrawEntry(renderType, draw);
		this.draws.add(this.lastDraw);
		return draw;
	}

	public StagedVertexBuffer getStagedBuffer()
	{
		return this.stagedBuffer;
	}

	public void flush()
	{
		if (this.draws.isEmpty())
		{
			return;
		}

		var modelViewStack = RenderSystem.getModelViewStack();
		modelViewStack.pushMatrix();
		modelViewStack.set(this.baseModelView);
		try
		{
			// Follow vanilla's staged flow: upload once, execute ordered draws, then end the draw.
			this.stagedBuffer.upload();
			for (DrawEntry entry : this.draws)
			{
				StagedVertexBuffer.ExecuteInfo executeInfo = this.stagedBuffer.getExecuteInfo(entry.draw);
				if (executeInfo != null)
				{
					entry.renderType.prepare().drawFromBuffer(executeInfo);
				}
			}
		}
		finally
		{
			try
			{
				this.stagedBuffer.endDraw();
				this.draws.clear();
				this.lastDraw = null;
			}
			finally
			{
				modelViewStack.popMatrix();
			}
		}
	}

	private void captureBaseModelView()
	{
		this.baseModelView.set(RenderSystem.getModelViewStack());
	}

	@Override
	public void close()
	{
		this.stagedBuffer.close();
	}

	private record DrawEntry(RenderType renderType, StagedVertexBuffer.Draw draw)
	{
	}
}
