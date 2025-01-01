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

package me.fallenbreath.tweakermore.config;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.InputEventHandler;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.util.render.TweakerMoreRenderEventHandler;

//#if MC >= 12101
//$$ import fi.dy.masa.malilib.registry.Registry;
//$$ import fi.dy.masa.malilib.util.data.ModInfo;
//$$ import me.fallenbreath.tweakermore.gui.TweakerMoreConfigGui;
//#endif

public class MalilibStuffsInitializer
{
	public static void init()
	{
		InitializationHandler.getInstance().registerInitializationHandler(() -> {
			ConfigManager.getInstance().registerConfigHandler(TweakerMoreMod.MOD_ID, TweakerMoreConfigStorage.getInstance());
			InputEventHandler.getKeybindManager().registerKeybindProvider(new KeybindProvider());

			//#if MC >= 12101
			//$$ Registry.CONFIG_SCREEN.registerConfigScreenFactory(new ModInfo(TweakerMoreMod.MOD_ID, TweakerMoreMod.MOD_NAME, TweakerMoreConfigGui::new));
			//#endif

			TweakerMoreRenderEventHandler.init();
			TweakerMoreConfigs.initConfigs();
		});
	}
}
