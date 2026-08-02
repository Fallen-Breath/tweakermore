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

package me.fallenbreath.tweakermore.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

//#if MC >= 12108
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//#else
import me.fallenbreath.tweakermore.util.render.context.RenderGlobals;
//#endif

public class GameUtils
{
	public static boolean isOnRenderThread()
	{
		//#if MC >= 12108
		//$$ return RenderSystem.isOnRenderThread();
		//#else
		return RenderGlobals.isOnRenderThread();
		//#endif
	}

	public static void scheduleOnClientThread(Minecraft mc, Runnable runnable)
	{
		//#if MC >= 1.21.3
		//$$ mc.schedule(runnable);
		//#else
		mc.tell(runnable);
		//#endif
	}

	public static void scheduleOnClientThread(Runnable runnable)
	{
		scheduleOnClientThread(Minecraft.getInstance(), runnable);
	}

	public static Screen getCurrentMinecraftScreen(Minecraft mc)
	{
		//#if MC >= 26.2
		//$$ return mc.gui.screen();
		//#else
		return mc.screen;
		//#endif
	}

	public static Screen getCurrentMinecraftScreen()
	{
		return getCurrentMinecraftScreen(Minecraft.getInstance());
	}
}
