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

import me.fallenbreath.tweakermore.impl.mc_tweaks.fixHoverTextScale.ScaleableHoverTextRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * The implementation for mc [1.20, ~)
 * See subproject 1.15.2 or 1.19.3 for implementation for other version range
 *
 * Targeted class:
 *   mc < 1.20: {@link net.minecraft.client.gui.screen.Screen}
 *   mc >= 1.20: {@link net.minecraft.client.gui.DrawContext}
 */
@Mixin(DrawContext.class)
public abstract class HoverTextRendererClassMixin implements ScaleableHoverTextRenderer
{
	private Double hoverTextScale$TKM = null;

	@Override
	public void setHoverTextScale(@Nullable Double scale)
	{
		if (scale != null)
		{
			this.hoverTextScale$TKM = MathHelper.clamp(scale, 0.01, 1);
		}
		else
		{
			this.hoverTextScale$TKM = null;
		}
	}

	@Inject(method = "drawHoverEvent", at = @At("TAIL"))
	private void fixHoverTextScale_cleanup(CallbackInfo ci)
	{
		this.hoverTextScale$TKM = null;
	}

	@ModifyArg(
			method = "drawHoverEvent",
			at = @At(
					value = "INVOKE",
					target = "Ljava/lang/Math;max(II)I"
			),
			index = 0
	)
	private int fixHoverTextScale_modifyEquivalentMaxScreenWidth(int width)
	{
		if (this.hoverTextScale$TKM != null)
		{
			width /= this.hoverTextScale$TKM;
		}
		return width;
	}

	@ModifyVariable(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V", at = @At("HEAD"), argsOnly = true)
	private TooltipPositioner fixHoverTextScale_modifyPositioner(
			TooltipPositioner positioner,
			/* parent method parameters vvv */
			TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner_
	)
	{
		if (this.hoverTextScale$TKM != null)
		{
			double scale = this.hoverTextScale$TKM;
			positioner = (screenWidth, screenHeight, xBase, yBase, width, height) -> {
				// vanilla: net.minecraft.client.gui.tooltip.HoveredTooltipPositioner
				xBase += 12;
				yBase -= 12;
				if (xBase + width * scale > screenWidth)
				{
					xBase = Math.max(xBase - 24 - width, 4);
				}
				if (yBase + height * scale + 6 > screenHeight)
				{
					yBase += (screenHeight - yBase - 12 - 1) / scale - height + 6;
				}
				return new Vector2i(xBase, yBase);
			};
		}
		return positioner;
	}
}
