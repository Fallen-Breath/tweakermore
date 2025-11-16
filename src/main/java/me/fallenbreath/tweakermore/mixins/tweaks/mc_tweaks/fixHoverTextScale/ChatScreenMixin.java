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
import me.fallenbreath.tweakermore.util.render.RenderUtils;
import me.fallenbreath.tweakermore.util.render.context.RenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 12000
//$$ import net.minecraft.client.gui.GuiGraphics;
//#elseif MC >= 11600
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen
{
	protected ChatScreenMixin(Component title)
	{
		super(title);
	}

	@Unique
	@Nullable
	private RenderUtils.Scaler hoverScaler$TKM = null;

	@Inject(
			method = "render",
			at = @At(
					value = "INVOKE",
					//#if MC >= 12000
					//$$ target = "Lnet/minecraft/client/gui/GuiGraphics;renderComponentHoverEffect(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Style;II)V"
					//#elseif MC >= 11600
					//$$ target = "Lnet/minecraft/client/gui/screens/ChatScreen;renderComponentHoverEffect(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/network/chat/Style;II)V"
					//#else
					target = "Lnet/minecraft/client/gui/screens/ChatScreen;renderComponentHoverEffect(Lnet/minecraft/network/chat/Component;II)V"
					//#endif
			)
	)
	private void fixHoverTextScale_doScale(
			//#if MC >= 12000
			//$$ GuiGraphics matrixStackOrDrawContext,
			//#elseif MC >= 11600
			//$$ PoseStack matrixStackOrDrawContext,
			//#endif
			int mouseX, int mouseY, float delta, CallbackInfo ci
	)
	{
		if (TweakerMoreConfigs.FIX_HOVER_TEXT_SCALE.getBooleanValue())
		{
			Minecraft mc = this.minecraft;

			if (mc != null)
			{
				double scale = mc.gui.getChat().getScale();
				this.hoverScaler$TKM = RenderUtils.createScaler(mouseX, mouseY, scale);
				this.hoverScaler$TKM.apply(RenderContext.gui(
						//#if MC >= 11600
						//$$ matrixStackOrDrawContext
						//#endif
				));

				//#if MC >= 12000
				//$$ ScaleableHoverTextRenderer shtr = (ScaleableHoverTextRenderer)matrixStackOrDrawContext;
				//#else
				ScaleableHoverTextRenderer shtr = (ScaleableHoverTextRenderer)this;
				//#endif
				shtr.setHoverTextScale$TKM(scale);
			}
		}
	}

	@Inject(
			method = "render",
			at = @At(
					value = "INVOKE",
					//#if MC >= 12000
					//$$ target = "Lnet/minecraft/client/gui/GuiGraphics;renderComponentHoverEffect(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Style;II)V",
					//#elseif MC >= 11600
					//$$ target = "Lnet/minecraft/client/gui/screens/ChatScreen;renderComponentHoverEffect(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/network/chat/Style;II)V",
					//#else
					target = "Lnet/minecraft/client/gui/screens/ChatScreen;renderComponentHoverEffect(Lnet/minecraft/network/chat/Component;II)V",
					//#endif
					shift = At.Shift.AFTER
			)
	)
	private void fixHoverTextScale_restoreScale(
			//#if MC >= 12000
			//$$ GuiGraphics matrixStackOrDrawContext,
			//#elseif MC >= 11600
			//$$ PoseStack matrixStackOrDrawContext,
			//#endif
			int mouseX, int mouseY, float delta, CallbackInfo ci
	)
	{
		if (this.hoverScaler$TKM != null)
		{
			this.hoverScaler$TKM.restore();
			this.hoverScaler$TKM = null;

			//#if MC >= 12000
			//$$ ScaleableHoverTextRenderer shtr = (ScaleableHoverTextRenderer)matrixStackOrDrawContext;
			//#else
			ScaleableHoverTextRenderer shtr = (ScaleableHoverTextRenderer)this;
			//#endif
			shtr.setHoverTextScale$TKM(null);
		}
	}
}
