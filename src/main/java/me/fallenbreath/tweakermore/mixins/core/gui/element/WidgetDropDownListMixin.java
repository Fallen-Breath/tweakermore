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

package me.fallenbreath.tweakermore.mixins.core.gui.element;

import fi.dy.masa.malilib.gui.widgets.WidgetDropDownList;
import me.fallenbreath.tweakermore.gui.SelectorDropDownList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(WidgetDropDownList.class)
public abstract class WidgetDropDownListMixin
{
	@SuppressWarnings({"ConstantConditions", "PointlessBitwiseExpression"})
	@ModifyArgs(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/malilib/render/RenderUtils;drawRect(IIIII)V",
					remap = false
			),
			remap = false
	)
	private void selectorDropDownListMakeOpaque(Args args)
	{
		if ((WidgetDropDownList<?>)(Object)this instanceof SelectorDropDownList<?>)
		{
			// ensure background is opaque
			int bgColor = args.get(4);
			int a = (bgColor >> 24) & 0xFF;
			bgColor = (0xFF << 24) | (a << 16) | (a << 8) | (a << 0);
			args.set(4, bgColor);

			// show left box border
			args.set(0, (int)args.get(0) + 1);
		}
	}

	@SuppressWarnings("ConstantConditions")
	@Inject(
			method = "onMouseScrolledImpl",
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/malilib/gui/GuiScrollBar;offsetValue(I)V",
					shift = At.Shift.AFTER,
					remap = false
			),
			cancellable = true,
			remap = false
	)
	private void fixNoReturnValueHandlingForScroll(CallbackInfoReturnable<Boolean> cir)
	{
		if ((WidgetDropDownList<?>)(Object)this instanceof SelectorDropDownList<?>)
		{
			cir.setReturnValue(true);
		}
	}
}
