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

import fi.dy.masa.tweakeroo.renderer.RenderUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mod_tweaks.serverDataSyncer.serverDataSyncer.ServerDataSyncer;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Restriction(require = @Condition(ModIds.tweakeroo))
@Mixin(RenderUtils.class)
public abstract class RenderUtilsMixin
{
	@ModifyVariable(
			method = "renderInventoryOverlay",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lfi/dy/masa/malilib/util/InventoryUtils;getInventory(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/inventory/Inventory;",
					ordinal = 0,
					remap = true
			),
			remap = false
	)
	private static Inventory serverDataSyncer4InventoryOverlay(Inventory inventory)
	{
		if (TweakerMoreConfigs.SERVER_DATA_SYNCER.getBooleanValue())
		{
			if (!MinecraftClient.getInstance().isIntegratedServerRunning())
			{
				ServerDataSyncer.getInstance().syncBlockInventory(inventory);
			}
		}
		return inventory;
	}

	@ModifyVariable(
			method = "renderInventoryOverlay",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lnet/minecraft/util/hit/EntityHitResult;getEntity()Lnet/minecraft/entity/Entity;",
					remap = true
			),
			//#if MC >= 11700
			//$$ ordinal = 1,
			//#endif
			remap = false
	)
	private static Entity serverDataSyncer4InventoryOverlay(Entity entity)
	{
		if (TweakerMoreConfigs.SERVER_DATA_SYNCER.getBooleanValue())
		{
			if (!MinecraftClient.getInstance().isIntegratedServerRunning())
			{
				ServerDataSyncer.getInstance().syncEntity(entity);
			}
		}
		return entity;
	}
}
