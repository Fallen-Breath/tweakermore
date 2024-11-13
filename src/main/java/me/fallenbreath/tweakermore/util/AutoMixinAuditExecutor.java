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

package me.fallenbreath.tweakermore.util;

import fi.dy.masa.malilib.event.TickHandler;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.impl.setting.debug.TweakerMoreDebugHelper;
import net.fabricmc.loader.api.FabricLoader;

public class AutoMixinAuditExecutor
{
	private static final String KEYWORD_PROPERTY = "tweakermore.mixin_audit";

	public static void run()
	{
		if (FabricLoader.getInstance().isDevelopmentEnvironment() && "true".equals(System.getProperty(KEYWORD_PROPERTY)))
		{
			TickHandler.getInstance().registerClientTickHandler(mc -> {
				TweakerMoreMod.LOGGER.info("Mixin audit started");
				TweakerMoreDebugHelper.forceLoadAllMixins();
				TweakerMoreMod.LOGGER.info("Mixin audit passed");
				System.exit(0);
			});
		}
	}
}
