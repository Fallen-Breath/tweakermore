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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.lowerExplosionSoundPercent;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Explosion.class)
public abstract class ExplosionMixin
{
	@ModifyArg(
			method = "finalizeExplosion",
			at = @At(
					value = "INVOKE",
					//#if MC >= 1.15
					target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"
					//#else
					//$$ target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
					//#endif
			),
			//#if MC >= 1.15
			index = 5
			//#else
			//$$ index = 6
			//#endif
	)
	private float lowerExplosionSound_adjustVolume(float volume)
	{
		if (TweakerMoreConfigs.LOWER_EXPLOSION_SOUND.getBooleanValue())
		{
			volume = (float)(volume * TweakerMoreConfigs.LOWER_EXPLOSION_SOUND_PERCENT.getDoubleValue() / 100);
		}
		return volume;
	}
}
