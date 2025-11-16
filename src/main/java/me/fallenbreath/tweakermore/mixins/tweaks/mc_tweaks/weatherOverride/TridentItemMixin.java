/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024  Fallen_Breath and contributors
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
import me.fallenbreath.tweakermore.impl.mc_tweaks.weatherOverride.WeatherOverrideHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TridentItem.class)
public abstract class TridentItemMixin
{
	@ModifyVariable(
			method = "releaseUsing",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/player/Player;isInWaterOrRain()Z"
			),
			argsOnly = true
	)
	private Level weatherOverride_dontOverrideTheRiptideCondition_1_begin(Level world)
	{
		if (TweakerMoreConfigs.WEATHER_OVERRIDE.getBooleanValue() && world instanceof ClientLevel)
		{
			WeatherOverrideHelper.disableOverride.set(true);
		}
		return world;
	}

	@ModifyVariable(
			method = "releaseUsing",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/player/Player;isInWaterOrRain()Z",
					shift = At.Shift.AFTER
			),
			argsOnly = true
	)
	private Level weatherOverride_dontOverrideTheRiptideCondition_1_end(Level world)
	{
		if (TweakerMoreConfigs.WEATHER_OVERRIDE.getBooleanValue() && world instanceof ClientLevel)
		{
			WeatherOverrideHelper.disableOverride.remove();
		}
		return world;
	}

	@ModifyVariable(
			method = "use",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/player/Player;isInWaterOrRain()Z"
			),
			argsOnly = true
	)
	private Level weatherOverride_dontOverrideTheRiptideCondition_2_begin(Level world)
	{
		if (TweakerMoreConfigs.WEATHER_OVERRIDE.getBooleanValue() && world instanceof ClientLevel)
		{
			WeatherOverrideHelper.disableOverride.set(true);
		}
		return world;
	}

	@ModifyVariable(
			method = "use",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/player/Player;isInWaterOrRain()Z",
					shift = At.Shift.AFTER
			),
			argsOnly = true
	)
	private Level weatherOverride_dontOverrideTheRiptideCondition_2_end(Level world)
	{
		if (TweakerMoreConfigs.WEATHER_OVERRIDE.getBooleanValue() && world instanceof ClientLevel)
		{
			WeatherOverrideHelper.disableOverride.remove();
		}
		return world;
	}
}
