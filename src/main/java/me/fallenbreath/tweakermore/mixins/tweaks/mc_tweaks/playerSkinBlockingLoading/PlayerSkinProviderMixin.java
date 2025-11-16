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

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.playerSkinBlockingLoading.TaskSynchronizer;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.resources.SkinManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//#if MC >= 11500
import org.spongepowered.asm.mixin.injection.ModifyVariable;
//#else
//$$ import org.spongepowered.asm.mixin.injection.ModifyArg;
//#endif

/**
 * Custom skin loader mod @Overwrite / @Inject cancel onto the loadSkin method
 * So don't apply mixin when that mod is loaded
 * <p>
 * Versions:
 *   mc1.19.3 ~ mc1.20.1: subproject 1.15.2 (main project)        <--------
 *   >= mc1.20.2        : subproject 1.20.2
 */
@Restriction(conflict = @Condition(ModIds.custom_skin_loader))
@Mixin(SkinManager.class)
public abstract class PlayerSkinProviderMixin
{
	//#if MC >= 11500
	@ModifyVariable(
	//#else
	//$$ @ModifyArg(
	//#endif
			method = "registerSkins(Lcom/mojang/authlib/GameProfile;Lnet/minecraft/client/resources/SkinManager$SkinTextureCallback;Z)V",
			at = @At(
					value = "INVOKE",
					//#if MC >= 11800
					//$$ target = "Lnet/minecraft/Util;backgroundExecutor()Ljava/util/concurrent/ExecutorService;"
					//#elseif MC >= 11500
					target = "Lnet/minecraft/Util;backgroundExecutor()Ljava/util/concurrent/Executor;"
					//#else
					//$$ target = "Ljava/util/concurrent/ExecutorService;submit(Ljava/lang/Runnable;)Ljava/util/concurrent/Future;"
					//#endif
			)
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
