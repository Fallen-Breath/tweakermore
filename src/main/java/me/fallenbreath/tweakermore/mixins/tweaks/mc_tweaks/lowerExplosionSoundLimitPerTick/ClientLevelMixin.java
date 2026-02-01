/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.lowerExplosionSoundLimitPerTick;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.lowerExplosionSoundLimitPerTick.LowerExplosionSoundLimitPerTickHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin
{
	@Inject(
			method = "playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V",
			at = @At("HEAD"),
			cancellable = true
	)
	private void lowerExplosionSoundLimitPerTick_impl(double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay, CallbackInfo ci)
	{
		if (!TweakerMoreConfigs.LOWER_EXPLOSION_SOUND.getBooleanValue())
		{
			return;
		}

		if (
				//#if MC >= 1.20.5
				//$$ sound == SoundEvents.GENERIC_EXPLODE.value()
				//#else
				sound == SoundEvents.GENERIC_EXPLODE
				//#endif
		)
		{
			int limitPerTick = TweakerMoreConfigs.LOWER_EXPLOSION_SOUND_LIMIT_PER_TICK.getIntegerValue();
			if (limitPerTick < 0)
			{
				return;  // do nothing if limit < 0
			}
			if (limitPerTick == 0 || LowerExplosionSoundLimitPerTickHelper.increaseAndGetExplosionSoundCount(x, y,z) > limitPerTick)
			{
				ci.cancel();
			}
		}
	}
}
