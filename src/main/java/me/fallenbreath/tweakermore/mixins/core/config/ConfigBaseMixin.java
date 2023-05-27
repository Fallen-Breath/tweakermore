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
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC >= 11800
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$ import org.spongepowered.asm.mixin.injection.ModifyArg;
//#endif

import java.util.List;

@Mixin(ConfigBase.class)
public abstract class ConfigBaseMixin
{
	//#if MC >= 11800
	//$$ @Shadow(remap = false) private String comment;
 //$$
	//$$ @ModifyArg(
	//$$ 		method = "getComment",
	//$$ 		at = @At(
	//$$ 				value = "INVOKE",
	//$$ 				target = "Lfi/dy/masa/malilib/util/StringUtils;getTranslatedOrFallback(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
	//$$ 		),
	//$$ 		index = 0,
	//$$ 		remap = false
	//$$ )
	//$$ private String tweakerMoreConfigCommentIsTheTranslationKey(String key)
	//$$ {
	//$$ 	if (TweakerMoreConfigs.hasConfig((IConfigBase)this))
	//$$ 	{
	//$$ 		key = this.comment;
	//$$ 	}
	//$$ 	return key;
	//$$ }
	//#endif

	@Inject(method = "getComment", at = @At("TAIL"), cancellable = true, remap = false)
	private void appendModRequirementHeader(CallbackInfoReturnable<String> cir)
	{
		TweakerMoreConfigs.getOptionFromConfig((IConfigBase)this).ifPresent(tweakerMoreOption -> {
			cir.setReturnValue(tweakerMoreOption.modifyComment(cir.getReturnValue()));
		});
	}
}
