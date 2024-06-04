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

package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.ofPlayerExtraModelOverride;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.impl.mod_tweaks.ofPlayerExtraModelOverride.OptifinePlayerExtraModelOverrider;
import me.fallenbreath.tweakermore.impl.mod_tweaks.ofPlayerExtraModelOverride.OverrideImpl;
import me.fallenbreath.tweakermore.impl.mod_tweaks.ofPlayerExtraModelOverride.PlayerConfigurationParserWithOverride;
import me.fallenbreath.tweakermore.util.ModIds;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@SuppressWarnings({"UnresolvedMixinReference", "UnusedMixin"})
@Restriction(require = @Condition(ModIds.optifine))
@Pseudo
@Mixin(targets = "net.optifine.player.PlayerConfigurationReceiver")
public abstract class PlayerConfigurationReceiverMixin
{
	@Shadow(remap = false)
	private String player;

	@Unique
	@Nullable
	private OverrideImpl currentOverrideImpl = null;

	@ModifyVariable(
			method = "fileDownloadFinished",
			at = @At("HEAD"),
			argsOnly = true,
			remap = false
	)
	private byte[] replacePlayerCfgContent$TKM(byte[] bytes)
	{
		Optional<OverrideImpl> override = OptifinePlayerExtraModelOverrider.overridePlayerConfig(this.player);
		this.currentOverrideImpl = null;

		if (override.isPresent())
		{
			this.currentOverrideImpl = override.get();
			bytes = override.get().cfg;
		}

		return bytes;
	}

	@Inject(
			method = "fileDownloadFinished",
			at = @At(
					value = "INVOKE",
					target = "Lnet/optifine/player/PlayerConfigurationParser;parsePlayerConfiguration(Lcom/google/gson/JsonElement;)Lnet/optifine/player/PlayerConfiguration;"
			),
			remap = false,
			locals = LocalCapture.CAPTURE_FAILSOFT
	)
	private void attachOverrideToPlayerConfigParser$TKM(
			String url, byte[] bytes, Throwable exception,
			CallbackInfo ci,
			String s, JsonParser jsonparser, JsonElement jsonelement,
			@Coerce Object playerconfigurationparser
	)
	{
		if (this.currentOverrideImpl != null)
		{
			((PlayerConfigurationParserWithOverride)playerconfigurationparser).setOverride$TKM(this.currentOverrideImpl);
		}
	}
}
