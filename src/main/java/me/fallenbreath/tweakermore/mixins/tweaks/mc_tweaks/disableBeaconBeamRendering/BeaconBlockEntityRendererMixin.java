package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableBeaconBeamRendering;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeaconBlockEntityRenderer.class)
public abstract class BeaconBlockEntityRendererMixin
{
	@Inject(
			//#if MC >= 11700
			//$$ method = "renderBeam(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;FJII[F)V",
			//#elseif MC >= 11500
			method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;FJII[F)V",
			//#else
			//$$ method = "renderBeaconLightBeam",
			//#endif
			at = @At("HEAD"),
			cancellable = true
	)
	private static void disableBeaconBeamRendering(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.DISABLE_BEACON_BEAM_RENDERING.getBooleanValue())
		{
			ci.cancel();
		}
	}
}
