package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableDarkSkyRendering;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
		//#if MC >= 11600
		//$$ ClientWorld.Properties.class
		//#else
		ClientWorld.class
		//#endif
)
public abstract class ClientWorldMixin
{
	@Inject(method = "getSkyDarknessHeight", at = @At("HEAD"), cancellable = true)
	private void disableDarkSkyRendering(CallbackInfoReturnable<Double> cir)
	{
		if (TweakerMoreConfigs.DISABLE_DARK_SKY_RENDERING.getBooleanValue())
		{
			cir.setReturnValue(-Double.MAX_VALUE);
		}
	}
}
