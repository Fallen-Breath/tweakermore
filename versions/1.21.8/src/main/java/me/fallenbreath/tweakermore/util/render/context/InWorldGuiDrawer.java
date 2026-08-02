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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

//#if MC >= 26.2
//$$ import me.fallenbreath.tweakermore.util.render.TextRenderer;
//#endif

public class InWorldGuiDrawer implements AutoCloseable
{
	public static boolean initializing = false;
	private static final RunOnce<InWorldGuiDrawer> INSTANCE = new RunOnce<>(() -> {
		initializing = true;
		var inst = new InWorldGuiDrawer();
		initializing = false;
		return inst;
	});

	private final GuiGraphics drawContext;
	private final GuiRenderState guiState;
	private final GuiRenderer guiRenderer;

	//#if MC < 26.2
	private final FogRenderer fogRenderer;
	//#endif

	private InWorldGuiDrawer()
	{
		// reference: net.minecraft.client.renderer.GameRenderer#GameRenderer
		Minecraft mc = Minecraft.getInstance();

		//#if MC < 26.2
		var immediate = RenderUtils.getVertexConsumer();
		//#endif

		this.guiState = new GuiRenderState();

		//#if MC >= 1.21.11
		//$$ // TODO: check if mouseX,mouseY setting to 0,0 works
		//$$ this.drawContext = new GuiGraphics(mc, this.guiState, 0, 0);
		//#else
		this.drawContext = new GuiGraphics(mc, this.guiState);
		//#endif

		//#if MC >= 26.2
		//$$ this.guiRenderer = new GuiRenderer(
		//$$ 		guiState,
		//$$ 		mc.gameRenderer.featureRenderDispatcher(),
		//$$ 		List.of()
		//$$ );
		//#else
		this.guiRenderer = new GuiRenderer(
				this.guiState, immediate,
				//#if MC >= 1.21.9
				//$$ mc.gameRenderer.getSubmitNodeStorage(),  // TODO: check if this work
				//$$ mc.gameRenderer.getFeatureRenderDispatcher(),
				//#endif
				List.of()
		);
		//#endif

		((InWorldGuiRendererHook)this.guiRenderer).setInWorldGuiRender$TKM(true);

		//#if MC < 26.2
		this.fogRenderer = new FogRenderer();
		//#endif
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
		//#if MC >= 26.2
		//$$ TextRenderer.flushBatch();
		//#endif

		RenderSystem.backupProjectionMatrix();
		//#if MC >= 26.2
		//$$ this.guiRenderer.render();
		//#else
		this.guiRenderer.render(this.fogRenderer.getBuffer(FogRenderer.FogMode.NONE));
		//#endif
		RenderSystem.restoreProjectionMatrix();

		//#if MC >= 26.1
		//$$ this.guiRenderer.endFrame();
		//#else
		this.guiRenderer.incrementFrameNumber();
		//#endif

		//#if MC < 26.2
		this.fogRenderer.endFrame();
		//#endif

		this.guiState.reset();
	}

	@Override
	public void close()
	{
		this.guiRenderer.close();

		//#if MC < 26.2
		this.fogRenderer.close();
		//#endif
	}

	@NotNull
	public GuiGraphics getDrawContext()
	{
		return this.drawContext;
	}
}
