package me.fallenbreath.tweakermore.mixins.tweaks.tweakmAutoFillContainer;

import me.fallenbreath.tweakermore.impl.tweakmAutoFillContainer.IScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin
{
	@Inject(method = "onOpenScreen", at = @At("TAIL"))
	private void tweakmAutoFillContainerMarking(CallbackInfo ci)
	{
		Screen screen = MinecraftClient.getInstance().currentScreen;
		if (screen != null)
		{
			((IScreen)screen).setShouldProcess(true);
		}
	}
}
