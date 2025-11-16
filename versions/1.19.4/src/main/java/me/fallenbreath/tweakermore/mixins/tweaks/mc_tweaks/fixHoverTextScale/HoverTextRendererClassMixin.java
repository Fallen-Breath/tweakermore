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
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 12000
//$$ import net.minecraft.client.gui.GuiGraphics;
//#else
import net.minecraft.client.gui.screens.Screen;
//#endif

/**
 * The implementation for mc [1.19.3, ~)
 * See subproject 1.15.2 for implementation for other version range
 * <p>
 * Targeted class:
 *   mc < 1.20: {@link net.minecraft.client.gui.screen.Screen}
 *   mc >= 1.20: {@link net.minecraft.client.gui.DrawContext}
 */
@Mixin(
		//#if MC >= 12000
		//$$ GuiGraphics.class
		//#else
		Screen.class
		//#endif
)
public abstract class HoverTextRendererClassMixin implements ScaleableHoverTextRenderer
{
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
			//#if MC >= 12000
			//$$ method = "drawHoverEvent",
			//#else
			method = "renderComponentHoverEffect",
			//#endif
			at = @At("TAIL")
	)
	private void fixHoverTextScale_cleanup(CallbackInfo ci)
	{
		this.hoverTextScale = null;
	}

	@ModifyArg(
			//#if MC >= 12000
			//$$ method = "drawHoverEvent",
			//#else
			method = "renderComponentHoverEffect",
			//#endif
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

	@ModifyVariable(
			//#if MC >= 12103
			//$$ method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;Lnet/minecraft/util/Identifier;)V",
			//#elseif MC >= 12000
			//$$ method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V",
			//#else
			method = "renderTooltipFromComponents",
			//#endif
			at = @At("HEAD"),
			argsOnly = true
	)
	private ClientTooltipPositioner fixHoverTextScale_modifyPositioner(ClientTooltipPositioner positioner)
	{
		if (this.hoverTextScale != null)
		{
			double scale = this.hoverTextScale;
			positioner = (
					//#if MC >= 12000
					//$$ screenWidth, screenHeight,
					//#else
					screen,
					//#endif
					x, y, width, height
			) -> {
				//#if MC < 12000
				int screenWidth = screen.width;
				int screenHeight = screen.height;
				//#endif

				// see vanilla: net.minecraft.client.gui.tooltip.HoveredTooltipPositioner#getPosition
				x += 12;
				y -= 12;

				if (x + width * scale > screenWidth)
				{
					x = Math.max(x - 24 - width, 4);
				}
				if (y + height * scale + 6 > screenHeight)
				{
					y += (int)((screenHeight - y - 12 - 1) / scale - height + 6);
				}
				return new Vector2i(x, y);
			};
		}
		return positioner;
	}
}
