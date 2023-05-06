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

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.fixHoverTextScale.ScaleableHoverTextRenderer;
import me.fallenbreath.tweakermore.util.render.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 11600
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen
{
	protected ChatScreenMixin(Text title)
	{
		super(title);
	}

	@Nullable
	private RenderUtil.Scaler hoverScaler$TKM = null;

	@Inject(
			method = "render",
			at = @At(
					value = "INVOKE",
					//#if MC >= 11600
					//$$ target = "Lnet/minecraft/client/gui/screen/ChatScreen;renderTextHoverEffect(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Style;II)V"
					//#else
					target = "Lnet/minecraft/client/gui/screen/ChatScreen;renderComponentHoverEffect(Lnet/minecraft/text/Text;II)V"
					//#endif
			)
	)
	private void fixHoverTextScale_doScale(
			//#if MC >= 11600
			//$$ MatrixStack matrices,
			//#endif
			int mouseX, int mouseY, float delta, CallbackInfo ci
	)
	{
		if (TweakerMoreConfigs.FIX_HOVER_TEXT_SCALE.getBooleanValue())
		{
			MinecraftClient mc = this.minecraft;

			if (mc != null)
			{
				double scale = mc.inGameHud.getChatHud().getChatScale();
				this.hoverScaler$TKM = RenderUtil.createScaler(mouseX, mouseY, scale);
				this.hoverScaler$TKM.apply(
						//#if MC >= 11600
						//$$ matrices
						//#endif
				);

				((ScaleableHoverTextRenderer)this).setHoverTextScale(scale);
			}
		}
	}

	@Inject(
			method = "render",
			at = @At(
					value = "INVOKE",
					//#if MC >= 11600
					//$$ target = "Lnet/minecraft/client/gui/screen/ChatScreen;renderTextHoverEffect(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Style;II)V",
					//#else
					target = "Lnet/minecraft/client/gui/screen/ChatScreen;renderComponentHoverEffect(Lnet/minecraft/text/Text;II)V",
					//#endif
					shift = At.Shift.AFTER
			)
	)
	private void fixHoverTextScale_restoreScale(CallbackInfo ci)
	{
		if (this.hoverScaler$TKM != null)
		{
			this.hoverScaler$TKM.restore();
			this.hoverScaler$TKM = null;

			((ScaleableHoverTextRenderer)this).setHoverTextScale(null);
		}
	}
}
