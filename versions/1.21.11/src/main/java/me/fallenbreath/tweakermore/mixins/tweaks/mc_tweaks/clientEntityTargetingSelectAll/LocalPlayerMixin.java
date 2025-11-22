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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.clientEntityTargetingSelectAll;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.clientEntityTargetingSelectAll.MinecraftClientWithExtendedTargetEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

/**
 * mc1.21.10- : subproject 1.15.2 (main project)
 * mc1.21.11+ : subproject 1.21.11        <--------
 */
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends Player
{
	public LocalPlayerMixin(Level level, GameProfile gameProfile)
	{
		super(level, gameProfile);
	}

	@Shadow
	@Final
	protected Minecraft minecraft;

	@Shadow
	private static HitResult filterHitResult(HitResult hitResult, Vec3 from, double maxRange)
	{
		throw new AssertionError();
	}

	@Shadow
	private static HitResult pick(Entity cameraEntity, double blockInteractionRange, double entityInteractionRange, float partialTicks)
	{
		return null;
	}

	@Unique private static final Predicate<Entity> extendedPredicateThatAcceptsAll = e -> true;
	@Unique private static final ThreadLocal<Boolean> shouldUseTheExtendedPredicate = ThreadLocal.withInitial(() -> false);

	@Inject(
			method = "raycastHitResult",
			at = @At("TAIL")
	)
	private void clientEntityTargetingSelectAll_makeExtendedTarget1(
			CallbackInfoReturnable<HitResult> cir,
			@Local(argsOnly = true) float partialTicks,
			@Local(argsOnly = true) Entity cameraEntity,
			@Local AttackRange itemAttackRange,
			@Local double blockInteractionRange
	)
	{
		MinecraftClientWithExtendedTargetEntity access = (MinecraftClientWithExtendedTargetEntity)this.minecraft;
		access.setExtendedEntityHitResult$TKM(null);

		if (TweakerMoreConfigs.CLIENT_ENTITY_TARGETING_SUPPORT_ALL.getBooleanValue())
		{
			// small copy of `raycastHitResult`, but is using our predicate
			HitResult extendedhitResult = null;
			if (itemAttackRange != null)
			{
				extendedhitResult = itemAttackRange.getClosesetHit(cameraEntity, partialTicks, extendedPredicateThatAcceptsAll);
				if (extendedhitResult instanceof BlockHitResult)
				{
					extendedhitResult = filterHitResult(extendedhitResult, cameraEntity.getEyePosition(partialTicks), blockInteractionRange);
				}
			}

			if (extendedhitResult == null || extendedhitResult.getType() == HitResult.Type.MISS)
			{
				shouldUseTheExtendedPredicate.set(true);
				extendedhitResult = pick(cameraEntity, blockInteractionRange, this.entityInteractionRange(), partialTicks);
				shouldUseTheExtendedPredicate.remove();
			}

			if (extendedhitResult instanceof EntityHitResult entityHitResult)
			{
				access.setExtendedEntityHitResult$TKM(entityHitResult);
			}
		}
	}

	@ModifyArg(
			method = "pick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/projectile/ProjectileUtil;getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;"
			)
	)
	private static Predicate<Entity> clientEntityTargetingSelectAll_makeExtendedTarget2(Predicate<Entity> predicate)
	{
		if (TweakerMoreConfigs.CLIENT_ENTITY_TARGETING_SUPPORT_ALL.getBooleanValue())
		{
			if (shouldUseTheExtendedPredicate.get())
			{
				predicate = extendedPredicateThatAcceptsAll;
			}
		}
		return predicate;
	}
}
