package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableVignetteDarkness;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin
{
	@Shadow public float
			//#if MC >= 11500
			vignetteDarkness;
			//#else
			//$$ field_2013;
			//#endif

	private Float prevVignetteDarkness$TKM = null;

	@Inject(method = "renderVignetteOverlay", at = @At(value = "HEAD"))
	private void disableVignetteDarkness_modifyVignetteDarkness( CallbackInfo ci)
	{
		if (TweakerMoreConfigs.DISABLE_VIGNETTE_DARKNESS.getBooleanValue())
		{
			//#if MC >= 11500
			this.prevVignetteDarkness$TKM = this.vignetteDarkness;
			this.vignetteDarkness = 0.0F;
			//#else
			//$$ this.prevVignetteDarkness$TKM = this.field_2013;
			//$$ this.field_2013 = 0.0F;
			//#endif
		}
	}

	@Inject(method = "renderVignetteOverlay", at = @At(value = "TAIL"))
	private void disableVignetteDarkness_restoreVignetteDarkness( CallbackInfo ci)
	{
		if (this.prevVignetteDarkness$TKM != null)
		{
			//#if MC >= 11500
			this.vignetteDarkness = this.prevVignetteDarkness$TKM;
			//#else
			//$$ this.field_2013 = this.prevVignetteDarkness$TKM;
			//#endif
		}
	}
}
