/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.mixins.tweaks.features.spectatorTeleportCommand;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.gui.spectator.PlayerMenuItem;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerMenuItem.class)
public abstract class PlayerMenuItemMixin
{
	@ModifyExpressionValue(
			method = "isEnabled",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/multiplayer/PlayerInfo;getGameMode()Lnet/minecraft/world/level/GameType;"
			)
	)
	private GameType w(GameType gameType)
	{
		// ensure the "this.playerInfo.getGameMode() != GameType.SPECTATOR" check can pass
		if (TweakerMoreConfigs.SPECTATOR_TELEPORT_COMMAND.getBooleanValue())
		{
			gameType = GameType.SURVIVAL;
		}
		return gameType;
	}
}
