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

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.impl.mod_tweaks.ofPlayerExtraModelOverride.OverrideImpl;
import me.fallenbreath.tweakermore.impl.mod_tweaks.ofPlayerExtraModelOverride.PlayerConfigurationParserWithOverride;
import me.fallenbreath.tweakermore.util.ModIds;
import me.fallenbreath.tweakermore.util.ReflectionUtil;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.Proxy;

@SuppressWarnings({"UnresolvedMixinReference", "UnusedMixin"})
@Restriction(require = @Condition(ModIds.optifine))
@Pseudo
@Mixin(targets = "net.optifine.player.PlayerConfigurationParser")
public abstract class PlayerConfigurationParserMixin implements PlayerConfigurationParserWithOverride
{
	@Unique
	private OverrideImpl override = null;

	@Override
	public void setOverride$TKM(OverrideImpl override)
	{
		this.override = override;
	}

	@Redirect(
			method = "downloadModel",
			at = @At(
					value = "INVOKE",
					target = "Lnet/optifine/http/HttpPipeline;get(Ljava/lang/String;Ljava/net/Proxy;)[B"
			),
			require = 0,
			remap = false
	)
	private byte[] downloadModelOverride$TKM(String urlStr, Proxy proxy)
	{
		if (this.override != null && this.override.model != null)
		{
			return this.override.model;
		}

		// original logic
		return invokeHttpPipelineGet(urlStr, proxy);
	}

	@Redirect(
			method = "downloadTextureImage",
			at = @At(
					value = "INVOKE",
					target = "Lnet/optifine/http/HttpPipeline;get(Ljava/lang/String;Ljava/net/Proxy;)[B"
			),
			require = 0,
			remap = false
	)
	private byte[] downloadTextureOverride$TKM(String urlStr, Proxy proxy)
	{
		if (this.override != null && this.override.texture != null)
		{
			return this.override.texture;
		}

		// original logic
		return invokeHttpPipelineGet(urlStr, proxy);
	}

	@Unique
	private static byte[] invokeHttpPipelineGet(String urlStr, Proxy proxy)
	{
		// HttpPipeline.get(urlStr, proxy)
		MutableObject<byte[]> ret = new MutableObject<>();
		ReflectionUtil.getClass("net.optifine.http.HttpPipeline").ifPresent(clazz -> {
			byte[] b = ReflectionUtil.<byte[]>invoke(clazz, "get", null, urlStr, proxy).get();
			ret.setValue(b);
		});
		return ret.getValue();
	}
}
