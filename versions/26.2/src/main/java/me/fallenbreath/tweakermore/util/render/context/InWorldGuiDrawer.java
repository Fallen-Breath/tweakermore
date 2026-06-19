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

import me.fallenbreath.tweakermore.util.RunOnce;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
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

	private final GuiGraphicsExtractor drawContext;
	private final GuiRenderer guiRenderer;

	private InWorldGuiDrawer()
	{
		// reference: net.minecraft.client.renderer.GameRenderer#GameRenderer
		Minecraft mc = Minecraft.getInstance();
		GuiRenderState guiState = new GuiRenderState();
		// TODO: check if mouseX,mouseY setting to 0,0 works
		this.drawContext = new GuiGraphicsExtractor(mc, guiState, 0, 0);
		this.guiRenderer = new GuiRenderer(
				guiState,
				mc.gameRenderer.featureRenderDispatcher(),
				List.of()
		);
		((InWorldGuiRendererHook)this.guiRenderer).setInWorldGuiRender$TKM(true);
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
		this.guiRenderer.render();
		this.guiRenderer.endFrame();
	}

	@Override
	public void close()
	{
		this.guiRenderer.close();
	}

	@NotNull
	public GuiGraphicsExtractor getDrawContext()
	{
		return this.drawContext;
	}
}
