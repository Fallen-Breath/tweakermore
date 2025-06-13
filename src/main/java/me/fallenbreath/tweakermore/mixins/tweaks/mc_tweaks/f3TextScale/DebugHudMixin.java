/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.f3TextScale;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.render.RenderUtils;
import me.fallenbreath.tweakermore.util.render.context.RenderContext;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 12000
//$$ import net.minecraft.client.gui.DrawContext;
//#elseif MC >= 11600
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

//#if MC >= 11600
//$$ import com.llamalad7.mixinextras.sugar.Local;
//#endif

@Mixin(DebugHud.class)
public abstract class DebugHudMixin
{
	@Inject(
			//#if MC >= 12000
			//$$ method = "drawText",
			//#else
			method = {"renderLeftText", "renderRightText"},
			//#endif
			at = @At("HEAD")
	)
	private void f3TextScale_renderLeftRightText_scalingApply(
			CallbackInfo ci,
			//#if MC >= 12000
			//$$ @Local(argsOnly = true) DrawContext matrixStackOrDrawContext,
			//#elseif MC >= 11600
			//$$ @Local(argsOnly = true) MatrixStack matrixStackOrDrawContext,
			//#endif
			@Share("scaler") LocalRef<RenderUtils.Scaler> scaler
	)
	{
		if (TweakerMoreConfigs.F3_TEXT_SCALE.isModified())
		{
			scaler.set(RenderUtils.createScaler(0, 0, TweakerMoreConfigs.F3_TEXT_SCALE.getDoubleValue()));
			scaler.get().apply(RenderContext.gui(
					//#if MC >= 11600
					//$$ matrixStackOrDrawContext
					//#endif
			));
		}
	}

	@ModifyExpressionValue(
			//#if MC >= 12000
			//$$ method = "drawText",
			//#else
			method = "renderRightText",
			//#endif
			at = @At(
					value = "INVOKE",
					//#if MC >= 12000
					//$$ target = "Lnet/minecraft/client/gui/DrawContext;getScaledWindowWidth()I"
					//#else
					target = "Lnet/minecraft/client/util/Window;getScaledWidth()I"
					//#endif
			)
	)
	private int f3TextScale_renderRightText_fixWindowScaledWidth(int width, @Share("scaler") LocalRef<RenderUtils.Scaler> scaler)
	{
		if (scaler.get() != null)
		{
			width = (int)(width / scaler.get().getScaleFactor());
		}
		return width;
	}

	@Inject(
			//#if MC >= 12000
			//$$ method = "drawText",
			//#else
			method = {"renderLeftText", "renderRightText"},
			//#endif
			at = @At("TAIL")
	)
	private void f3TextScale_renderLeftRightText_scalingRestore(CallbackInfo ci, @Share("scaler") LocalRef<RenderUtils.Scaler> scaler)
	{
		if (scaler.get() != null)
		{
			scaler.get().restore();
		}
	}
}
