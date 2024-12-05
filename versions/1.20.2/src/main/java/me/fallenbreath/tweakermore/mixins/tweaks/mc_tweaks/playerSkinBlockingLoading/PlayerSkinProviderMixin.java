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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.playerSkinBlockingLoading;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.playerSkinBlockingLoading.TaskSynchronizer;
import me.fallenbreath.tweakermore.util.ModIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.CompletableFuture;

/**
 * Custom skin loader mod @Overwrite / @Inject cancel onto the loadSkin method
 * So don't apply mixin when that mod is loaded
 * <p>
 * Versions:
 *   mc1.19.3 ~ mc1.20.1: subproject 1.15.2 (main project)
 *   >= mc1.20.2        : subproject 1.20.2        <--------
 */
@Restriction(conflict = @Condition(ModIds.custom_skin_loader))
@Mixin(targets = "net/minecraft/client/texture/PlayerSkinProvider$1")  // the CacheLoader subclass in PlayerSkinProvider's constructor
public abstract class PlayerSkinProviderMixin
{
	@ModifyExpressionValue(
			method = "load(Lnet/minecraft/client/texture/PlayerSkinProvider$Key;)Ljava/util/concurrent/CompletableFuture;",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/concurrent/CompletableFuture;supplyAsync(Ljava/util/function/Supplier;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"
			)
	)
	private CompletableFuture<?> playerSkinBlockingLoading_blockingProfileFetching(CompletableFuture<?> completableFuture)
	{
		if (TweakerMoreConfigs.PLAYER_SKIN_BLOCKING_LOADING.getBooleanValue())
		{
			completableFuture = TaskSynchronizer.createSyncedFuture(completableFuture);
		}
		return completableFuture;
	}
}
