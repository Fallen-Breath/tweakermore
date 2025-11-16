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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.keepMessageHistoryOnReconfiguration;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.keepMessageHistoryOnReconfiguration.KeepMessageHistoryOnReconfigurationHelper;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public abstract class InGameHudMixin
{
	@SuppressWarnings("RedundantIfStatement")
	@WrapWithCondition(
			method = "clear",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/components/ChatComponent;clearMessages(Z)V"
			)
	)
	private boolean keepMessageOnReconfiguration_dontClearChatHud(ChatComponent instance, boolean clearHistory)
	{
		if (TweakerMoreConfigs.KEEP_MESSAGE_HISTORY_ON_RECONFIGURATION.getBooleanValue())
		{
			if (KeepMessageHistoryOnReconfigurationHelper.isReconfiguring.get())
			{
				return false;
			}
		}
		return true;
	}
}
