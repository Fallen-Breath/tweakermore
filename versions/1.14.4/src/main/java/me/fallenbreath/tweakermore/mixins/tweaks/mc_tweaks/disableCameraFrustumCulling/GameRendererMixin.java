package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableCameraFrustumCulling;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.disableFrustumChunkCulling.CouldBeAlwaysVisibleFrustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VisibleRegion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Only used in mc1.14.4
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin
{
	@ModifyVariable(
			method = "renderCenter",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/VisibleRegion;setOrigin(DDD)V",
					shift = At.Shift.AFTER
			)
	)
	private VisibleRegion disableCameraFrustumCulling(VisibleRegion visibleRegion)
	{
		if (TweakerMoreConfigs.DISABLE_CAMERA_FRUSTUM_CULLING.getBooleanValue())
		{
			if (visibleRegion instanceof CouldBeAlwaysVisibleFrustum)
			{
				boolean alwaysVisible = TweakerMoreConfigs.DISABLE_CAMERA_FRUSTUM_CULLING.getBooleanValue();
				((CouldBeAlwaysVisibleFrustum)visibleRegion).setAlwaysVisible(alwaysVisible);
			}
		}
		return visibleRegion;
	}
}