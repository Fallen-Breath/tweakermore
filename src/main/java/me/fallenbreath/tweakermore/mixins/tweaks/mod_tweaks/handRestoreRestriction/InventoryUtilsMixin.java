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

package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.handRestoreRestriction;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Restriction(require = @Condition(ModIds.tweakeroo))
@Mixin(InventoryUtils.class)
public abstract class InventoryUtilsMixin
{
	@ModifyExpressionValue(
			method = "preRestockHand",
			slice = @Slice(
					from = @At(
							value = "FIELD",
							target = "Lfi/dy/masa/tweakeroo/config/FeatureToggle;TWEAK_HAND_RESTOCK:Lfi/dy/masa/tweakeroo/config/FeatureToggle;",
							remap = false
					)
			),
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/tweakeroo/config/FeatureToggle;getBooleanValue()Z",
					ordinal = 0,
					remap = false
			),
			remap = false
	)
	private static boolean applyHandRestoreRestriction(boolean booleanValue, /* parent method parameters -> */ PlayerEntity player, Hand hand, boolean allowHotbar)
	{
		Item currentItem = player.getStackInHand(hand).getItem();
		return booleanValue && TweakerMoreConfigs.HAND_RESTORE_RESTRICTION.isAllowed(currentItem);
	}
}
