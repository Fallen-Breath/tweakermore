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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.barrierParticleAlwaysVisible;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * mc1.14         : subproject 1.14.4
 * mc1.15 ~ mc1.16: subproject 1.15.2 (main project)        <--------
 * mc1.17+        : subproject 1.17.1
 */
@Mixin(ClientLevel.class)
public abstract class ClientWorldMixin
{
	@ModifyVariable(
			method = "animateTick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/player/LocalPlayer;getHandSlots()Ljava/lang/Iterable;"
			)
	)
	private boolean barrierParticleAlwaysVisible_setFlagToTrue(boolean spawnBarrierParticles)
	{
		if (TweakerMoreConfigs.BARRIER_PARTICLE_ALWAYS_VISIBLE.getBooleanValue())
		{
			spawnBarrierParticles = true;
		}
		return spawnBarrierParticles;
	}
}
