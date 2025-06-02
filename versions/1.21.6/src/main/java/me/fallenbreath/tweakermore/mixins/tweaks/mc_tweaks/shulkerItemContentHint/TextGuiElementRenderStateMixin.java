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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.shulkerItemContentHint;

import me.fallenbreath.tweakermore.impl.mc_tweaks.shulkerBoxItemContentHint.TextGuiElementRenderStateExtra;
import me.fallenbreath.tweakermore.impl.mc_tweaks.shulkerBoxItemContentHint.TextGuiElementRenderStatePlus;
import net.minecraft.client.gui.render.state.TextGuiElementRenderState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TextGuiElementRenderState.class)
public abstract class TextGuiElementRenderStateMixin implements TextGuiElementRenderStatePlus
{
	@Unique
	private TextGuiElementRenderStateExtra extra$TKM = null;

	@Override
	@Nullable
	public TextGuiElementRenderStateExtra getExtra$TKM()
	{
		return this.extra$TKM;
	}

	@Override
	public void setExtra$TKM(TextGuiElementRenderStateExtra extra$TKM)
	{
		this.extra$TKM = extra$TKM;
	}

	@ModifyArg(
			method = "prepare",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/font/TextRenderer;prepare(Lnet/minecraft/text/OrderedText;FFIZI)Lnet/minecraft/client/font/TextRenderer$GlyphDrawable;"
			),
			index = 1
	)
	private float shulkerItemContentHint_modifyTextX(float x)
	{
		var extra = this.getExtra$TKM();
		if (extra != null)
		{
			x = extra.textX();
		}
		return x;
	}

	@ModifyArg(
			method = "prepare",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/font/TextRenderer;prepare(Lnet/minecraft/text/OrderedText;FFIZI)Lnet/minecraft/client/font/TextRenderer$GlyphDrawable;"
			),
			index = 2
	)
	private float shulkerItemContentHint_modifyTextY(float y)
	{
		var extra = this.getExtra$TKM();
		if (extra != null)
		{
			y = extra.textY();
		}
		return y;
	}
}
