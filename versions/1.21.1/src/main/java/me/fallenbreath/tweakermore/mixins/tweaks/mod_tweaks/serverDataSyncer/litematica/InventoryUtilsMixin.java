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

package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.serverDataSyncer.litematica;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mod_tweaks.serverDataSyncer.ServerDataSyncer;
import me.fallenbreath.tweakermore.util.ModIds;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.registry.RegistryWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import fi.dy.masa.litematica.util.InventoryUtils;

@Restriction(require = @Condition(ModIds.litematica))
@Mixin(InventoryUtils.class)
public abstract class InventoryUtilsMixin
{
    @ModifyReceiver(
            method = "getTargetInventoryFromBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/entity/BlockEntity;createNbtWithIdentifyingData(Lnet/minecraft/registry/RegistryWrapper$WrapperLookup;)Lnet/minecraft/nbt/NbtCompound;"
            )
    )
    private static BlockEntity serverDataSyncer4InventoryOverlay_blockEntity(BlockEntity blockEntity, RegistryWrapper.WrapperLookup wrapperLookup)
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
}
