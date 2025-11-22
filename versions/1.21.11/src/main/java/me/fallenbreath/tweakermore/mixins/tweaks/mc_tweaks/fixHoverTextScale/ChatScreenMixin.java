/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2025  Fallen_Breath and contributors
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

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.fixHoverTextScale.ScaleableHoverTextRenderer;
import me.fallenbreath.tweakermore.util.render.RenderUtils;
import me.fallenbreath.tweakermore.util.render.context.RenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * mc1.21.10- : subproject 1.15.2 (main project)
 * mc1.21.11+ : subproject 1.21.11        <--------
 */
@Mixin(GuiGraphics.class)
public abstract class ChatScreenMixin
{
	@Shadow @Final int mouseX;
	@Shadow @Final int mouseY;

	@Unique @Nullable
	private RenderUtils.Scaler hoverScaler$TKM = null;

	@Inject(
			method = "renderDeferredElements",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphics;renderComponentHoverEffect(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Style;II)V"
			)
	)
	private void fixHoverTextScale_doScale(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.FIX_HOVER_TEXT_SCALE.getBooleanValue())
		{
			Minecraft mc = Minecraft.getInstance();
			GuiGraphics self = (GuiGraphics)(Object)this;

			if (mc != null)
			{
				double scale = mc.options.chatScale().get();
				this.hoverScaler$TKM = RenderUtils.createScaler(this.mouseX, this.mouseY, scale);
				this.hoverScaler$TKM.apply(RenderContext.gui( self));

				ScaleableHoverTextRenderer shtr = (ScaleableHoverTextRenderer)self;
				shtr.setHoverTextScale$TKM(scale);
			}
		}
	}

	@Inject(
			method = "renderDeferredElements",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphics;renderComponentHoverEffect(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Style;II)V",
					shift = At.Shift.AFTER
			)
	)
	private void fixHoverTextScale_restoreScale(CallbackInfo ci)
	{
		if (this.hoverScaler$TKM != null)
		{
			this.hoverScaler$TKM.restore();
			this.hoverScaler$TKM = null;

			GuiGraphics self = (GuiGraphics)(Object)this;
			ScaleableHoverTextRenderer shtr = (ScaleableHoverTextRenderer)self;
			shtr.setHoverTextScale$TKM(null);
		}
	}
}
