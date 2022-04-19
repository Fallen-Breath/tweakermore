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
	@Inject(
			//#if MC >= 11500
			method = "method_24227",
			//#else
			//$$ method = "method_18504",
			//#endif
			at = @At("TAIL"),
			remap = false
	)
	private void onClientInitFinished(CallbackInfo ci)
	{
		DocumentGenerator.onClientInitFinished();
	}
}
