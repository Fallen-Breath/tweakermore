package me.fallenbreath.tweakermore.mixins.tweaks.disableCameraFrustumCulling;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.disableFrustumChunkCulling.AlwaysVisibleFrustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VisibleRegion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

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
	private VisibleRegion disableFrustumChunkCulling(VisibleRegion visibleRegion)
	{
		if (TweakerMoreConfigs.DISABLE_CAMERA_FRUSTUM_CULLING.getBooleanValue())
		{
			visibleRegion = new AlwaysVisibleFrustum(visibleRegion);
		}
		return visibleRegion;
	}
}
