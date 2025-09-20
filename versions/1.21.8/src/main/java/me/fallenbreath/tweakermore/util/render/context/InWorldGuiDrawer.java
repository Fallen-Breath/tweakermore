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

import com.mojang.blaze3d.systems.RenderSystem;
import me.fallenbreath.tweakermore.util.RunOnce;
import me.fallenbreath.tweakermore.util.render.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.fog.FogRenderer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InWorldGuiDrawer implements AutoCloseable
{
	public static boolean initializing = false;
	private static final RunOnce<InWorldGuiDrawer> INSTANCE = new RunOnce<>(() -> {
		initializing = true;
		var inst = new InWorldGuiDrawer();
		initializing = false;
		return inst;
	});

	private final DrawContext drawContext;
	private final GuiRenderState guiState;
	private final GuiRenderer guiRenderer;
	private final FogRenderer fogRenderer;

	private InWorldGuiDrawer()
	{
		// reference: net.minecraft.client.render.GameRenderer#GameRenderer
		MinecraftClient mc = MinecraftClient.getInstance();
		VertexConsumerProvider.Immediate immediate = RenderUtils.getVertexConsumer();
		this.guiState = new GuiRenderState();
		this.drawContext = new DrawContext(mc, this.guiState);
		this.guiRenderer = new GuiRenderer(
				this.guiState, immediate,
				//#if MC >= 1.21.9
				//$$ mc.gameRenderer.getEntityRenderCommandQueue(),  // TODO: check if this work
				//$$ mc.gameRenderer.getEntityRenderDispatcher(),
				//#endif
				List.of()
		);
		((InWorldGuiRendererHook)this.guiRenderer).setInWorldGuiRender$TKM(true);
		this.fogRenderer = new FogRenderer();
	}

	public static InWorldGuiDrawer getInstance()
	{
		return INSTANCE.get();
	}

	public static void closeInstance()
	{
		if (INSTANCE.hasValue())
		{
			INSTANCE.get().close();
		}
	}

	public void render()
	{
		RenderSystem.backupProjectionMatrix();
		this.guiRenderer.render(this.fogRenderer.getFogBuffer(FogRenderer.FogType.NONE));
		RenderSystem.restoreProjectionMatrix();

		this.guiRenderer.incrementFrame();
		this.fogRenderer.rotate();
		this.guiState.clear();
	}

	@Override
	public void close()
	{
		this.guiRenderer.close();
		this.fogRenderer.close();
	}

	@NotNull
	public DrawContext getDrawContext()
	{
		return this.drawContext;
	}
}
