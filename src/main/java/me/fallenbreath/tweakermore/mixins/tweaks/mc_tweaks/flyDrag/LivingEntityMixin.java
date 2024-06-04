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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.flyDrag;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.EntityUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity
{
	public LivingEntityMixin(EntityType<?> type, World world)
	{
		super(type, world);
	}

	@ModifyVariable(
			method = "travel",
			at = @At(
					value = "INVOKE",
					//#if MC >= 11700
					//$$ target = "Lnet/minecraft/entity/LivingEntity;applyMovementInput(Lnet/minecraft/util/math/Vec3d;F)Lnet/minecraft/util/math/Vec3d;"
					//#elseif MC >= 11600
					//$$ target = "Lnet/minecraft/entity/LivingEntity;method_26318(Lnet/minecraft/util/math/Vec3d;F)Lnet/minecraft/util/math/Vec3d;"
					//#elseif MC >= 11500
					target = "Lnet/minecraft/entity/LivingEntity;applyClimbingSpeed(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"
					//#else
					//$$ target = "Lnet/minecraft/entity/LivingEntity;method_18801(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"
					//#endif
			),
			ordinal = 1
	)
	private float creativeFlyDrag(float dragFactor)
	{
		LivingEntity self = (LivingEntity)(Object)this;
		if (self == MinecraftClient.getInstance().player && TweakerMoreConfigs.FLY_DRAG.isModified())
		{
			//#if MC >= 12000
			//$$ boolean onGround = this.isOnGround();
			//#else
			boolean onGround = this.onGround;
			//#endif
			if (EntityUtil.isFlyingCreativePlayer(self) && !onGround)
			{
				dragFactor = (float)(1.0 - TweakerMoreConfigs.FLY_DRAG.getDoubleValue());
			}
		}
		return dragFactor;
	}
}
