package me.fallenbreath.tweakermore.mixins.tweaks.features.serverMsptMetricsStatistic;

import me.fallenbreath.tweakermore.impl.features.serverMsptMetricsStatistic.MetricsDataWithRichStatistic;
import me.fallenbreath.tweakermore.impl.features.serverMsptMetricsStatistic.RichStatisticManager;
import net.minecraft.util.MetricsData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MetricsData.class)
public abstract class MetricsDataMixin implements MetricsDataWithRichStatistic
{
	private RichStatisticManager richStatisticManager$TKM = null;

	@Override
	public void enableRichStatistic()
	{
		if (this.richStatisticManager$TKM == null)
		{
			this.richStatisticManager$TKM = new RichStatisticManager();
		}
	}

	@Override
	@Nullable
	public RichStatisticManager getRichStatisticManager()
	{
		return this.richStatisticManager$TKM;
	}

	@Inject(method = "pushSample", at = @At("TAIL"))
	private void serverMsptMetricsStatistic_callback(long time, CallbackInfo ci)
	{
		if (this.richStatisticManager$TKM != null)
		{
			this.richStatisticManager$TKM.recordGameTickMetrics(time);
		}
	}
}
