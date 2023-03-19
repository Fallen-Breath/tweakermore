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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.legacyF3NLogic;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = ">=1.16"))
@Mixin(Keyboard.class)
public abstract class KeyboardMixin
{
	@Shadow @Final private MinecraftClient client;

	@Inject(
			method = "processF3",
			slice = @Slice(
					from = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/client/Keyboard;copyLookAt(ZZ)V"
					)
			),
			at = @At(
					//#if MC >= 11600
					//$$ value = "INVOKE",
					//$$ target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSpectator()Z",
					//$$ ordinal = 0
					//#else
					// this mixin will not be applied due to @Restriction annotation
					// so just use a dummy target point here
					value = "HEAD"
					//#endif
			),
			cancellable = true
	)
	private void legacyF3NLogic(CallbackInfoReturnable<Boolean> cir)
	{
		if (TweakerMoreConfigs.LEGACY_F3_N_LOGIC.getBooleanValue())
		{
			assert this.client.player != null;

			Consumer<String> commandSender =
					//#if MC >= 11903
					//$$ this.client.player.networkHandler::sendCommand;
					//#else
					cmd -> this.client.player.sendChatMessage("/" + cmd);
					//#endif

			if (this.client.player.isCreative())
			{
				commandSender.accept("gamemode spectator");
			}
			else
			{
				commandSender.accept("gamemode creative");
			}
			cir.setReturnValue(true);
		}
	}
}