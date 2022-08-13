package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableCameraFrustumCulling;

import me.fallenbreath.tweakermore.impl.mc_tweaks.disableFrustumChunkCulling.CouldBeAlwaysVisibleFrustum;
import net.minecraft.client.render.FrustumWithOrigin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FrustumWithOrigin.class)
public abstract class FrustumMixin implements CouldBeAlwaysVisibleFrustum
{
	private boolean alwaysVisible$TKM = false;

	@Override
	public void setAlwaysVisible(boolean alwaysVisible)
	{
		this.alwaysVisible$TKM = alwaysVisible;
	}

	@Inject(
			method = "intersects(DDDDDD)Z",
			at = @At("HEAD"),
			cancellable = true
	)
	private void disableCameraFrustumCulling_implementAlwaysVisible(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, CallbackInfoReturnable<Boolean> cir)
	{
		if (this.alwaysVisible$TKM)
		{
			cir.setReturnValue(true);
		}
	}
}
