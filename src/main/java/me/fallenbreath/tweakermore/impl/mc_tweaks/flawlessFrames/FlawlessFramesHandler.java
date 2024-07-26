/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.impl.mc_tweaks.flawlessFrames;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;

import java.util.List;

public class FlawlessFramesHandler
{
	private static final List<String> HOOK_CLASSES = ImmutableList.of(
			"com.replaymod.render.hooks.ChunkLoadingRenderGlobal",  // in ReplayMod <=2.5.2
			"com.replaymod.render.hooks.ForceChunkLoadingHook"  // in ReplayMod >=2.6.0
	);
	private static Object hook = null;

	private static void installHook()
	{
		hook = null;
		Class<?> clazz = null;
		WorldRenderer worldRenderer = MinecraftClient.getInstance().worldRenderer;
		for (String hookClass : HOOK_CLASSES)
		{
			try
			{
				clazz = Class.forName(hookClass);
				break;
			}
			catch (ClassNotFoundException ignored)
			{
			}
		}
		if (clazz == null)
		{
			TweakerMoreMod.LOGGER.error("Failed to located replay flawless rendering hook, searched {}", HOOK_CLASSES);
			return;
		}
		try
		{
			hook = clazz.getConstructor(WorldRenderer.class).newInstance(worldRenderer);
		}
		catch (Exception e)
		{
			TweakerMoreMod.LOGGER.error("Failed to construct replay flawless rendering hook", e);
		}
	}

	private static void uninstallHook()
	{
		try
		{
			hook.getClass().getMethod("uninstall").invoke(hook);
		}
		catch (Exception e)
		{
			TweakerMoreMod.LOGGER.error("Failed to invoke method uninstall of replay flawless rendering hook {}", hook, e);
		}
		hook = null;
	}

	public static void onConfigValueChanged(ConfigBoolean config)
	{
		boolean enabled = config.getBooleanValue();
		if (hook == null && enabled)
		{
			installHook();
		}
		else if (hook != null && !enabled)
		{
			uninstallHook();
		}
	}

	/**
	 * Re-hook after replay uninstall its hook for rendering or taking snapshot
	 */
	public static void refreshHook()
	{
		if (hook != null)
		{
			uninstallHook();
			installHook();
		}
	}
}
