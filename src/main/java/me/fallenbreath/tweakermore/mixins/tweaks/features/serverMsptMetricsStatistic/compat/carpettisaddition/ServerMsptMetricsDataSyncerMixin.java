package me.fallenbreath.tweakermore.mixins.tweaks.features.serverMsptMetricsStatistic.compat.carpettisaddition;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.impl.features.serverMsptMetricsStatistic.MetricsDataWithRichStatistic;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.util.MetricsData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnresolvedMixinReference")
@Restriction(require = @Condition(value = ModIds.carpet_tis_addition, versionPredicates = ">=1.41.0"))
@Pseudo
@Mixin(targets = "carpettisaddition.helpers.rule.syncServerMsptMetricsData.ServerMsptMetricsDataSyncer")
public abstract class ServerMsptMetricsDataSyncerMixin
{
	@Shadow(remap = false) private MetricsData metricsData;

	@Inject(method = "<init>", at = @At("TAIL"), remap = false)
	private void serverMsptMetricsStatistic_hookMetricsData(CallbackInfo ci)
	{
		((MetricsDataWithRichStatistic)this.metricsData).enableRichStatistic();
	}
}
