package me.fallenbreath.tweakermore.impl.features.serverMsptMetricsStatistic;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.listentries.ServerMsptMetricsStatisticType;
import me.fallenbreath.tweakermore.util.render.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.MetricsData;

//#if MC >= 11600
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

public class RichStatisticManager
{
	private final MetricsStatistic statGameTick;
	private final MetricsStatistic statSecondAvg;
	private final MetricsStatistic statSecondMax;
	private final MetricsStatistic statMinuteAvg;
	private final MetricsStatistic statMinuteMax;

	public RichStatisticManager()
	{
		this.statGameTick = new MetricsStatistic(MetricsStatistic.Type.MAX, 1);
		this.statSecondAvg = new MetricsStatistic(MetricsStatistic.Type.AVG, 20);  // 20gt -> 1s
		this.statSecondMax = new MetricsStatistic(MetricsStatistic.Type.MAX, 20);  // 20gt -> 1s
		this.statMinuteAvg = new MetricsStatistic(MetricsStatistic.Type.AVG, 60);  // 60s -> 1min
		this.statMinuteMax = new MetricsStatistic(MetricsStatistic.Type.AVG, 60);  // 60s -> 1min

		this.statGameTick.addNewSampleCallback(this.statSecondAvg::addData);
		this.statGameTick.addNewSampleCallback(this.statSecondMax::addData);
		this.statSecondAvg.addNewSampleCallback(this.statMinuteAvg::addData);
		this.statSecondMax.addNewSampleCallback(this.statMinuteMax::addData);
	}

	public static void cycleStatisticType()
	{
		IConfigOptionListEntry value = TweakerMoreConfigs.SERVER_MSPT_METRICS_STATISTIC_TYPE.getOptionListValue();
		value = value.cycle(true);
		TweakerMoreConfigs.SERVER_MSPT_METRICS_STATISTIC_TYPE.setOptionListValue(value);
		InfoUtils.printActionbarMessage("tweakermore.config.serverMsptMetricsStatisticTypeCycle.message.cycled", value.getDisplayName());
	}

	public void recordGameTickMetrics(long msThisTick)
	{
		this.statGameTick.addData(msThisTick);
	}

	public MetricsData modifyServerMsptMetricsStatistic(MetricsData metricsData)
	{
		switch ((ServerMsptMetricsStatisticType)TweakerMoreConfigs.SERVER_MSPT_METRICS_STATISTIC_TYPE.getOptionListValue())
		{
			case SECOND_AVG:
				return this.statSecondAvg.getMetricsData();
			case SECOND_MAX:
				return this.statSecondMax.getMetricsData();
			case MINUTE_AVG:
				return this.statMinuteAvg.getMetricsData();
			case MINUTE_MAX:
				return this.statMinuteMax.getMetricsData();

			case GAME_TICK:
			default:
				return metricsData;
		}
	}

	public void renderExtraOnDebugHud(
			//#if MC >= 11600
			//$$ MatrixStack matrices,
			//#endif
			int x, int width
	)
	{
		IConfigOptionListEntry value = TweakerMoreConfigs.SERVER_MSPT_METRICS_STATISTIC_TYPE.getOptionListValue();
		if (value == ServerMsptMetricsStatisticType.USE_VANILLA)
		{
			return;
		}

		String text = StringUtils.translate("tweakermore.config.serverMsptMetricsStatisticTypeCycle.message.hint", value.getDisplayName());
		MinecraftClient mc = MinecraftClient.getInstance();
		int height =
				//#if MC >= 11500
				mc.getWindow().getScaledHeight();
				//#else
				//$$ mc.window.getScaledHeight();
				//#endif
		mc.textRenderer.draw(
				//#if MC >= 11600
				//$$ matrices,
				//#endif
				text,
				x + width - 2 - RenderUtil.getRenderWidth(text),
				height - 60 + 2,
				0x00E0E0E0
		);
	}
}