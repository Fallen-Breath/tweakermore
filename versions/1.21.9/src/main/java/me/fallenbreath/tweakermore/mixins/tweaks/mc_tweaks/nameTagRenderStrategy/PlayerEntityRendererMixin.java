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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.nameTagRenderStrategy;

import com.llamalad7.mixinextras.sugar.Local;
import me.fallenbreath.tweakermore.impl.mc_tweaks.nameTagRenderStrategy.PlayerEntityRenderStateWithPlayerProfileName;
import net.minecraft.client.network.ClientPlayerLikeEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin<AvatarlikeEntity extends PlayerLikeEntity & ClientPlayerLikeEntity>
{
	@Inject(
			method = "updateRenderState(Lnet/minecraft/entity/PlayerLikeEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V",
			at = @At("TAIL")
	)
	private void nameTagRenderStrategy_attachPlayerProfileName(
			CallbackInfo ci,
			@Local(argsOnly = true) AvatarlikeEntity playerLikeEntity,
			@Local(argsOnly = true) PlayerEntityRenderState playerEntityRenderState
	)
	{
		String playerProfileName = null;
		if (playerLikeEntity instanceof PlayerEntity playerEntity)
		{
			playerProfileName = playerEntity.getGameProfile().name();
		}
		((PlayerEntityRenderStateWithPlayerProfileName)playerEntityRenderState).setPlayerProfileName$TKM(playerProfileName);
	}
}
