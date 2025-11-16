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

import com.llamalad7.mixinextras.sugar.Local;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = "<1.20"))
@Mixin(ClientPacketListener.class)
public abstract class ClientPlayNetworkHandlerMixin
{
	@Shadow private Minecraft minecraft;

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
			method = "handlePlayerInfo",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/multiplayer/PlayerInfo;setGameMode(Lnet/minecraft/world/level/GameType;)V"
			)
	)
	private void mcSpectatorEnterSinkingFixPorting_onGameModeUpdate(ClientboundPlayerInfoPacket packet, CallbackInfo ci, @Local ClientboundPlayerInfoPacket.PlayerUpdate packetEntry, @Local PlayerInfo playerListEntry)
	//#endif
	{
		if (TweakerMoreConfigs.MC_SPECTATOR_ENTER_SINKING_FIX_PORTING.getBooleanValue())
		{
			LocalPlayer player = this.minecraft.player;

			// is the client's player
			if (player != null && player.getUUID().equals(
					//#if MC >= 11903
					//$$ packetEntry.profileId()
					//#else
					packetEntry.getProfile().getId()
					//#endif
			))
			{
				GameType newGameMode =
						//#if MC >= 11903
						//$$ packetEntry.gameMode();
						//#else
						packetEntry.getGameMode();
						//#endif
				if (newGameMode == GameType.SPECTATOR && newGameMode != playerListEntry.getGameMode())  // entering spectator
				{
					// clean the velocity at y-axis
					player.setDeltaMovement(player.getDeltaMovement().multiply(1, 0, 1));
				}
			}
		}
	}
}
