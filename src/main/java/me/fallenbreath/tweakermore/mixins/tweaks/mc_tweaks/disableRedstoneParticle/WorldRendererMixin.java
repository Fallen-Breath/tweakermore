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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableRedstoneParticle;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin
{
	@Inject(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;ZZDDDDDD)V", at = @At("HEAD"), cancellable = true)
	private void disableRedstoneParticle(ParticleEffect particleEffect, boolean shouldAlwaysSpawn, boolean isImportant, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfo ci)
	{
		if (TweakerMoreConfigs.DISABLE_REDSTONE_PARTICLE.getBooleanValue() && particleEffect instanceof DustParticleEffect)
		{
			ci.cancel();
		}
	}
}
