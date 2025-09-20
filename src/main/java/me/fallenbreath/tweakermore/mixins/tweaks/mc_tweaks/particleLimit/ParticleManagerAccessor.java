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

import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

//#if MC >= 1.21.9
//$$ import net.minecraft.client.particle.ParticleRenderer;
//#else
import net.minecraft.client.particle.Particle;
import java.util.Queue;
//#endif

@Mixin(ParticleManager.class)
public interface ParticleManagerAccessor
{
	@Accessor
	//#if MC >= 1.21.9
	//$$ Map<ParticleTextureSheet, ParticleRenderer<?>> getParticles();
	//#else
	Map<ParticleTextureSheet, Queue<Particle>> getParticles();
	//#endif
}
