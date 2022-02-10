package me.fallenbreath.tweakermore.mixins.tweaks.tweakmSafeAfk;

import me.fallenbreath.tweakermore.impl.tweakmSafeAfk.SafeAfkHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin
{
	@Shadow private MinecraftClient client;

	@Inject(method = "onHealthUpdate", at = @At("TAIL"))
	private void tweakerMoreSafeAfkHook(CallbackInfo ci)
	{
		SafeAfkHelper.onHealthUpdate(this.client);
	}

	@Inject(method = {"clearWorld", "onPlayerRespawn"}, at = @At("TAIL"))
	private void resetLastHurtGameTime(CallbackInfo ci)
	{
		SafeAfkHelper.resetHurtTime();
	}
}
