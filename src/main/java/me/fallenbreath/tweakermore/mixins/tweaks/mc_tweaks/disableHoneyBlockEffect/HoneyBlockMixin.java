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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableHoneyBlockEffect;

import com.llamalad7.mixinextras.sugar.Local;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.world.level.block.HoneyBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = ">=1.15"))
@Mixin(HoneyBlock.class)
public abstract class HoneyBlockMixin
{
	/**
	 * The signature of net.minecraft.block.HoneyBlock#onEntityCollision was changed in mc1.21.10.
	 * If we choose to not`@ModifyExpressionValue` at `isSliding` inside `onEntityCollision`,
	 * we can have a seamless compatibility between mc1.21.9 and mc1.21.10
	 */
	@Inject(method = "isSlidingDown", at = @At("HEAD"), cancellable = true)
	private void disableHoneyBlockEffect_isSlidingHack(CallbackInfoReturnable<Boolean> cir, @Local(argsOnly = true) Entity entity)
	{
		if (TweakerMoreConfigs.DISABLE_HONEY_BLOCK_EFFECT.getBooleanValue() && entity == Minecraft.getInstance().player)
		{
			cir.setReturnValue(false);
		}
	}
}
