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
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JoinMultiplayerScreen.class)
public abstract class MultiplayerScreenMixin
{
	@Shadow
	private ServerList servers;

	@Inject(
			method = "join(Lnet/minecraft/client/multiplayer/ServerData;)V",
			at = @At("HEAD")
	)
	private void moveConnectedServerEntryToTop_recordServerInfoIndex(ServerData entry, CallbackInfo ci)
	{
		if (!TweakerMoreConfigs.MOVE_CONNECTED_SERVER_ENTRY_TO_TOP.getBooleanValue())
		{
			return;
		}

		MoveConnectedServerEntryToTopHelper.selectedIndex = -1;
		if (this.servers == null)
		{
			return;
		}

		int idx = ((ServerListAccessor)this.servers).getServers().indexOf(entry);
		if (idx != -1)
		{
			MoveConnectedServerEntryToTopHelper.selectedIndex = idx;
		}
	}
}
