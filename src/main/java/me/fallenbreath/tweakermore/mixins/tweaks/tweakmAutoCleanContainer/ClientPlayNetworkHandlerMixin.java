package me.fallenbreath.tweakermore.mixins.tweaks.tweakmAutoCleanContainer;

import me.fallenbreath.tweakermore.impl.tweakmAutoCleanContainer.TweakAutoCleanContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin
{
	@Inject(method = "onOpenScreen", at = @At("TAIL"), cancellable = true)
	private void tweakmAutoCleanContainer(CallbackInfo ci)
	{
		TweakAutoCleanContainer.process(MinecraftClient.getInstance().currentScreen);
	}
}
