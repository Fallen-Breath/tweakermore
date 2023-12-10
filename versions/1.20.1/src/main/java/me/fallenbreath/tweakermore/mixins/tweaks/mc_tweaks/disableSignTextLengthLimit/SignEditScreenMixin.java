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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableSignTextLengthLimit;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

/**
 * The implementation for mc [1.20, ~)
 * See subproject 1.14.4 or 1.15.2 for implementation for other version range
 */
@Restriction(conflict = @Condition(value = ModIds.caxton, versionPredicates = "<0.3.0-beta.2"))
@Mixin(AbstractSignEditScreen.class)
public abstract class SignEditScreenMixin extends Screen
{
	@Shadow @Final private SignBlockEntity blockEntity;

	@Shadow @Final private String[] messages;

	@Shadow private SignText text;
	private boolean filtered$TKM;

	protected SignEditScreenMixin(Text title)
	{
		super(title);
	}

	@Inject(
			method = "<init>(Lnet/minecraft/block/entity/SignBlockEntity;ZZLnet/minecraft/text/Text;)V",
			at = @At("TAIL")
	)
	private void recordFilteredParam(SignBlockEntity blockEntity, boolean front, boolean filtered, Text title, CallbackInfo ci)
	{
		this.filtered$TKM = filtered;
	}

	@ModifyExpressionValue(
			method = "method_45658",  // lambda method in init
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/block/entity/SignBlockEntity;getMaxTextWidth()I",
					remap = true
			),
			remap = false
	)
	private int disableSignTextLengthLimitInSignEditor(int maxLength)
	{
		if (TweakerMoreConfigs.DISABLE_SIGN_TEXT_LENGTH_LIMIT.getBooleanValue())
		{
			maxLength = Integer.MAX_VALUE;
		}
		return maxLength;
	}

	@Inject(
			method = "renderSignText",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I",
					ordinal = 0
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void drawLineOverflowHint(
			DrawContext context, CallbackInfo ci,
			@Local(ordinal = 5) int lineIdx,
			@Local(ordinal = 6) int xStart
	)
	{
		if (TweakerMoreConfigs.DISABLE_SIGN_TEXT_LENGTH_LIMIT.getBooleanValue())
		{
			int textArrayLen = this.messages.length;
			MinecraftClient mc = this.client;
			if (mc != null && 0 <= lineIdx && lineIdx < textArrayLen)
			{
				Text text = this.text.getMessage(lineIdx, this.filtered$TKM);
				int maxWidth = this.blockEntity.getMaxTextWidth();
				List<?> wrapped = mc.textRenderer.wrapLines(text, maxWidth);
				boolean overflowed = wrapped.size() > 1;

				if (overflowed)
				{
					assert Formatting.RED.getColorValue() != null;
					context.drawText(this.textRenderer, "!", xStart - 10, lineIdx * 10 - textArrayLen * 5, Formatting.RED.getColorValue(), false);
				}
			}
		}
	}
}
