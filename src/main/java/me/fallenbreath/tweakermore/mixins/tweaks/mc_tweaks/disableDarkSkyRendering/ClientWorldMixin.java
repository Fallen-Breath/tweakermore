package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableDarkSkyRendering;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC >= 11500
import net.minecraft.client.world.ClientWorld;
//#else
//$$ import net.minecraft.world.World;
//#endif

@Mixin(
		//#if MC >= 11600
		//$$ ClientWorld.Properties.class
		//#elseif MC >= 11500
		ClientWorld.class
		//#else
		//$$ World.class
		//#endif
)
public abstract class ClientWorldMixin
{
	@Inject(
			//#if MC >= 11500
			method = "getSkyDarknessHeight",
			//#else
			//$$ method = "getHorizonHeight",
			//#endif
			at = @At("HEAD"),
			cancellable = true
	)
	private void disableDarkSkyRendering(CallbackInfoReturnable<Double> cir)
	{
		if (TweakerMoreConfigs.DISABLE_DARK_SKY_RENDERING.getBooleanValue())
		{
			cir.setReturnValue(-Double.MAX_VALUE);
		}
	}
}
