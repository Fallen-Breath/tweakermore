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

package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.minihudHideIfChatScreenOpened;

import fi.dy.masa.minihud.event.RenderHandler;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(require = @Condition(ModIds.minihud))
@Mixin(RenderHandler.class)
public abstract class RenderHandlerMixin
{
	@Inject(
			//#if MC >= 12103
			//$$ method = "onRenderGameOverlayPostAdvanced",
			//#else
			method = "onRenderGameOverlayPost",
			//#endif
			at = @At(
					value = "FIELD",
					//#if MC >= 12002
					//$$ target = "Lnet/minecraft/client/option/GameOptions;hudHidden:Z",
					//#else
					target = "Lnet/minecraft/client/options/GameOptions;debugEnabled:Z",
					//#endif
					remap = true
			),
			cancellable = true,
			remap = false
	)
	private void minihudHideIfChatScreenOpened(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.MINIHUD_HIDE_IF_CHAT_SCREEN_OPENED.getBooleanValue())
		{
			if (MinecraftClient.getInstance().currentScreen instanceof ChatScreen)
			{
				ci.cancel();
			}
		}
	}
}
