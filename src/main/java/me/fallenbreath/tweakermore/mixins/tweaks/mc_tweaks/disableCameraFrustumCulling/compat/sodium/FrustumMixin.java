package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableCameraFrustumCulling.compat.sodium;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.impl.mc_tweaks.disableFrustumChunkCulling.CouldBeAlwaysVisibleFrustum;
import net.minecraft.client.render.Frustum;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Inject after sodium's me.jellysquid.mods.sodium.mixin.core.MixinFrustum is applied, so priority 2000
 */
@Restriction(require = @Condition(type = Condition.Type.MIXIN, value = "me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableCameraFrustumCulling.compat.sodium.JomlFrustumMixin"))
@Mixin(value = Frustum.class, priority = 2000)
public abstract class FrustumMixin
{
	@Dynamic("Added by sodium")
	@SuppressWarnings("UnresolvedMixinReference")
	@Inject(
			method = "sodium$createFrustum",
			at = @At("TAIL"),
			remap = false
	)
	private void disableCameraFrustumCulling_implementAlwaysVisible(CallbackInfoReturnable<? extends CouldBeAlwaysVisibleFrustum> cir)
	{
		CouldBeAlwaysVisibleFrustum self = (CouldBeAlwaysVisibleFrustum)this;
		cir.getReturnValue().setAlwaysVisible(self.getAlwaysVisible());
	}
}
