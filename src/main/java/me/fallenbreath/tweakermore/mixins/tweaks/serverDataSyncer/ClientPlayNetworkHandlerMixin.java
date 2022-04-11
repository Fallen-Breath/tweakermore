package me.fallenbreath.tweakermore.mixins.tweaks.serverDataSyncer;

import me.fallenbreath.tweakermore.impl.serverDataSyncer.ServerDataSyncer;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin
{
	@Inject(method = "onPlayerRespawn", at = @At("TAIL"))
	private void resetserverDataSyncerSyncLimiter(CallbackInfo ci)
	{
		ServerDataSyncer.getInstance().onDimensionChanged();
	}
}
