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

import com.llamalad7.mixinextras.sugar.Local;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 1.21.9
//$$ import net.minecraft.client.world.ClientWorld;
//#else
import net.minecraft.client.render.WorldRenderer;
//#endif

@Mixin(
		//#if MC >= 1.21.9
		//$$ ClientWorld.class
		//#else
		WorldRenderer.class
		//#endif
)
public abstract class WorldRendererMixin
{
	@Inject(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;ZZDDDDDD)V", at = @At("HEAD"), cancellable = true)
	private void disableRedstoneParticle(CallbackInfo ci, @Local(argsOnly = true) ParticleEffect particleEffect)
	{
		if (TweakerMoreConfigs.DISABLE_REDSTONE_PARTICLE.getBooleanValue() && particleEffect instanceof DustParticleEffect)
		{
			ci.cancel();
		}
	}
}
