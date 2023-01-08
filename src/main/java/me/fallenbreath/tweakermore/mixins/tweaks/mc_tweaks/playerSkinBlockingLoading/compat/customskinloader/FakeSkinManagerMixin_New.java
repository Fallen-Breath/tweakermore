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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.playerSkinBlockingLoading.compat.customskinloader;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.playerSkinBlockingLoading.TaskSynchronizer;
import me.fallenbreath.tweakermore.util.ModIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * The way to submit Runnable in {@link customskinloader.fake.FakeSkinManager#loadProfileTextures}
 *   >=14.15-SNAPSHOT-350: {@code CustomSkinLoader.loadProfileTextures()}, static
 *   >=14.14-SNAPSHOT-336 <14.15-SNAPSHOT-350: {@code CustomSkinLoader.loadProfileTextures()}    <--------
 *   >14.11 <14.14-SNAPSHOT-336: {@code THREAD_POOL.execute()}
 *   <=14.11: {@code THREAD_POOL.submit()}
 */
@Restriction(require = @Condition(value = ModIds.custom_skin_loader, versionPredicates = ">=14.14-SNAPSHOT-336 <14.15-SNAPSHOT-350"))
@Pseudo
@Mixin(targets = "customskinloader.fake.FakeSkinManager")
public abstract class FakeSkinManagerMixin_New
{
	@SuppressWarnings("UnresolvedMixinReference")
	@ModifyArg(
			method = "loadProfileTextures",
			at = @At(
					value = "INVOKE",
					target = "Lcustomskinloader/CustomSkinLoader;loadProfileTextures(Ljava/lang/Runnable;)V"
			),
			remap = false
	)
	private Runnable playerSkinBlockingLoading_blockingProfileFetching(Runnable runnable)
	{
		if (TweakerMoreConfigs.PLAYER_SKIN_BLOCKING_LOADING.getBooleanValue())
		{
			runnable = TaskSynchronizer.createSyncedTask(runnable);
		}
		return runnable;
	}
}
