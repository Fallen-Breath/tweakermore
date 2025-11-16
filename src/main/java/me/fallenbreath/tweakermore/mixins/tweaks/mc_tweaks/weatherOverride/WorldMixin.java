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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.weatherOverride;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.listentries.WeatherOverrideValue;
import me.fallenbreath.tweakermore.impl.mc_tweaks.weatherOverride.WeatherOverrideHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(Level.class)
public abstract class WorldMixin
{
	@Inject(method = "getRainLevel", at = @At("HEAD"), cancellable = true)
	private void weatherOverride_overrideRain(CallbackInfoReturnable<Float> cir)
	{
		this.weatherOverride_common(cir, WeatherOverrideValue::getRainGradient);
	}

	@Inject(method = "getThunderLevel", at = @At("HEAD"), cancellable = true)
	private void weatherOverride_overrideThunder(CallbackInfoReturnable<Float> cir)
	{
		this.weatherOverride_common(cir, WeatherOverrideValue::getThunderGradient);
	}

	@Unique
	private void weatherOverride_common(CallbackInfoReturnable<Float> cir, Function<WeatherOverrideValue, Float> overrider)
	{
		if (TweakerMoreConfigs.WEATHER_OVERRIDE.getBooleanValue())
		{
			if (WeatherOverrideHelper.disableOverride.get())
			{
				return;
			}

			Level self = (Level)(Object)this;
			if (self instanceof ClientLevel)
			{
				WeatherOverrideValue value = (WeatherOverrideValue) TweakerMoreConfigs.WEATHER_OVERRIDE_VALUE.getOptionListValue();
				cir.setReturnValue(overrider.apply(value));
			}
		}
	}
}
