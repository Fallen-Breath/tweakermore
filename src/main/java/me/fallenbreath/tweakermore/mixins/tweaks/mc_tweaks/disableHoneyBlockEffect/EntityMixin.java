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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HoneyBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = ">=1.15"))
@Mixin(Entity.class)
public abstract class EntityMixin
{
	@WrapOperation(
			method = "getBlockJumpFactor",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/Block;getJumpFactor()F"
			)
	)
	private float disableHoneyBlockEffect_modifyHoneyBlockJumpVelocityMultiplier(Block block, Operation<Float> original)
	{
		return adjustVelocityGetterResult(block, original, 1.0F);
	}

	@WrapOperation(
			method = "getBlockSpeedFactor",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/Block;getSpeedFactor()F"
			)
	)
	private float disableHoneyBlockEffect_modifyHoneyBlockVelocityMultiplier(Block block, Operation<Float> original)
	{
		return adjustVelocityGetterResult(block, original, 1.0F);
	}

	@Unique
	private float adjustVelocityGetterResult(Block block, Operation<Float> getter, float override)
	{
		float ret = getter.call(block);
		if (TweakerMoreConfigs.DISABLE_HONEY_BLOCK_EFFECT.getBooleanValue())
		{
			Entity self = (Entity)(Object)this;
			if (block instanceof HoneyBlock && self == Minecraft.getInstance().player)
			{
				ret = override;
			}
		}
		return ret;
	}
}
