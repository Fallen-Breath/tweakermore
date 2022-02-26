package me.fallenbreath.tweakermore.mixins.doc;

import me.fallenbreath.tweakermore.util.doc.DocumentGenerator;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin
{
	@Inject(method = "method_24227", at = @At("TAIL"), remap = false)
	private void onClientInitFinished(CallbackInfo ci)
	{
		DocumentGenerator.onClientInitFinished();
	}
}
