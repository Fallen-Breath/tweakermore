package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableCameraFrustumCulling.compat.sodium;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.impl.mc_tweaks.disableFrustumChunkCulling.CouldBeAlwaysVisibleFrustum;
import me.fallenbreath.tweakermore.util.ModIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Restriction(require = {
		@Condition(value = ModIds.sodium, versionPredicates = ">0.3.3 <=0.4.3")
})
@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.util.frustum.JomlFrustum")
public abstract class JomlFrustumMixin implements CouldBeAlwaysVisibleFrustum
{
	private boolean alwaysVisible$TKM = false;

	@Override
	public void setAlwaysVisible(boolean alwaysVisible)
	{
		this.alwaysVisible$TKM = alwaysVisible;
	}

	@Override
	public boolean getAlwaysVisible()
	{
		return this.alwaysVisible$TKM;
	}

	@SuppressWarnings({"rawtypes", "unchecked", "UnresolvedMixinReference"})
	@Inject(
			method = "testBox",
			at = @At("HEAD"),
			remap = false,
			cancellable = true
	)
	private void disableCameraFrustumCulling_implementAlwaysVisible(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, CallbackInfoReturnable cir)
	{
		if (this.alwaysVisible$TKM)
		{
			try
			{
				Class visibilityClass = Class.forName("me.jellysquid.mods.sodium.client.util.frustum.Frustum$Visibility");
				Object inside = Enum.valueOf(visibilityClass, "INSIDE");
				cir.setReturnValue(inside);
			}
			catch (Exception ignored)
			{
			}
		}
	}
}
