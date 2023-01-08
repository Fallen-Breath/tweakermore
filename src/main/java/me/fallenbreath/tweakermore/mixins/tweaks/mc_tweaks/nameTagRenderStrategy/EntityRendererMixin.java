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

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.listentries.RestrictionType;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

//#if MC >= 11500
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
//#endif

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin
{
	@Inject(
			//#if MC >= 11500
			method = "renderLabelIfPresent",
			//#else
			//$$ method = "renderLabel(Lnet/minecraft/entity/Entity;Ljava/lang/String;DDDI)V",
			//#endif
			at = @At("HEAD"),
			cancellable = true
	)
	private void nameTagRenderStrategy(
			//#if MC >= 11500
			// the text arg is String in 15 and Text in 16+, so just @Coerce here for lazyness
			Entity entity, @Coerce Object text, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i,
			//#else
			//$$ Entity entity, String text, double x, double y, double z, int maxDistance,
			//#endif
			CallbackInfo ci
	)
	{
		RestrictionType strategyType = (RestrictionType)TweakerMoreConfigs.PLAYER_NAME_TAG_RENDER_STRATEGY_TYPE.getOptionListValue();
		if (strategyType != RestrictionType.NONE && entity instanceof PlayerEntity)
		{
			String playerName = ((PlayerEntity)entity).getGameProfile().getName();
			List<String> list = TweakerMoreConfigs.PLAYER_NAME_TAG_RENDER_STRATEGY_LIST.getStrings();
			boolean shouldRender = strategyType.testEquality(playerName, list);
			if (!shouldRender)
			{
				ci.cancel();
			}
		}
	}
}
