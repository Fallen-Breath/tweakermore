package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableCameraFrustumCulling;

//#if MC < 11500
//$$ import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
//$$ import me.fallenbreath.tweakermore.impl.mc_tweaks.disableFrustumChunkCulling.AlwaysVisibleFrustum;
//$$ import net.minecraft.client.render.VisibleRegion;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.ModifyVariable;
//#endif

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Only used in mc1.14.4
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin
{
	//#if MC < 11500
	//$$ @ModifyVariable(
	//$$ 		method = "renderCenter",
	//$$ 		at = @At(
	//$$ 				value = "INVOKE",
	//$$ 				target = "Lnet/minecraft/client/render/VisibleRegion;setOrigin(DDD)V",
	//$$ 				shift = At.Shift.AFTER
	//$$ 		)
	//$$ )
	//$$ private VisibleRegion disableCameraFrustumCulling(VisibleRegion visibleRegion)
	//$$ {
	//$$ 	if (TweakerMoreConfigs.DISABLE_CAMERA_FRUSTUM_CULLING.getBooleanValue())
	//$$ 	{
	//$$ 		visibleRegion = new AlwaysVisibleFrustum(visibleRegion);
	//$$ 	}
	//$$ 	return visibleRegion;
	//$$ }
	//#endif
}