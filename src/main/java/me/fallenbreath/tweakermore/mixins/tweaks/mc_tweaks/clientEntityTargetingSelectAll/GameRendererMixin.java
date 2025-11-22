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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.clientEntityTargetingSelectAll;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.clientEntityTargetingSelectAll.MinecraftClientWithExtendedTargetEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Predicate;

/**
 * mc1.21.10- : subproject 1.15.2 (main project)        <--------
 * mc1.21.11+ : subproject 1.21.11
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin
{
	@Shadow @Final private Minecraft minecraft;

	@WrapOperation(
			//#if MC >= 12006
			//$$ method = "pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;",
			//#else
			method = "pick",
			//#endif
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/projectile/ProjectileUtil;getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;"
			)
	)
	private @Nullable EntityHitResult clientEntityTargetingSelectAll_makeExtendedTarget(Entity entity, Vec3 vecStart, Vec3 vecEnd, AABB box, Predicate<Entity> predicate, double reach, Operation<EntityHitResult> original)
	{
		MinecraftClientWithExtendedTargetEntity access = (MinecraftClientWithExtendedTargetEntity)this.minecraft;
		if (TweakerMoreConfigs.CLIENT_ENTITY_TARGETING_SUPPORT_ALL.getBooleanValue())
		{
			access.setExtendedEntityHitResult$TKM(original.call(entity, vecStart, vecEnd, box, (Predicate<Entity>)e -> true, reach));
		}
		else
		{
			access.setExtendedEntityHitResult$TKM(null);
		}
		return original.call(entity, vecStart, vecEnd, box, predicate, reach);
	}
}
