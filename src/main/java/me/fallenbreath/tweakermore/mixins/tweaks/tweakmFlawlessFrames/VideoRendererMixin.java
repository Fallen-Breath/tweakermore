package me.fallenbreath.tweakermore.mixins.tweaks.tweakmFlawlessFrames;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.impl.tweakmFlawlessFrames.FlawlessFramesHandler;
import me.fallenbreath.tweakermore.util.ModIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnresolvedMixinReference")
@Restriction(require = @Condition(ModIds.replay_mod))
@Pseudo
@Mixin(targets = "com.replaymod.render.rendering.VideoRenderer")
public abstract class VideoRendererMixin
{
	@Inject(method = "finish", at = @At("TAIL"), remap = false)
	private void tweakmFlawlessFrames_onVideoRenderFinished(CallbackInfo ci)
	{
		FlawlessFramesHandler.refreshHook();
	}
}
