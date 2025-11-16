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

package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.serverDataSyncer.litematica;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import fi.dy.masa.litematica.render.OverlayRenderer;
import fi.dy.masa.malilib.util.InventoryUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mod_tweaks.serverDataSyncer.ServerDataSyncer;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.Container;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(require = @Condition(ModIds.litematica))
@Mixin(OverlayRenderer.class)
public abstract class OverlayRendererMixin
{
	@ModifyExpressionValue(
			method = "renderBlockInfoOverlay",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/phys/BlockHitResult;getBlockPos()Lnet/minecraft/core/BlockPos;",
					remap = true
			),
			remap = false
	)
	private BlockPos serverDataSyncer4InfoOverlay(
			BlockPos pos,
			@Local(ordinal = 1) Level worldClient
	)
	{
		if (TweakerMoreConfigs.SERVER_DATA_SYNCER.getBooleanValue())
		{
			if (worldClient instanceof ClientLevel)
			{
				Container inventory = InventoryUtils.getInventory(worldClient, pos);
				if (inventory != null)
				{
					ServerDataSyncer.getInstance().syncBlockInventory(inventory);
				}
			}
		}
		return pos;
	}
}
