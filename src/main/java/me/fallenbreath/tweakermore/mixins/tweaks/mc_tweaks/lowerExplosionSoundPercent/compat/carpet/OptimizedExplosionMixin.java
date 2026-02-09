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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.lowerExplosionSoundPercent.compat.carpet;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@SuppressWarnings("UnresolvedMixinReference")
@Restriction(require = {
		@Condition(ModIds.fabric_carpet),
		@Condition(value = ModIds.minecraft, versionPredicates = "<1.21.2")
})
@Pseudo
@Mixin(targets = "carpet.helpers.OptimizedExplosion")
public abstract class OptimizedExplosionMixin
{
	@ModifyArg(
			method = "doExplosionB",
			at = @At(
					value = "INVOKE",
					// fabric-carpet in mc1.19.4 keeps using the playSound with different intermediary name
					// so don't let remap remaps with intermediary
					//#disable-remap
					target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V",
					//#enable-remap
					remap = true
			),
			index = 6,
			remap = false
	)
	private static float lowerExplosionSound_adjustVolume_carpetOptimizedTNT(float volume)
	{
		if (TweakerMoreConfigs.LOWER_EXPLOSION_SOUND.getBooleanValue())
		{
			volume = (float)(volume * TweakerMoreConfigs.LOWER_EXPLOSION_SOUND_PERCENT.getDoubleValue() / 100);
		}
		return volume;
	}
}
