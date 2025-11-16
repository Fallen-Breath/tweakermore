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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.nameTagRenderStrategy;

import com.llamalad7.mixinextras.sugar.Local;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.listentries.RestrictionType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

//#if MC >= 1.21.9
//$$ import me.fallenbreath.tweakermore.impl.mc_tweaks.nameTagRenderStrategy.PlayerEntityRenderStateWithPlayerProfileName;
//#endif

//#if MC >= 12103
//$$ import net.minecraft.client.renderer.entity.state.EntityRenderState;
//$$ import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
//#endif

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin
		//#if MC >= 12103
		//$$ <T extends Entity, S extends EntityRenderState>
		//#endif
{
	@Inject(
			//#if MC >= 11500
			method = "renderNameTag",
			//#else
			//$$ method = "renderNameTag(Lnet/minecraft/world/entity/Entity;Ljava/lang/String;DDDI)V",
			//#endif
			at = @At("HEAD"),
			cancellable = true
	)
	private void nameTagRenderStrategy(
			CallbackInfo ci,
			//#if MC >= 12103
			//$$ @Local(argsOnly = true) S entityState
			//#else
			@Local(argsOnly = true) Entity entity
			//#endif
	)
	{
		RestrictionType strategyType = (RestrictionType)TweakerMoreConfigs.PLAYER_NAME_TAG_RENDER_STRATEGY_TYPE.getOptionListValue();
		if (
				strategyType != RestrictionType.NONE &&
				//#if MC >= 12103
				//$$ entityState instanceof PlayerEntityRenderState playerEntityRenderState
				//#else
				entity instanceof Player
				//#endif
		)
		{
			//#if MC >= 12109
			//$$ String playerName = ((PlayerEntityRenderStateWithPlayerProfileName)playerEntityRenderState).getPlayerProfileName$TKM();
			//#elseif MC >= 12103
			//$$ String playerName = playerEntityRenderState.name;
			//#else
			String playerName = ((Player)entity).getGameProfile().getName();
			//#endif
			if (playerName == null)
			{
				// not a player (mc1.21.9+)
				return;
			}

			List<String> list = TweakerMoreConfigs.PLAYER_NAME_TAG_RENDER_STRATEGY_LIST.getStrings();
			boolean shouldRender = strategyType.testEquality(playerName, list);
			if (!shouldRender)
			{
				ci.cancel();
			}
		}
	}
}
