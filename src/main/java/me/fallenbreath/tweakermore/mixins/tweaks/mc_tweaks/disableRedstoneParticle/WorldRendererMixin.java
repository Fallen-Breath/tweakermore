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
