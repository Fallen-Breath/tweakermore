package me.fallenbreath.tweakermore.mixins.tweaks.connectionSimulatedDelay;

import io.netty.channel.Channel;
import me.fallenbreath.tweakermore.impl.connectionSimulatedDelay.ChannelDelayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = {
		"net/minecraft/network/ClientConnection$1", // anonymous class in connect
		"net/minecraft/network/ClientConnection$2"  // anonymous class in connectLocal
})
public abstract class ClientConnection_ChannelInitializerMixin
{
	@Inject(method = "initChannel(Lio/netty/channel/Channel;)V", at = @At("TAIL"))
	private void connectionSimulatedDelay(Channel channel, CallbackInfo ci)
	{
		channel.pipeline().addFirst(new ChannelDelayer());
	}
}
