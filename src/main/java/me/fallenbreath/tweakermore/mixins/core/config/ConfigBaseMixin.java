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

package me.fallenbreath.tweakermore.mixins.core.config;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBase;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ConfigBase.class)
public abstract class ConfigBaseMixin
{
	@Shadow(remap = false) @Final
	private String prettyName;

	@Shadow(remap = false)
	private String comment;

	@Inject(method = "getPrettyName", at = @At("HEAD"), cancellable = true, remap = false)
	private void tweakerMoreUseMyPrettyName(CallbackInfoReturnable<String> cir)
	{
		TweakerMoreConfigs.getOptionFromConfig((IConfigBase)this).
				ifPresent(tweakerMoreOption -> {
					cir.setReturnValue(StringUtils.translate(this.prettyName));
				});
	}

	@Inject(method = "getComment", at = @At("HEAD"), cancellable = true, remap = false)
	private void tweakerMoreUseMyComment(CallbackInfoReturnable<String> cir)
	{
		TweakerMoreConfigs.getOptionFromConfig((IConfigBase)this).
				ifPresent(tweakerMoreOption -> {
					String translatedComment = StringUtils.translate(this.comment);
					translatedComment = tweakerMoreOption.modifyComment(translatedComment);
					cir.setReturnValue(translatedComment);
				});
	}
}
