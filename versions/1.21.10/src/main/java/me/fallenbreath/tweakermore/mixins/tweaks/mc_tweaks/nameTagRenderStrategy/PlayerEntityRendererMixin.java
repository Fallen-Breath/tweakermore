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
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin<AvatarlikeEntity extends Avatar & ClientAvatarEntity>
{
	@Inject(
			method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
			at = @At("TAIL")
	)
	private void nameTagRenderStrategy_attachPlayerProfileName(
			CallbackInfo ci,
			@Local(argsOnly = true) AvatarlikeEntity playerLikeEntity,
			@Local(argsOnly = true) PlayerEntityRenderState playerEntityRenderState
	)
	{
		String playerProfileName = null;
		if (playerLikeEntity instanceof Player playerEntity)
		{
			playerProfileName = playerEntity.getGameProfile().name();
		}
		((PlayerEntityRenderStateWithPlayerProfileName)playerEntityRenderState).setPlayerProfileName$TKM(playerProfileName);
	}
}
