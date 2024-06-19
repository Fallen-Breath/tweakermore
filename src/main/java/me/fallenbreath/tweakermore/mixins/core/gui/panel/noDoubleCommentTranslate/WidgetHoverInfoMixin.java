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

package me.fallenbreath.tweakermore.mixins.core.gui.panel.noDoubleCommentTranslate;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fi.dy.masa.malilib.gui.widgets.WidgetHoverInfo;
import me.fallenbreath.tweakermore.gui.WidgetHoverInfoNoTranslateHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WidgetHoverInfo.class)
public abstract class WidgetHoverInfoMixin
{
	@WrapOperation(
			method = {
					"setInfoLines",
					"addLines"
			},
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/malilib/util/StringUtils;translate(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;",
					remap = false
			),
			remap = false
	)
	private String dontTranslateIfTheFlagIsSet(String translationKey, Object[] args, Operation<String> original)
	{
		if (WidgetHoverInfoNoTranslateHelper.dontTranslate.get())
		{
			if (args.length > 0)
			{
				throw new RuntimeException("dontTranslate is set to true, but args is still provided");
			}
			return translationKey;
		}
		return original.call(translationKey, args);
	}
}
