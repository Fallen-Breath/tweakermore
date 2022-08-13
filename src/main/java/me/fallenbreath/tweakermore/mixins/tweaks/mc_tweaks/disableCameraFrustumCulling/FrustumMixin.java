package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableCameraFrustumCulling;

import me.fallenbreath.tweakermore.impl.mc_tweaks.disableFrustumChunkCulling.CouldBeAlwaysVisibleFrustum;
import net.minecraft.client.render.Frustum;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC >= 11800
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif

@Mixin(Frustum.class)
public abstract class FrustumMixin implements CouldBeAlwaysVisibleFrustum
{
	private boolean alwaysVisible$TKM = false;

	//#if MC >= 11800
	//$$ @Inject(method = "<init>(Lnet/minecraft/client/render/Frustum;)V", at = @At("TAIL"))
	//$$ private void copyAlwaysVisibleFlag(Frustum frustum, CallbackInfo ci)
	//$$ {
	//$$ 	this.alwaysVisible$TKM = ((FrustumMixin)(Object)frustum).alwaysVisible$TKM;
	//$$ }
	//#endif

	@Override
	public void setAlwaysVisible(boolean alwaysVisible)
	{
		this.alwaysVisible$TKM = alwaysVisible;
	}

	@Inject(
			method = "isVisible(Lnet/minecraft/util/math/Box;)Z",
			at = @At("HEAD"),
			cancellable = true
	)
	private void disableCameraFrustumCulling_implementAlwaysVisible(Box box, CallbackInfoReturnable<Boolean> cir)
	{
		if (this.alwaysVisible$TKM)
		{
			cir.setReturnValue(true);
		}
	}
}
