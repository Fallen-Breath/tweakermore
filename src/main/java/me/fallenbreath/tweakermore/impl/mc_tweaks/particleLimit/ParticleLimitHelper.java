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

package me.fallenbreath.tweakermore.impl.mc_tweaks.particleLimit;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.Lists;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.particleLimit.ParticleManagerAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;

import java.util.Map;
import java.util.Queue;

//#if MC >= 1.21.9
//$$ import me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.particleLimit.ParticleRendererAccessor;
//#endif

public class ParticleLimitHelper
{
	//#if MC >= 1.21.9
	//$$ @SuppressWarnings({"rawtypes", "unchecked"})
	//#else
	@SuppressWarnings("UnstableApiUsage")
	//#endif
	public static void onConfigValueChanged(ConfigInteger config)
	{
		int newLimit = config.getIntegerValue();

		ParticleEngine particleManager = Minecraft.getInstance().particleEngine;

		//#if MC >= 1.21.9
		//$$ var particles = ((ParticleManagerAccessor)particleManager).getParticles();
		//$$ for (var particleRenderer : particles.values())
		//$$ {
		//$$ 	EvictingQueue<Particle> newQueue = EvictingQueue.create(newLimit);
		//$$ 	newQueue.addAll(particleRenderer.getAll());
		//$$ 	((ParticleRendererAccessor)particleRenderer).setParticles$TKM(newQueue);
		//$$ }
		//#else
		Map<ParticleRenderType, Queue<Particle>> particles = ((ParticleManagerAccessor)particleManager).getParticles();
		for (ParticleRenderType key : Lists.newArrayList(particles.keySet()))
		{
			EvictingQueue<Particle> newQueue = EvictingQueue.create(newLimit);
			newQueue.addAll(particles.get(key));
			particles.put(key, newQueue);
		}
		//#endif
	}
}
