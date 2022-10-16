package me.fallenbreath.tweakermore.mixins.tweaks.features.serverMsptMetricsStatistic;

import me.fallenbreath.tweakermore.impl.features.serverMsptMetricsStatistic.MetricsDataWithRichStatistic;
import me.fallenbreath.tweakermore.impl.features.serverMsptMetricsStatistic.RichStatisticManager;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.util.MetricsData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 11600
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

@Mixin(DebugHud.class)
public abstract class DebugHudMixin
{
	private MetricsData originMetricsData$TKM = null;

	@ModifyVariable(method = "drawMetricsData", at = @At("HEAD"), argsOnly = true)
	private MetricsData serverMsptMetricsStatistic_modify(
			MetricsData metricsData,
			/* parent method parameters vvv */
			//#if MC >= 11600
			//$$ MatrixStack matrices,
			//#endif
			MetricsData metricsData_, int x, int width, boolean showFps
	)
	{
		if (!showFps)
		{
			RichStatisticManager manager = ((MetricsDataWithRichStatistic)metricsData).getRichStatisticManager();
			if (manager != null)
			{
				this.originMetricsData$TKM = metricsData;
				metricsData = manager.modifyServerMsptMetricsStatistic(metricsData);
			}
		}
		return metricsData;
	}

	@Inject(
			method = "drawMetricsData",
			at = @At(
					value = "INVOKE",
					//#if MC >= 11500
					target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableDepthTest()V",
					//#else
					//$$ target = "Lcom/mojang/blaze3d/platform/GlStateManager;enableDepthTest()V",
					//#endif
					remap = false
			)
	)
	private void serverMsptMetricsStatistic_renderExtra(
			//#if MC >= 11600
			//$$ MatrixStack matrices,
			//#endif
			MetricsData metricsData, int x, int width, boolean showFps, CallbackInfo ci
	)
	{
		if (!showFps && this.originMetricsData$TKM != null)
		{
			RichStatisticManager manager = ((MetricsDataWithRichStatistic)this.originMetricsData$TKM).getRichStatisticManager();
			if (manager != null)
			{
				long[] ls = metricsData.getSamples();
				int m = Math.max(0, ls.length - width);
				int n = ls.length - m;

				manager.renderExtraOnDebugHud(
						//#if MC >= 11600
						//$$ matrices,
						//#endif
						x, n
				);
			}
		}
	}
}
