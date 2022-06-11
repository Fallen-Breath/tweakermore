package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableCameraFrustumCulling;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.disableFrustumChunkCulling.AlwaysVisibleFrustum;
import net.minecraft.client.render.WorldRenderer;

//#if MC >= 11500
import net.minecraft.client.render.Frustum;
//#endif

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin
{
	//#if MC >= 11500
	@ModifyVariable(
			method = "render",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/render/WorldRenderer;shouldCaptureFrustum:Z",
					ordinal = 0
			)
	)
	private Frustum disableCameraFrustumCulling(Frustum frustum)
	{
		if (TweakerMoreConfigs.DISABLE_CAMERA_FRUSTUM_CULLING.getBooleanValue())
		{
			frustum = new AlwaysVisibleFrustum(frustum);
		}
		return frustum;
	}
	//#endif
}
