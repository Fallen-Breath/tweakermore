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

package me.fallenbreath.tweakermore.mixins.core.migration;

import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigBooleanHotkeyed;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.util.ModIds;
import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 11800
//$$ import com.google.gson.JsonElement;
//$$ import me.fallenbreath.tweakermore.TweakerMoreMod;
//$$ import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif

/**
 * ConfigBooleanHotkeyed in malilib 1.18 does not accept single json primitive (boolean)
 * which breaks compatibility of config files generated in previous mc versions
 * here comes a hacky fix, still for tweakermore's config only xd
 */
@Restriction(require = {@Condition(value = ModIds.malilib, versionPredicates = ">=0.11.5")})
@Mixin(ConfigBooleanHotkeyed.class)
public abstract class ConfigBooleanHotkeyedMixin extends ConfigBoolean
{
	public ConfigBooleanHotkeyedMixin(String name, boolean defaultValue, String comment)
	{
		super(name, defaultValue, comment);
	}

	//#if MC >= 11800
	//$$ @Inject(
	//$$ 		method = "setValueFromJsonElement",
	//$$ 		at = @At(
	//$$ 				value = "INVOKE",
	//$$ 				target = "Lcom/google/gson/JsonElement;isJsonObject()Z",
	//$$ 				remap = false
	//$$ 		),
	//$$ 		cancellable = true,
	//$$ 		remap = false
	//$$ )
	//$$ private void youCanReadSingleBooleanToo(JsonElement obj, CallbackInfo ci)
	//$$ {
	//$$ 	if (TweakerMoreConfigs.hasConfig((ConfigBooleanHotkeyed)(Object)this))
	//$$ 	{
	//$$ 		if (obj.isJsonPrimitive())
	//$$ 		{
	//$$ 			try
	//$$ 			{
	//$$ 				super.setValueFromJsonElement(obj);
	//$$ 			}
	//$$ 			catch (Exception e)
	//$$ 			{
	//$$ 				TweakerMoreMod.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), obj, e);
	//$$ 			}
	//$$ 			ci.cancel();
	//$$ 		}
	//$$ 	}
	//$$ }
	//#endif
}
