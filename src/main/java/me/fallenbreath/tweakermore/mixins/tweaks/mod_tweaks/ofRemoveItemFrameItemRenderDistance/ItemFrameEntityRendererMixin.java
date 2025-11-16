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

package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.ofRemoveItemFrameItemRenderDistance;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(require = @Condition(ModIds.optifine))
@Mixin(ItemFrameRenderer.class)
public abstract class ItemFrameEntityRendererMixin
{
	@Dynamic("Added by optifine")
	@ModifyExpressionValue(
			method = "isRenderItem",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/renderer/entity/ItemFrameRenderer;itemRenderDistanceSq:D",
					remap = true
			),
			remap = false
	)
	private double ofDisableItemFrameItemRenderDistance(double itemRenderDistance)
	{
		if (TweakerMoreConfigs.OF_REMOVE_ITEM_FRAME_ITEM_RENDER_DISTANCE.getBooleanValue())
		{
			itemRenderDistance = Double.MAX_VALUE;
		}
		return itemRenderDistance;
	}
}
