package me.fallenbreath.tweakermore.mixins.tweaks.tweakmAutoContainerProcess;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.impl.tweakmAutoContainerProcess.AutoProcessableScreen;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(require = @Condition(ModIds.itemscroller))
@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin
{
	@Inject(method = "onOpenContainer", at = @At("TAIL"))
	private void tweakmAutoFillContainerMarking(CallbackInfo ci)
	{
		Screen screen = MinecraftClient.getInstance().currentScreen;
		if (screen != null)
		{
			((AutoProcessableScreen)screen).setShouldProcess(true);
		}
	}
}
