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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JoinMultiplayerScreen.class)
public abstract class MultiplayerScreenMixin
{
	@Inject(
			method = "refreshServerList",
			at = @At("TAIL")  // at TAIL, the newScreen has performed init(), so its `serverListWidget` field has been initialized
	)
	private void rememberServerListScrollPositionOnRefresh_inheritScrollPosition(CallbackInfo ci)
	{
		if (!TweakerMoreConfigs.REMEMBER_SERVER_LIST_SCROLL_POSITION_ON_REFRESH.getBooleanValue())
		{
			return;
		}

		Screen oldScreen = (JoinMultiplayerScreen)(Object)this;
		Screen newScreen = Minecraft.getInstance().screen;
		if (!(oldScreen instanceof MultiplayerScreenAccessor && newScreen instanceof MultiplayerScreenAccessor))
		{
			return;
		}

		ServerSelectionList oldWidget = ((MultiplayerScreenAccessor)oldScreen).getServerListWidget();
		ServerSelectionList newWidget = ((MultiplayerScreenAccessor)newScreen).getServerListWidget();
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
