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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.daytimeOverride;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalLongRef;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 12103
//$$ import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
//#endif

/**
 * Modify daytime here too,
 * so the logic used when the client received a time update packet can be reused by us (gamerule changing etc.)
 */
@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin
{
	@Inject(
			//#if MC >= 12103
			//$$ method = "setTime",
			//#else
			method = "setTimeOfDay",
			//#endif
			at = @At("HEAD")
	)
	private void overwriteDayTime(
			CallbackInfo ci,
			//#if MC >= 12103
			//$$ @Local(argsOnly = true, ordinal = 1) LocalLongRef timeOfDay,
			//$$ @Local(argsOnly = true) LocalBooleanRef shouldTickTimeOfDay
			//#else
			@Local(argsOnly = true) LocalLongRef timeOfDay
			//#endif
	)
	{
		if (TweakerMoreConfigs.DAYTIME_OVERRIDE.getBooleanValue())
		{
			//#if MC >= 12103
			//$$ timeOfDay.set(TweakerMoreConfigs.DAYTIME_OVERRIDE_VALUE.getIntegerValue());
			//$$ shouldTickTimeOfDay.set(false);
			//#else
			timeOfDay.set(-TweakerMoreConfigs.DAYTIME_OVERRIDE_VALUE.getIntegerValue());
			//#endif
		}
	}
}
