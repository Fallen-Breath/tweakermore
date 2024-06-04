/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * TweakerMore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TweakerMore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TweakerMore.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.fallenbreath.tweakermore.mixins.tweaks.features.serverMsptMetricsStatistic;

import me.fallenbreath.tweakermore.impl.features.serverMsptMetricsStatistic.MetricsDataWithRichStatistic;
import me.fallenbreath.tweakermore.impl.features.serverMsptMetricsStatistic.RichStatisticManager;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.util.MetricsData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 12000
//$$ import net.minecraft.client.gui.DrawContext;
//#elseif MC >= 11600
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

/**
 * <= mc1.20.1: subproject 1.15.2 (main project)        <--------
 * >= mc1.20.2: subproject 1.20.2
 */
@Mixin(DebugHud.class)
public abstract class DebugHudMixin
{
	@Unique private MetricsData originMetricsData$TKM = null;

	@ModifyVariable(method = "drawMetricsData", at = @At("HEAD"), argsOnly = true)
	private MetricsData serverMsptMetricsStatistic_modify(
			MetricsData metricsData,
			/* parent method parameters vvv */
			//#if MC >= 12000
			//$$ DrawContext matrixStackOrDrawContext,
			//#elseif MC >= 11600
			//$$ MatrixStack matrixStackOrDrawContext,
			//#endif
			MetricsData metricsData_, int x, int width, boolean showFps
	)
	{
		if (!showFps)
		{
			RichStatisticManager manager = ((MetricsDataWithRichStatistic)metricsData).getRichStatisticManager$TKM();
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
			//#if MC >= 12000
			//$$ at = @At("TAIL")
			//#else
			at = @At(
					value = "INVOKE",
					//#if MC >= 11500
					target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableDepthTest()V",
					//#else
					//$$ target = "Lcom/mojang/blaze3d/platform/GlStateManager;enableDepthTest()V",
					//#endif
					remap = false
			)
			//#endif
	)
	private void serverMsptMetricsStatistic_renderExtra(
			//#if MC >= 12000
			//$$ DrawContext matrixStackOrDrawContext,
			//#elseif MC >= 11600
			//$$ MatrixStack matrixStackOrDrawContext,
			//#endif
			MetricsData metricsData, int x, int width, boolean showFps, CallbackInfo ci
	)
	{
		if (!showFps && this.originMetricsData$TKM != null)
		{
			RichStatisticManager manager = ((MetricsDataWithRichStatistic)this.originMetricsData$TKM).getRichStatisticManager$TKM();
			if (manager != null)
			{
				long[] ls = metricsData.getSamples();
				int m = Math.max(0, ls.length - width);
				int n = ls.length - m;

				manager.renderExtraOnDebugHud(
						//#if MC >= 11600
						//$$ matrixStackOrDrawContext,
						//#endif
						x, n
				);
			}
		}
	}
}
