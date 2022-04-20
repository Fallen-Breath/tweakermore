package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableCameraFrustumCulling;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.disableFrustumChunkCulling.AlwaysVisibleFrustum;

//#if MC < 11500
//$$ import net.minecraft.client.render.VisibleRegion;
//#endif

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

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