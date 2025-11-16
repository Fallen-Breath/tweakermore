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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.scoreboardSideBarScale;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.render.RenderUtils;
import me.fallenbreath.tweakermore.util.render.context.RenderContext;
import net.minecraft.client.gui.Gui;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 12000
//$$ import net.minecraft.client.gui.GuiGraphics;
//#elseif MC >= 11600
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif

//#if MC >= 11600
//$$ import com.llamalad7.mixinextras.sugar.Local;
//#endif

@Mixin(Gui.class)
public abstract class InGameHudMixin
{
	//#if MC < 12006
	@Shadow private int screenWidth;
	//#endif

	@Unique
	@Nullable
	private RenderUtils.Scaler scaler = null;

	@ModifyVariable(
			//#if MC >= 12103
			//$$ method = "displayScoreboardSidebar(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/scores/Objective;)V",  // lambda method as the context.draw() callback in method renderScoreboardSidebar
			//#elseif MC >= 12004
			//$$ method = "method_55440",  // lambda method as the context.draw() callback in method renderScoreboardSidebar
			//#else
			method = "displayScoreboardSidebar",
			//#endif
			at = @At(
					value = "INVOKE",
					//#if MC >= 11600
					//$$ target = "Lnet/minecraft/client/Options;getBackgroundColor(F)I",
					//#else
					target = "Lnet/minecraft/client/Options;getBackgroundColor(F)I",
					//#endif
					ordinal = 0
			),
			//#if MC >= 12103
			//$$ ordinal = 5  // the lv that stores `drawContext.getScaledWindowHeight() / 2 + n / 3`
			//#else
			ordinal = 3  // the lv that stores `this.scaledHeight / 2 + h / 3`
			//#endif
	)
	private int tweakerMore_scoreboardSideBarScale_push(
			int centerY
			//#if MC >= 12000
			//$$ , @Local(argsOnly = true) GuiGraphics matrixStackOrDrawContext
			//#elseif MC >= 11600
			//$$ , @Local(argsOnly = true) PoseStack matrixStackOrDrawContext
			//#endif
	)
	{
		this.scaler = null;
		if (TweakerMoreConfigs.SCOREBOARD_SIDE_BAR_SCALE.isModified())
		{
			//#if MC >= 12006
			//$$ int scaledWidth = matrixStackOrDrawContext.getScaledWindowWidth();
			//#endif
			this.scaler = RenderUtils.createScaler(this.screenWidth, centerY, TweakerMoreConfigs.SCOREBOARD_SIDE_BAR_SCALE.getDoubleValue());
			this.scaler.apply(RenderContext.gui(
					//#if MC >= 11600
					//$$ matrixStackOrDrawContext
					//#endif
			));
		}
		return centerY;
	}

	@Inject(
			//#if MC >= 12006
			//$$ method = "displayScoreboardSidebar(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/scores/Objective;)V",
			//#else
			method = "displayScoreboardSidebar",
			//#endif
			at = @At("RETURN")
	)
	private void tweakerMore_scoreboardSideBarScale_pop(CallbackInfo ci)
	{
		if (this.scaler != null)
		{
			this.scaler.restore();
		}
	}
}
