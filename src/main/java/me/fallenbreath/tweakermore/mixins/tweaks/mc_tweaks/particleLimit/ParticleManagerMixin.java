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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.particleLimit;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.particle.ParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin
{
	@ModifyArg(
			method = "method_18125",  // lambda method in tick
			at = @At(
					value = "INVOKE",
					target = "Lcom/google/common/collect/EvictingQueue;create(I)Lcom/google/common/collect/EvictingQueue;",
					remap = false
			)
	)
	private static int particleLimit_modifyQueueSize(int limit)
	{
		if (TweakerMoreConfigs.PARTICLE_LIMIT.isModified())
		{
			limit = TweakerMoreConfigs.PARTICLE_LIMIT.getIntegerValue();
		}
		return limit;
	}
}
