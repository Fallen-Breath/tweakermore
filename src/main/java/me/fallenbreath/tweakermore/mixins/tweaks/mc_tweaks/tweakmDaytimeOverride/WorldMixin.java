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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.tweakmDaytimeOverride;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class WorldMixin
{
	@Inject(method = "getTimeOfDay", at = @At("HEAD"), cancellable = true)
	private void overwriteDayTime(CallbackInfoReturnable<Long> cir)
	{
		World self = (World)(Object)this;
		if (self instanceof ClientWorld)
		{
			if (TweakerMoreConfigs.TWEAKM_DAYTIME_OVERRIDE.getBooleanValue())
			{
				cir.setReturnValue((long)TweakerMoreConfigs.DAYTIME_OVERRIDE_VALUE.getIntegerValue());
			}
		}
	}
}
