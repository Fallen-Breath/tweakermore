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

package me.fallenbreath.tweakermore.impl.mc_tweaks.windowSize;

import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.util.InfoUtils;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.Window;
import org.lwjgl.glfw.GLFW;

public class WindowSizeHelper
{
	public static void applyWindowSize()
	{
		Window window = getMcWindow();

		if (window == null)
		{
			return;
		}
		if (window.isFullscreen())
		{
			InfoUtils.showGuiOrInGameMessage(Message.MessageType.WARNING, "tweakermore.impl.windowSize.full_screen_nope");
			return;
		}

		applyWindowSizeImpl(window);
	}

	public static void onWindowSizeChanged(Window window)
	{
		if (!TweakerMoreConfigs.WINDOW_SIZE_PINNED.getBooleanValue())
		{
			return;
		}
		if (window.isFullscreen())
		{
			return;
		}

		int windowWidth = window.getScreenWidth();
		int windowHeight = window.getScreenHeight();
		int configWidth = getConfigWidth();
		int configHeight = getConfigHeight();

		if (windowWidth != configWidth || windowHeight != configHeight)
		{
			TweakerMoreMod.LOGGER.debug(
					"Window size was changed to ({}, {}) and is different to the configured size ({}, {}), resizing",
					windowWidth, windowHeight, configWidth, configHeight
			);
			applyWindowSizeImpl(window);
		}
	}

	public static void onConfigLoaded()
	{
		Window window = getMcWindow();
		if (canPerformSizeChange(window))
		{
			onWindowSizeChanged(window);
		}
	}

	private static Window getMcWindow()
	{
		Minecraft mc = Minecraft.getInstance();
		//#if MC >= 11500
		return mc.getWindow();
		//#else
		//$$ return mc.window;
		//#endif
	}

	private static boolean canPerformSizeChange(Window window)
	{
		return window != null && !window.isFullscreen();
	}

	private static int getConfigWidth()
	{
		return Math.max(1, TweakerMoreConfigs.WINDOW_SIZE_WIDTH.getIntegerValue());
	}

	private static int getConfigHeight()
	{
		return Math.max(1, TweakerMoreConfigs.WINDOW_SIZE_HEIGHT.getIntegerValue());
	}

	private static void applyWindowSizeImpl(Window window)
	{
		if (window.isFullscreen())
		{
			// remember to check this before calling applyWindowSizeImpl()
			throw new RuntimeException("resize in full screen");
		}
		GLFW.glfwSetWindowSize(window.getWindow(), getConfigWidth(), getConfigHeight());
	}
}
