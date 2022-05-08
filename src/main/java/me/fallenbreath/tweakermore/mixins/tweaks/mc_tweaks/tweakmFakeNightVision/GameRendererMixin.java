package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.tweakmFakeNightVision;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin
{
	@Inject(
			method = "getNightVisionStrength",
			at = @At("HEAD"),
			cancellable = true
	)
	private
	//#if MC >= 11500
	static
	//#endif
	void tweakmFakeNightVision(CallbackInfoReturnable<Float> cir)
	{
		// only mc.player and camera.getFocusedEntity() will invoke this method, so we don't need to check the entity arg
		if (TweakerMoreConfigs.TWEAKM_FAKE_NIGHT_VISION.getBooleanValue())
		{
			cir.setReturnValue(1.0F);
		}
	}
}
