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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.fixHoverTextScale;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.fallenbreath.tweakermore.impl.mc_tweaks.fixHoverTextScale.RenderTooltipArgs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.fixHoverTextScale.ScaleableHoverTextRenderer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

//#if MC >= 11600
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif

/**
 * The implementation for mc (~, 1.19.2]
 * See subproject 1.19.3 for implementation for other version range
 * <p>
 * Targeted class:
 *   mc < 1.20: {@link net.minecraft.client.gui.screens.Screen}
 *   mc >= 1.20: {@link net.minecraft.client.gui.DrawContext}
 */
@Mixin(Screen.class)
public abstract class HoverTextRendererClassMixin implements ScaleableHoverTextRenderer
{
	@Shadow public int width;
	@Shadow public int height;
	@Unique
	private Double hoverTextScale = null;

	@Override
	public void setHoverTextScale$TKM(@Nullable Double scale)
	{
		if (scale != null)
		{
			this.hoverTextScale = Mth.clamp(scale, 0.01, 1);
		}
		else
		{
			this.hoverTextScale = null;
		}
	}

	@Inject(
			method = "renderComponentHoverEffect",
			at = @At("TAIL")
	)
	private void fixHoverTextScale_cleanup(CallbackInfo ci)
	{
		this.hoverTextScale = null;
	}

	@ModifyArg(
			method = "renderComponentHoverEffect",
			at = @At(
					value = "INVOKE",
					target = "Ljava/lang/Math;max(II)I"
			),
			index = 0
	)
	private int fixHoverTextScale_modifyEquivalentMaxScreenWidth(int width)
	{
		if (this.hoverTextScale != null)
		{
			width /= this.hoverTextScale;
		}
		return width;
	}

	/*
		// vanilla
		if (x + maxWidth > this.width)
		 {
			x -= 28 + maxWidth;
		}
		if (y + totalHeight + 6 > this.height)
		{
			y = this.height - totalHeight - 6;
		}

		// what we want
		if (xBase + maxWidth * scale > this.width)
		{
			xBase -= 28 + maxWidth;
		}
		if (yBase + totalHeight * scale + 6 > this.height)
		{
			yBase += (this.height - yBase - 12 - 1) / scale - totalHeight + 6 + 1
		}
	 */

	@Unique
	private RenderTooltipArgs renderTooltipArgs = null;

	@Inject(
			//#if MC >= 11700
			//$$ method = "renderTooltipInternal(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/util/List;II)V",
			//#elseif MC >= 11600
			//$$ method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/util/List;II)V",
			//#else
			method = "renderTooltip(Ljava/util/List;II)V",
			//#endif
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/gui/screens/Screen;width:I",
					shift = At.Shift.BEFORE
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void fixHoverTextScale_storeLocals(
			//#if MC >= 11600
			//$$ PoseStack matrices,
			//#endif
			List<?> text, int x, int y,
			CallbackInfo ci,
			int maxWidth, int xBase, int yBase, int j, int totalHeight
	)
	{
		this.renderTooltipArgs = new RenderTooltipArgs(xBase, yBase, maxWidth, totalHeight);
	}

	@ModifyExpressionValue(
			//#if MC >= 11700
			//$$ method = "renderTooltipInternal(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/util/List;II)V",
			//#elseif MC >= 11600
			//$$ method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/util/List;II)V",
			//#else
			method = "renderTooltip(Ljava/util/List;II)V",
			//#endif
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/gui/screens/Screen;width:I",
					ordinal = 0
			)
	)
	private int fixHoverTextScale_tweakWidthAdjust(int width)
	{
		if (this.hoverTextScale != null)
		{
			RenderTooltipArgs args = this.renderTooltipArgs;
			boolean shouldAdjust = args.xBase + args.maxWidth * this.hoverTextScale > this.width;

			return shouldAdjust ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		}
		return width;
	}

	@ModifyExpressionValue(
			//#if MC >= 11700
			//$$ method = "renderTooltipInternal(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/util/List;II)V",
			//#elseif MC >= 11600
			//$$ method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/util/List;II)V",
			//#else
			method = "renderTooltip(Ljava/util/List;II)V",
			//#endif
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/gui/screens/Screen;height:I",
					ordinal = 0
			)
	)
	private int fixHoverTextScale_cancelVanillaHeightAdjust(int height)
	{
		if (this.hoverTextScale != null)
		{
			height = Integer.MAX_VALUE;
		}
		return height;
	}

	@ModifyVariable(
			//#if MC >= 11700
			//$$ method = "renderTooltipInternal(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/util/List;II)V",
			//#elseif MC >= 11600
			//$$ method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/util/List;II)V",
			//#else
			method = "renderTooltip(Ljava/util/List;II)V",
			//#endif
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/gui/screens/Screen;height:I",
					ordinal = 0
			),
			ordinal = 4
	)
	private int fixHoverTextScale_applyHeightAdjust(int yBase)
	{
		if (this.hoverTextScale != null)
		{
			int totalHeight = this.renderTooltipArgs.totalHeight;
			double scale = this.hoverTextScale;

			if (yBase + totalHeight * scale + 6 > this.height)
			{
				yBase += (int)((this.height - yBase - 12 - 1) / scale - totalHeight + 6 + 1);
			}
		}
		return yBase;
	}
}
