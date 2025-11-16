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

package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.serverDataSyncer.tweakeroo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mod_tweaks.serverDataSyncer.ServerDataSyncer;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(require = @Condition(ModIds.tweakeroo))
@Mixin(RenderUtils.class)
public abstract class RenderUtilsMixin
{
	@ModifyExpressionValue(
			method = "renderInventoryOverlay",
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/malilib/util/InventoryUtils;getInventory(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/Container;",
					ordinal = 0,
					remap = true
			),
			remap = false
	)
	private static Container serverDataSyncer4InventoryOverlay_blockEntity(Container inventory)
	{
		if (TweakerMoreConfigs.SERVER_DATA_SYNCER.getBooleanValue())
		{
			if (!Minecraft.getInstance().hasSingleplayerServer())
			{
				ServerDataSyncer.getInstance().syncBlockInventory(inventory);
			}
		}
		return inventory;
	}

	@ModifyExpressionValue(
			method = "renderInventoryOverlay",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/phys/EntityHitResult;getEntity()Lnet/minecraft/world/entity/Entity;",
					remap = true
			),
			remap = false
	)
	private static Entity serverDataSyncer4InventoryOverlay_entity(Entity entity)
	{
		if (TweakerMoreConfigs.SERVER_DATA_SYNCER.getBooleanValue())
		{
			if (!Minecraft.getInstance().hasSingleplayerServer())
			{
				ServerDataSyncer.getInstance().syncEntity(entity, false);
			}
		}
		return entity;
	}
}
