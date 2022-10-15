package me.fallenbreath.tweakermore.mixins.util.render;

import me.fallenbreath.tweakermore.util.render.RenderUtil;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin
{
	@Inject(method = "render", at = @At("HEAD"))
	private void recordTickDelta(float tickDelta, long startTime, boolean tick, CallbackInfo ci)
	{
		RenderUtil.tickDelta = tickDelta;
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void recordTickDelta(CallbackInfo ci)
	{
		RenderUtil.tickDelta = 1.0F;
	}
}
