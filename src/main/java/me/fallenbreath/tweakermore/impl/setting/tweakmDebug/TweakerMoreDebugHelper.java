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

package me.fallenbreath.tweakermore.impl.setting.tweakmDebug;

import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.util.InfoUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import org.spongepowered.asm.mixin.MixinEnvironment;

public class TweakerMoreDebugHelper
{
	public static void resetAllConfigStatistic()
	{
		TweakerMoreConfigs.getAllOptions().forEach(option -> option.getStatistic().reset());
	}

	public static void forceLoadAllMixins()
	{
		MixinEnvironment.getCurrentEnvironment().audit();
		InfoUtils.showGuiOrInGameMessage(Message.MessageType.SUCCESS, "tweakermore.impl.tweakerMoreDevMixinAudit.success");
	}
}
