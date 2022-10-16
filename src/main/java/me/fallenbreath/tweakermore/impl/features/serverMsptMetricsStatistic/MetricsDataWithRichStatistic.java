package me.fallenbreath.tweakermore.impl.features.serverMsptMetricsStatistic;

import org.jetbrains.annotations.Nullable;

public interface MetricsDataWithRichStatistic
{
	void enableRichStatistic();

	@Nullable
	RichStatisticManager getRichStatisticManager();
}
