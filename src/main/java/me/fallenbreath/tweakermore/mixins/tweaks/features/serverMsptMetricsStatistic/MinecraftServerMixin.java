package me.fallenbreath.tweakermore.mixins.tweaks.features.serverMsptMetricsStatistic;

import me.fallenbreath.tweakermore.impl.features.serverMsptMetricsStatistic.MetricsDataWithRichStatistic;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MetricsData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin
{
	@Shadow @Final private MetricsData metricsData;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void serverMsptMetricsStatistic_hookMetricsData(CallbackInfo ci)
	{
		((MetricsDataWithRichStatistic)this.metricsData).enableRichStatistic();
	}
}
