package me.fallenbreath.tweakermore.mixins.tweaks.disableRedstoneWireParticle;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.block.RedstoneWireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RedstoneWireBlock.class)
public abstract class RedstoneWireBlockMixin
{
	// inject right before world.addParticle to avoid client RNG side effects
	@Inject(
			method = "method_27936",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"
			),
			cancellable = true
	)
	private void disableRedstoneWireParticle(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.DISABLE_REDSTONE_WIRE_PARTICLE.getBooleanValue())
		{
			ci.cancel();
		}
	}
}
