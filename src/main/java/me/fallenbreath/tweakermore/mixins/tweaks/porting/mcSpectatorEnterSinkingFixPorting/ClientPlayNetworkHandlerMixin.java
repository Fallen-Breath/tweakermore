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

package me.fallenbreath.tweakermore.mixins.tweaks.porting.mcSpectatorEnterSinkingFixPorting;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

//#if MC >= 12002
//$$ import net.minecraft.client.network.ClientCommonNetworkHandler;
//$$ import net.minecraft.client.network.ClientConnectionState;
//$$ import net.minecraft.network.ClientConnection;
//#else
import org.spongepowered.asm.mixin.Shadow;
//#endif

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin
		//#if MC >= 12002
		//$$ extends ClientCommonNetworkHandler
		//#endif
{
	//#if MC >= 12002
	//$$ protected ClientPlayNetworkHandlerMixin(MinecraftClient client, ClientConnection connection, ClientConnectionState connectionState)
	//$$ {
	//$$ 	super(client, connection, connectionState);
	//$$ }
	//#else
	@Shadow private MinecraftClient client;
	//#endif

	//#if MC >= 11903
	//$$ @Inject(
	//$$ 		method = "handlePlayerListAction",
	//$$ 		at = @At(
	//$$ 				value = "INVOKE",
	//$$ 				target = "Lnet/minecraft/client/network/PlayerListEntry;setGameMode(Lnet/minecraft/world/GameMode;)V"
	//$$ 		)
	//$$ )
	//$$ private void mcSpectatorEnterSinkingFixPorting_onGameModeUpdate(PlayerListS2CPacket.Action action, PlayerListS2CPacket.Entry packetEntry, PlayerListEntry playerListEntry, CallbackInfo ci)
	//#else
	@Inject(
			method = "onPlayerList",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/network/PlayerListEntry;setGameMode(Lnet/minecraft/world/GameMode;)V"
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void mcSpectatorEnterSinkingFixPorting_onGameModeUpdate(PlayerListS2CPacket packet, CallbackInfo ci, Iterator<?> iterator, PlayerListS2CPacket.Entry packetEntry, PlayerListEntry playerListEntry)
	//#endif
	{
		if (TweakerMoreConfigs.MC_SPECTATOR_ENTER_SINKING_FIX_PORTING.getBooleanValue())
		{
			ClientPlayerEntity player = this.client.player;

			// is the client's player
			if (player != null && player.getUuid().equals(
					//#if MC >= 11903
					//$$ packetEntry.profileId()
					//#else
					packetEntry.getProfile().getId()
					//#endif
			))
			{
				GameMode newGameMode =
						//#if MC >= 11903
						//$$ packetEntry.gameMode();
						//#else
						packetEntry.getGameMode();
						//#endif
				if (newGameMode == GameMode.SPECTATOR && newGameMode != playerListEntry.getGameMode())  // entering spectator
				{
					// clean the velocity at y-axis
					player.setVelocity(player.getVelocity().multiply(1, 0, 1));
				}
			}
		}
	}
}
