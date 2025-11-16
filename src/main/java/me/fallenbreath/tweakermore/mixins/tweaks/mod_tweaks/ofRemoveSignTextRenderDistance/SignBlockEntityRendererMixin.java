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

package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.ofRemoveSignTextRenderDistance;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(require = @Condition(ModIds.optifine))
@Mixin(SignRenderer.class)
public abstract class SignBlockEntityRendererMixin
{
	@Dynamic("Added by optifine")
	@ModifyExpressionValue(
			method = "isRenderText",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/renderer/blockentity/SignRenderer;textRenderDistanceSq:D",
					remap = true
			),
			remap = false
	)
	private static double ofDisableSignTextRenderDistance(double signTextRenderDistance)
	{
		if (TweakerMoreConfigs.OF_REMOVE_SIGN_TEXT_RENDER_DISTANCE.getBooleanValue())
		{
			signTextRenderDistance = Double.MAX_VALUE;
		}
		return signTextRenderDistance;
	}
}
