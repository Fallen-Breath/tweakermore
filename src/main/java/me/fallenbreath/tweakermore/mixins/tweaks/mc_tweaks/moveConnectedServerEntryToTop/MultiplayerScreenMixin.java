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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.moveConnectedServerEntryToTop;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.moveConnectedServerEntryToTop.MoveConnectedServerEntryToTopHelper;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.options.ServerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public abstract class MultiplayerScreenMixin
{
	@Shadow
	private ServerList serverList;

	@Inject(
			method = "connect(Lnet/minecraft/client/network/ServerInfo;)V",
			at = @At("HEAD")
	)
	private void moveConnectedServerEntryToTop_recordServerInfoIndex(ServerInfo entry, CallbackInfo ci)
	{
		if (!TweakerMoreConfigs.MOVE_CONNECTED_SERVER_ENTRY_TO_TOP.getBooleanValue())
		{
			return;
		}

		MoveConnectedServerEntryToTopHelper.selectedIndex = -1;
		if (this.serverList == null)
		{
			return;
		}

		int idx = ((ServerListAccessor)this.serverList).getServers().indexOf(entry);
		if (idx != -1)
		{
			MoveConnectedServerEntryToTopHelper.selectedIndex = idx;
		}
	}
}
