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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = "<1.20"))
@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin
{
	@Shadow private MinecraftClient client;

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
			)
	)
	private void mcSpectatorEnterSinkingFixPorting_onGameModeUpdate(PlayerListS2CPacket packet, CallbackInfo ci, @Local PlayerListS2CPacket.Entry packetEntry, @Local PlayerListEntry playerListEntry)
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
