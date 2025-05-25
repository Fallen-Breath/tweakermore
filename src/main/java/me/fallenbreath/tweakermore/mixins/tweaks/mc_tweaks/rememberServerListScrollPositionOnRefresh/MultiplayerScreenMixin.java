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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.rememberServerListScrollPositionOnRefresh;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public abstract class MultiplayerScreenMixin
{
	@Inject(
			method = "refresh",
			at = @At("TAIL")  // at TAIL, the newScreen has performed init(), so its `serverListWidget` field has been initialized
	)
	private void rememberServerListScrollPositionOnRefresh_inheritScrollPosition(CallbackInfo ci)
	{
		if (!TweakerMoreConfigs.REMEMBER_SERVER_LIST_SCROLL_POSITION_ON_REFRESH.getBooleanValue())
		{
			return;
		}

		Screen oldScreen = (MultiplayerScreen)(Object)this;
		Screen newScreen = MinecraftClient.getInstance().currentScreen;
		if (!(oldScreen instanceof MultiplayerScreenAccessor && newScreen instanceof MultiplayerScreenAccessor))
		{
			return;
		}

		MultiplayerServerListWidget oldWidget = ((MultiplayerScreenAccessor)oldScreen).getServerListWidget();
		MultiplayerServerListWidget newWidget = ((MultiplayerScreenAccessor)newScreen).getServerListWidget();
		if (oldWidget == null || newWidget == null)
		{
			return;
		}

		//#if MC >= 12104
		//$$ newWidget.setScrollY(oldWidget.getScrollY());
		//#else
		newWidget.setScrollAmount(oldWidget.getScrollAmount());
		//#endif
	}
}
