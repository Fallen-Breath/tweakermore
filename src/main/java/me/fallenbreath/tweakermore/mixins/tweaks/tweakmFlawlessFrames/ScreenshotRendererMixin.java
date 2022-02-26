package me.fallenbreath.tweakermore.mixins.tweaks.tweakmFlawlessFrames;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.impl.tweakmFlawlessFrames.FlawlessFramesHandler;
import me.fallenbreath.tweakermore.util.ModIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("UnresolvedMixinReference")
@Restriction(require = @Condition(ModIds.replay_mod))
@Pseudo
@Mixin(targets = "com.replaymod.extras.advancedscreenshots.ScreenshotRenderer")
public abstract class ScreenshotRendererMixin
{
	@Inject(
			method = "renderScreenshot",
			at = @At(
					value = "RETURN",
					ordinal = 0
			),
			remap = false
	)
	private void tweakmFlawlessFrames_onScreenshotRenderFinished(CallbackInfoReturnable<Boolean> cir)
	{
		FlawlessFramesHandler.refreshHook();
	}
}
