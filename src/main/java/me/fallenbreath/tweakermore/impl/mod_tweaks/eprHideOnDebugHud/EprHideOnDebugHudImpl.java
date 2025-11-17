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

package me.fallenbreath.tweakermore.impl.mod_tweaks.eprHideOnDebugHud;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class EprHideOnDebugHudImpl
{
	public static void applyHide(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.EPR_HIDE_ON_DEBUG_HUD.getBooleanValue())
		{
			Minecraft mc = Minecraft.getInstance();
			if (
					//#if MC >= 12002
					//$$ mc.getDebugOverlay().showDebugScreen()
					//#else
					mc.options.renderDebug
					//#endif
			)
			{
				ci.cancel();
			}
		}
	}
}
