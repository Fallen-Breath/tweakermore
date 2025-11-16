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
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = ">=1.16"))
@Mixin(KeyboardHandler.class)
public abstract class KeyboardMixin
{
	@Shadow @Final private Minecraft minecraft;

	//#if MC < 11600
	@SuppressWarnings("MixinAnnotationTarget")
	//#endif
	@Inject(
			method = "handleDebugKeys",
			slice = @Slice(
					from = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/client/KeyboardHandler;copyRecreateCommand(ZZ)V"
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
			assert this.minecraft.player != null;

			Consumer<String> commandSender =
					//#if MC >= 12106
					//$$ this.client.player.networkHandler::sendChatCommand;
					//#elseif MC >= 11903
					//$$ this.client.player.networkHandler::sendCommand;
					//#else
					cmd -> this.minecraft.player.chat("/" + cmd);
					//#endif

			if (this.minecraft.player.isCreative())
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