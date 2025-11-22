/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024  Fallen_Breath and contributors
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

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.sugar.Local;
import fi.dy.masa.tweakeroo.renderer.InventoryOverlayHandler;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mod_tweaks.serverDataSyncer.ServerDataSyncer;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.HolderLookup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

//#if MC >= 1.21.11
//$$ import fi.dy.masa.malilib.util.data.tag.CompoundData;
//#else
import net.minecraft.nbt.CompoundTag;
//#endif

@Restriction(require = @Condition(ModIds.tweakeroo))
@Mixin(InventoryOverlayHandler.class)
public abstract class InventoryOverlayHandlerMixin
{
	@ModifyReceiver(
			method = "getTargetInventoryFromBlock",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/entity/BlockEntity;saveWithFullMetadata(Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/nbt/CompoundTag;"
			)
	)
	private BlockEntity serverDataSyncer4InventoryOverlay_blockEntity(BlockEntity blockEntity, HolderLookup.Provider wrapperLookup)
	{
		if (TweakerMoreConfigs.SERVER_DATA_SYNCER.getBooleanValue())
		{
			if (blockEntity != null)
			{
				ServerDataSyncer.getInstance().syncBlockEntity(blockEntity);
			}
		}
		return blockEntity;
	}

	@ModifyVariable(method = "getTargetInventoryFromEntity", at = @At("HEAD"), argsOnly = true)
	private Entity serverDataSyncer4InventoryOverlay_entity(
			Entity entity,
			//#if MC >= 1.21.11
			//$$ @Local(argsOnly = true) CompoundData nbt
			//#else
			@Local(argsOnly = true) CompoundTag nbt
			//#endif
	)
	{
		if (TweakerMoreConfigs.SERVER_DATA_SYNCER.getBooleanValue())
		{
			// if nbt != null, tweakeroo itself has already fetched the entity data from wherever else
			if (nbt == null)
			{
				ServerDataSyncer.getInstance().syncEntity(entity, false);
			}
		}
		return entity;
	}
}
